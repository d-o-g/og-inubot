/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot;

import com.inubot.api.event.EventBus;
import com.inubot.api.event.concurrent.SynchronousEventBus;
import com.inubot.api.methods.Game;
import com.inubot.api.util.Time;
import com.inubot.bot.account.Account;
import com.inubot.bot.account.AccountManager;
import com.inubot.bot.modscript.Injector;
import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.net.irc.IRCConnection;
import com.inubot.bot.ui.BotMenuBar;
import com.inubot.bot.ui.LogPane;
import com.inubot.bot.util.*;
import com.inubot.bot.util.io.Crawler;
import com.inubot.bot.util.io.JarNode;
import com.inubot.client.GameCanvas;
import com.inubot.client.natives.ClientNative;
import com.inubot.script.ScriptFlux;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public abstract class Bot<Client extends ClientNative> extends JFrame implements Runnable {

    private static final String IRC_CHANNEL = "#inubot";
    protected static Bot instance;
    private static boolean useProxy = false;
    protected final Crawler crawler;
    private final ScriptFlux scriptFlux;
    private final IRCConnection irc;
    private final LogPane logPane;
    private final EventBus eventBus;
    private Client client;

    public Bot() {
        super(Configuration.APPLICATION_NAME);
        setBackground(Color.BLACK);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.scriptFlux = new ScriptFlux();
        this.crawler = createCrawler();
        this.logPane = new LogPane();
        this.irc = new IRCConnection();
        this.eventBus = new SynchronousEventBus();
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        new Thread(this).start();
        try {
            irc.connect("irc.foonetic.net");
            irc.joinChannel(IRC_CHANNEL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends ClientNative> Bot<T> getInstance() {
        return instance;
    }

    @Override
    public void run() {
        BotMenuBar menuBar = new BotMenuBar();
        super.setJMenuBar(menuBar);
        crawler.crawl();
        if (crawler.isOutdated())
            crawler.download();
        try {
            ModScript.load(Files.readAllBytes(Paths.get(crawler.modscript)), Integer.toString(crawler.getHash()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse modscript", e);
        }

        File randomDat = new File(System.getProperty("user.home") + "/random.dat");
        if (randomDat.exists())
            randomDat.setReadOnly();

        File pack = new File(crawler.pack);
        Injector injector = initInjector(pack);;
        Map<String, byte[]> classes = injector.inject(false);

        RSClassLoader classloader = new RSClassLoader(classes);
        ModScript.setClassLoader(classloader);

        if (useProxy && ProxyUtils.useAliveUSProxy())
            setTitle(Configuration.APPLICATION_NAME + " - Proxy [" + ProxyUtils.getLastIP() + ":" + ProxyUtils.getLastPort() + "]");

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.setBackground(Color.BLACK);
        this.client = (Client) crawler.start(classloader);
        container.add(((Applet) client), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        if (instance == null) {
            instance = this;
        }

        while (Game.getState() < Game.STATE_CREDENTIALS_SCREEN || ((Applet) client).getComponentCount() == 0) {
            Time.sleep(100);
        }

        initCache(client);

        getCanvas().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!e.isControlDown())
                    return;
                for (Hotkey cb : Hotkey.values()) {
                    if (e.getKeyCode() == cb.getKey()) {
                        cb.onActivation();
                    }
                }
            }
        });
        if (instance == null) {
            instance = this;
        }
    }

    public Client getClient() {
        return client;
    }

    public GameCanvas getCanvas() {
        return ((Applet) client).getComponentCount() > 0 ? (GameCanvas) ((Applet) client).getComponent(0) : null;
    }

    @Override
    public BotMenuBar getJMenuBar() {
        return (BotMenuBar) super.getJMenuBar();
    }

    public IRCConnection getIRCConnection() {
        return irc;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public ScriptFlux getScriptFlux() {
        return scriptFlux;
    }

    public LogPane getLogPane() {
        return logPane;
    }

    protected abstract Injector initInjector(File pack);

    protected abstract void initCache(Client client);

    protected abstract Crawler createCrawler();

    protected void initArgs(String... args) {
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
            if (arg.equals("-world")) {
                Configuration.WORLD = Integer.parseInt(args[i + 1]);
            }
            if (arg.equals("-farm")) {
                com.inubot.api.methods.Client.GAME_TICK_SLEEP = 100;
                com.inubot.api.methods.Client.LANDSCAPE_RENDERING_ENABLED = false;
                com.inubot.api.methods.Client.MODEL_RENDERING_ENABLED = false;
            }
        }
    }
}
