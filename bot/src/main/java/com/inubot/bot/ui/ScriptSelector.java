package com.inubot.bot.ui;

import com.inubot.Bot;
import com.inubot.bot.util.CachedClassLoader;
import com.inubot.bot.util.Configuration;
import com.inubot.script.Manifest;
import com.inubot.script.loader.LocalScriptLoader;
import com.inubot.script.loader.RemoteScriptDefinition;
import com.inubot.script.loader.ScriptDefinition;
import com.inubot.script.loader.ScriptFilter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by bytehound on 7/28/2015.
 */
public class ScriptSelector extends JFrame {

    private final JPanel content = new JPanel();
    private final JScrollPane scrollPane = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    private java.util.List<Entity> entities = new ArrayList<>();

    private static final Class[] SCRIPT_CLASSES = {

    };

    public ScriptSelector() {
        super("Script Selector");

        LocalScriptLoader loader = new LocalScriptLoader();
        try {
            loader.parse(new File(Configuration.SCRIPTS));
            ScriptDefinition[] definitions = loader.getDefinitions();
            for (ScriptDefinition def : definitions) {
                entities.add(new Entity(def));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        CachedClassLoader remoteLoader = new CachedClassLoader(RemoteScriptDefinition.getNetworkedScriptDefinitions());
        ScriptFilter filter = new ScriptFilter();
        for (String name : RemoteScriptDefinition.getNetworkedScriptDefinitions().keySet()) {
            try {
                Class<?> clazz = remoteLoader.loadClass(name);
                if (filter.accept(clazz)) {
                    ScriptDefinition def = new ScriptDefinition(clazz.getAnnotation(Manifest.class));
                    def = new RemoteScriptDefinition(def.name(), def.developer(), def.desc(), def.version());
                    def.setScriptClass((Class<? extends com.inubot.script.Script>) clazz);
                    entities.add(new Entity(def));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Class<?> clazz : SCRIPT_CLASSES) {
            if (filter.accept(clazz)) {
                ScriptDefinition def = new ScriptDefinition(clazz.getAnnotation(Manifest.class));
                def = new RemoteScriptDefinition(def.name(), def.developer(), def.desc(), def.version());
                def.setScriptClass((Class<? extends com.inubot.script.Script>) clazz);
                entities.add(new Entity(def));
            }
        }

        content.setLayout(new GridLayout(entities.size(), 1));
        entities.forEach(content::add);
        scrollPane.setPreferredSize(new Dimension(320, 100));
        add(scrollPane);
        
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    class Entity extends JPanel {

        private final JButton initiate = new JButton("Initiate");
        private final JLabel title;

        public Entity(ScriptDefinition target) {
            title = new JLabel("   ".concat(target.name()));

            this.setLayout(new BorderLayout());
            this.setPreferredSize(new Dimension(300, 20));

            this.initiate.setFocusable(false);
            this.initiate.setToolTipText(target.desc());

            this.setToolTipText("Author: ".concat(target.developer()));

            this.add(title, BorderLayout.WEST);
            this.add(initiate, BorderLayout.EAST);

            this.initiate.addActionListener(e -> {
                try {
                    com.inubot.script.Script targetInstance = target.getScriptClass().newInstance();
                    Bot.getInstance().getScriptFlux().execute(targetInstance);
                    dispose();
                } catch (InstantiationException | IllegalAccessException e1) {
                    e1.printStackTrace();
                }
            });

            this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, this.getBackground().darker()));
        }

    }
}
