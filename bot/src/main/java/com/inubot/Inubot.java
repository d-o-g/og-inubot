package com.inubot;

import com.inubot.api.methods.Client;
import com.inubot.api.methods.Game;
import com.inubot.api.util.CacheLoader;
import com.inubot.api.util.Time;
import com.inubot.bot.Account;
import com.inubot.bot.AccountManager;
import com.inubot.bot.irc.IRCConnection;
import com.inubot.bot.modscript.Injector;
import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.modscript.transform.*;
import com.inubot.bot.ui.BotMenuBar;
import com.inubot.bot.util.*;
import com.inubot.bot.util.io.Crawler;
import com.inubot.bot.util.io.JarNode;
import com.inubot.client.GameCanvas;
import com.inubot.client.natives.RSClient;
import com.inubot.script.Script;
import com.inubot.script.ScriptFlux;
import com.inubot.script.bundled.money.Potato;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

    private static final Logger logger = LoggerFactory.getLogger(Inubot.class);

    public static final Class[] SCRIPT_CLASSES = new Class[]{
            Potato.class
    };

    private static Inubot instance;
    private static boolean useProxy = false;
    private static String autoStartScript = null;
    private final Crawler crawler;
    private final ScriptFlux scriptFlux;

    private static final String IRC_CHANNEL = "#bone-bots";

    private RSClient client;

    private final IRCConnection irc;

    public Inubot() {
        super(Configuration.APPLICATION_NAME);
        setBackground(Color.BLACK);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.scriptFlux = new ScriptFlux();
        this.crawler = new Crawler(Crawler.GameType.OSRS);
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        new Thread(this).start();
        irc = new IRCConnection();
        try {
            irc.connect("irc.foonetic.net");
            irc.joinChannel(IRC_CHANNEL);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                logger.info("Account is now " + user + " : " + pass);
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
    public BotMenuBar getJMenuBar() {
        return (BotMenuBar) super.getJMenuBar();
    }

    @Override
    public void run() {
        //add(toolBar, BorderLayout.NORTH);
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
            randomDat.setReadOnly();

        Injector injector = new Injector(new JarNode(new File(crawler.pack)));
        Collections.addAll(injector.getTransforms(), new EngineTickCallback(),
                new ProcessActionCallback(), new ProcessActionInvoker(), new InterfaceImpl(),
                new ModelHack(), new CanvasHack(), /*new WidgetHack(),*/ new GetterAdder(),
                new InvokerTransform(), new IdleTimeSetter(), new HoveredRegionTileSetter(),
                new GroundItemPosition(), new MessageCallback(), new UserDetailsSetter(),
                new VarpBitHack(), new LandscapeHack(), new LowMemorySetter(), new CatchBlockSweeper());
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

        if (instance == null)
            instance = this;

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
                for (Hotkey cb : Hotkey.values()) {
                    if (e.getKeyCode() == cb.getKey())
                        cb.onActivation();
                }
            }
        });
    }

    public IRCConnection getIRCConnection() {
        return irc;
    }

    public ScriptFlux getScriptFlux() {
        return scriptFlux;
    }
}
