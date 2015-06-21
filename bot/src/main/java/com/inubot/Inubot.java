package com.inubot;

import com.inubot.api.methods.Client;
import com.inubot.api.methods.Game;
import com.inubot.api.util.CacheLoader;
import com.inubot.api.util.Random;
import com.inubot.api.util.Time;
import com.inubot.bot.irc.IRCConnection;
import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.modscript.transform.*;
import com.inubot.bot.util.Configuration;
import com.inubot.bot.util.ProxyUtils;
import com.inubot.bot.util.RSClassLoader;
import com.inubot.client.GameCanvas;
import com.inubot.script.bundled.NMZAfker;
import com.inubot.script.bundled.motherlode.MotherloadMine;
import com.inubot.script.others.Powermine;
import com.inubot.script.bundled.combattrainer.CombatTrainerPRO;
import com.inubot.script.bundled.hunter.BirdSnarePRO;
import com.inubot.script.bundled.hunter.FalconryPRO;
import com.inubot.script.bundled.hunter.RedChinsPRO;
import temp.account.Account;
import temp.account.AccountManager;
import com.inubot.script.bundled.rangeguild.RangeGuild;
import temp.farm.irc.IRC;
import com.inubot.bot.modscript.Injector;
import com.inubot.bot.ui.BotMenuBar;
import com.inubot.bot.ui.BotToolBar;
import com.inubot.bot.util.io.Crawler;
import com.inubot.bot.util.io.JarNode;
import com.inubot.client.natives.RSClient;
import com.inubot.script.Script;
import com.inubot.script.ScriptFlux;
import com.inubot.script.bundled.agility.PerfectAgility;
import com.inubot.script.bundled.tutisland.TutorialIsland;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

/**
 * @author unsigned
 * @since 20-04-2015
 */
public class Inubot extends JFrame implements Runnable {

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
            //RangeGuild.class,
            MotherloadMine.class,
            Powermine.class,
            NMZAfker.class
            //PestControl.class,
            //WineMeker.class,
            //Fletcher.class,
            //ChatTest.class
    };

    private static Inubot instance;
    private static boolean useProxy = false;
    private static String autoStartScript = null;
    private final Crawler crawler;
    private final ScriptFlux scriptFlux;
    private final IRCConnection connection;
    private RSClient client;
    private BotToolBar toolBar;

    public Inubot() {
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

        connection = new IRCConnection("irc.rizon.net", 6667);
        new Thread(connection).start();
    }

    public static Inubot getInstance() {
        return instance;
    }


    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-proxy")) {
                String username = args[i + 1];
                String password = args[i + 2];
                String address = args[i + 3];
                String port = args[i + 4];

                Authenticator authenticator = new Authenticator() {

                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password.toCharArray());
                    }
                };
                System.setProperty("java.net.socks.username", username);
                System.setProperty("java.net.socks.password", password);
                System.setProperty("socksProxyHost", address);
                System.setProperty("socksProxyPort", port);
                java.net.Authenticator.setDefault(authenticator);
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

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                instance = new Inubot();
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

    public IRCConnection getConnection() { return connection; }

    public ScriptFlux getScriptFlux() {
        return scriptFlux;
    }

    public BotToolBar getToolBar() {
        return toolBar;
    }
}
