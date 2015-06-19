package org.runedream;

import org.runedream.api.methods.*;
import org.runedream.api.util.*;
import org.runedream.bot.account.Account;
import org.runedream.bot.account.AccountManager;
import org.runedream.bot.farm.irc.IRC;
import org.runedream.bot.farm.shell.Shell;
import org.runedream.bot.modscript.Injector;
import org.runedream.bot.modscript.ModScript;
import org.runedream.bot.modscript.transform.*;
import org.runedream.bot.ui.BotMenuBar;
import org.runedream.bot.ui.BotToolBar;
import org.runedream.bot.util.*;
import org.runedream.bot.util.io.Crawler;
import org.runedream.bot.util.io.JarNode;
import org.runedream.client.GameCanvas;
import org.runedream.client.natives.RSClient;
import org.runedream.script.Script;
import org.runedream.script.ScriptFlux;
import org.runedream.script.memes.*;
import org.runedream.script.memes.agility.PerfectAgility;
import org.runedream.script.memes.hunter.*;
import org.runedream.script.memes.range.RangeGuild;
import org.runedream.script.memes.tutisland.TutorialIsland;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

/**
 * @author unsigned
 * @since 20-04-2015
 */
public class RuneDream extends JFrame implements Runnable {

    public static final Class[] SCRIPT_CLASSES = new Class[]{
            PerfectAgility.class,
            TutorialIsland.class,
            //AutoWalker.class,
            //Seagull.class,
            BirdSnarePRO.class,
            FalconryPRO.class,
            //CoolWidgetExplorer.class,
            //Crafter.class,
            RedChinsPRO.class,
            //PrayerFlick.class,
            CombatTrainerPRO.class,
            RangeGuild.class
            //PestControl.class,
            //WineMeker.class,
            //Fletcher.class,
            //ChatTest.class
    };

    private static RuneDream instance;
    private static boolean useProxy = false;
    private static String autoStartScript = null;
    private final Crawler crawler;
    private final ScriptFlux scriptFlux;
    private RSClient client;
    private BotToolBar toolBar;
    private ActionBar actionBar;

    public RuneDream() {
        super(Configuration.APPLICATION_NAME);
        setBackground(Color.BLACK);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.scriptFlux = new ScriptFlux();
        this.crawler = new Crawler(Crawler.GameType.OSRS);
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        new Thread(this).start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                //DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(Configuration.ACTIONS)));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    public static RuneDream getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-proxy")) {
                useProxy = true;
            }
            if (arg.equals("-account")) {
                String user = args[i + 1];
                String pass = args[i + 2];
                AccountManager.setCurrentAccount(new Account(user, pass));
                System.out.println("Account is now " + user + " : " + pass);
            }
            if (arg.equals("-script")) {
                String name = args[i + 1];
                autoStartScript = name.replace(".class", "");
            }
            if (arg.equals("-world")) {
                Configuration.WORLD = Integer.parseInt(args[i + 1]);
            }
            if (arg.equals("-farm")) {
                Client.GAME_TICK_SLEEP = 100;
                Client.LANDSCAPE_RENDERING_ENABLED = false;
                Client.MODEL_RENDERING_ENABLED = false;
            }
        }
        IRC.init("irc.rizon.net", 6667, "RDBot" + Random.nextInt(111111, 999999), "Septron", "rd-bot");
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                instance = new RuneDream();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public RSClient getClient() {
        return client;
    }

    public GameCanvas getCanvas() {
        return client.asApplet().getComponentCount() > 0 ? (GameCanvas) client.asApplet().getComponent(0) : null;
    }

    @Override
    public void run() {
        this.toolBar = new BotToolBar();
        add(toolBar, BorderLayout.NORTH);
        BotMenuBar menuBar = new BotMenuBar();
        super.setJMenuBar(menuBar);
        crawler.crawl();
        if (crawler.isOutdated())
            crawler.download();
        try {
            ModScript.load(Files.readAllBytes(Paths.get(crawler.modscript)), Integer.toString(crawler.getHash()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse modscript");
        }

        File randomDat = new File(System.getProperty("user.home") + "/random.dat");
        if (randomDat.exists())
            randomDat.delete();

        Injector injector = new Injector(new JarNode(new File(crawler.pack)));
        Collections.addAll(injector.getTransforms(), new EngineTickCallback(),
                new ProcessActionCallback(), new ProcessActionInvoker(), new InterfaceImpl(),
                new ModelHack(), new CanvasHack(), new WidgetPositionHack(), new GetterAdder(),
                new InvokerTransform(), new IdleTimeSetter(), new HoveredRegionTileSetter(),
                new GroundItemPosition(), new MessageCallback(), new UserDetailsSetter(),
                new VarpBitHack(), new LandscapeHack(), new LowMemorySetter()/*, new CharacterCallbacks()*/);
        Map<String, byte[]> classes = injector.inject();

        RSClassLoader classloader = new RSClassLoader(classes);
        ModScript.setClassLoader(classloader);

        if (useProxy && ProxyUtils.useAliveUSProxy())
            setTitle(Configuration.APPLICATION_NAME + " - Proxy [" + ProxyUtils.getLastIP() + ":" + ProxyUtils.getLastPort() + "]");

        Container container = getContentPane();
        container.setBackground(Color.BLACK);
        this.client = (RSClient) crawler.start(classloader);
        container.add(client.asApplet());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        while (Game.getState() < Game.STATE_CREDENTIALS_SCREEN || getClient().asApplet().getComponentCount() == 0)
            Time.sleep(100);

        if (autoStartScript != null) {
            try {
                for (Class clazz : SCRIPT_CLASSES) {
                    if (clazz.getSimpleName().equals(autoStartScript)) {
                        Script s = (Script) clazz.newInstance();
                        getScriptFlux().execute(s);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        CacheLoader.load(client);
        this.actionBar = new ActionBar();
        getCanvas().addMouseListener(actionBar);
        getCanvas().addMouseMotionListener(actionBar);
        getCanvas().addKeyListener(actionBar);
        getCanvas().paintables.add(actionBar);
        getCanvas().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!e.isControlDown())
                    return;
                for (CtrlBind cb : CtrlBind.values()) {
                    if (e.getKeyCode() == cb.getKey())
                        cb.onActivation();
                }
            }
        });
    }

    public ScriptFlux getScriptFlux() {
        return scriptFlux;
    }

    public BotToolBar getToolBar() {
        return toolBar;
    }

    public ActionBar getActionBar() {
        return actionBar;
    }
}
