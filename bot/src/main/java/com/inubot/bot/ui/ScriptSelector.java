package com.inubot.bot.ui;

import com.inubot.Bot;
import com.inubot.bot.util.CachedClassLoader;
import com.inubot.bot.util.Configuration;
import com.inubot.bundledscripts.complete.alcher.ProAlcher;
import com.inubot.script.Manifest;
import com.inubot.script.Script;
import com.inubot.script.loader.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bytehound on 7/28/2015.
 */
public class ScriptSelector extends JFrame {

    private static final Class[] SCRIPT_CLASSES = {
            ProAlcher.class
    };

    public ScriptSelector() {
        super("Script Selector");

        LocalScriptLoader loader = new LocalScriptLoader();
        List<Entity> entities = new ArrayList<>();

        for (Class c : SCRIPT_CLASSES) {
            Manifest m = (Manifest) c.getAnnotation(Manifest.class);
            if (m != null) {
                RemoteScriptDefinition rem = new RemoteScriptDefinition(m.name(), m.developer(), m.desc(), m.version());
                rem.setScriptClass(c);
                entities.add(new Entity(rem));
            }
        }

        try {
            loader.parse(new File(Configuration.SCRIPTS));
            ScriptDefinition[] definitions = loader.getDefinitions();
            outer:
            for (ScriptDefinition def : definitions) {
                for (Entity e : entities) {
                    if (e.target.equals(def)) {
                        continue outer;
                    }
                }
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
                    def.setScriptClass((Class<? extends Script>) clazz);
                    entities.add(new Entity(def));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JPanel content = new JPanel();
        outer:
        for (Entity entity : entities) {
            for (Component c : content.getComponents()) {
                if (c instanceof Entity) {
                    Entity entity0 = (Entity) c;
                    if (entity.target.equals(entity0.target)) {
                        continue outer;
                    }
                }
            }
            content.add(entity);
        }

        content.setLayout(new GridLayout(entities.size(), 1));
        entities.forEach(content::add);
        JScrollPane scrollPane = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(320, 100));
        add(scrollPane);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private class Entity extends JPanel {

        private final ScriptDefinition target;

        private Entity(ScriptDefinition target) {
            this.target = target;

            this.setLayout(new BorderLayout());
            this.setPreferredSize(new Dimension(300, 20));

            JButton initiate = new JButton("Start");
            initiate.setFocusable(false);
            initiate.setToolTipText(target.desc());


            this.setToolTipText("Author: " + target.developer());
            JLabel title = new JLabel("   " + target.name());

            this.add(title, BorderLayout.WEST);
            this.add(initiate, BorderLayout.EAST);

            initiate.addActionListener(e -> {
                try {
                    Script targetInstance = target.getScriptClass().newInstance();
                    Bot.getInstance().getScriptFlux().execute(targetInstance);
                    dispose();
                } catch (InstantiationException | IllegalAccessException e1) {
                    e1.printStackTrace();
                }
            });

            this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, this.getBackground().darker()));
            if (target instanceof RemoteScriptDefinition) {
                setBackground(getBackground().darker());
            }
        }
    }
}
