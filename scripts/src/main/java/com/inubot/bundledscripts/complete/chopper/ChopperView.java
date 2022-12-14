package com.inubot.bundledscripts.complete.chopper;

import com.inubot.bundledscripts.proframework.ProModel.Event;
import com.inubot.bundledscripts.proframework.ProView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ChopperView extends ProView<ChopperController> implements ChopperConstants {

    private final JFrame frame;
    private final JList<Progression> tasks;
    private final DefaultListModel<Progression> taskModel;
    private boolean disposable;

    ChopperView() {
        this.frame = new JFrame("ProChopper");
        this.tasks = new JList<>();
        this.taskModel = new DefaultListModel<>();
        frame.getContentPane().setLayout(new BorderLayout());

        JPanel taskPane = new JPanel();
        taskPane.setLayout(new BorderLayout());

        tasks.setModel(taskModel);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        JButton add = new JButton("Add");
        JFrame addFrame = mkAddFrame();
        add.addActionListener(e -> {
            addFrame.setVisible(true);
        });
        JButton remove = new JButton("Remove");
        remove.addActionListener(e -> {
            int idx = tasks.getSelectedIndex();
            if (idx != -1) {
                taskModel.removeElementAt(idx);
            }
        });
        JComboBox<ProgressionType> types = new JComboBox<>(new ProgressionType[]{
                ProgressionType.BEST_CASH,
                ProgressionType.BEST_EXPERIENCE,
                ProgressionType.ejifojdf
        });
        types.getModel().setSelectedItem("Select a preset or add your own tasks!");
        buttonPanel.add(add);
        buttonPanel.add(remove);

        buttonPanel.add(new JLabel("Premade types:"));
        buttonPanel.add(types);
        types.addActionListener(e -> {
            ProgressionType type = (ProgressionType) types.getSelectedItem();
            if (type != null) {
                taskModel.clear();
                for (Progression p : type.getProgressFlow()) {
                    taskModel.addElement(p);
                }
            }
        });

        taskPane.add(buttonPanel, BorderLayout.NORTH);
        taskPane.add(tasks, BorderLayout.SOUTH);

        frame.getContentPane().add(taskPane, BorderLayout.NORTH);

        JButton start = new JButton("Start");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disposable = true;
                getController().encounter(new Event<>(this, START_PROP));
                java.util.List<Progression> progressions = new ArrayList<>();
                for (Object o : taskModel.toArray()) {
                    progressions.add((Progression) o);
                }
                getController().getModel().setProgressionType(
                        new ProgressionType(
                                "Custom",
                                progressions
                        )
                );
            }
        });
        frame.getContentPane().add(start, BorderLayout.SOUTH);
        frame.setMinimumSize(new Dimension(500, 300));
        frame.setPreferredSize(new Dimension(500, 300));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private JFrame mkAddFrame() {
        JFrame frame = new JFrame("Create a task!");
        frame.getContentPane().setLayout(new BorderLayout());
        JRadioButton[] trees = new JRadioButton[Tree.values().length];
        for (int i = 0; i < trees.length; i++) {
            trees[i] = new JRadioButton(Tree.values()[i].getName());
        }
        trees[0].setSelected(true);
        JPanel top = new JPanel();
        top.setLayout(new FlowLayout());
        for (JRadioButton treeBtn : trees) {
            treeBtn.addActionListener(e -> {
                if (treeBtn.isSelected()) {
                    for (JRadioButton treeBtn0 : trees) {
                        if (!treeBtn0.equals(treeBtn)) {
                            treeBtn0.setSelected(false);
                        }
                    }
                }
            });
            top.add(treeBtn);
        }
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        JTextField input = new JTextField("What level would you like to chop this tree at?");
        JCheckBox type = new JCheckBox("Level (uncheck for duration)");
        type.setSelected(true);
        type.addActionListener(e -> input.setText(type.isSelected()
                ? "What level would you like to chop this tree at?"
                : "How long do you want to chop this tree for? (minutes)"));
        JButton done = new JButton("Done");
        done.addActionListener(e -> {
            Tree selected = null;
            for (int i = 0; i < trees.length; i++) {
                JRadioButton btn = trees[i];
                if (btn.isSelected()) {
                    selected = Tree.values()[i];
                    break;
                }
            }
            if (selected != null) {
                taskModel.addElement(type.isSelected()
                        ? new Progression.Basic(selected, Integer.parseInt(input.getText()))
                        : new Progression.Timed(selected, TimeUnit.MINUTES.toMillis(Integer.parseInt(input.getText()))));
                frame.dispose();
            }
        });
        bottom.add(type);
        bottom.add(input);
        bottom.add(done);

        frame.getContentPane().add(BorderLayout.NORTH, top);
        frame.getContentPane().add(BorderLayout.SOUTH, bottom);
        frame.setPreferredSize(new Dimension(500, 300));
        frame.setMinimumSize(new Dimension(500, 300));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        return frame;
    }

    @Override
    public void display() {
        frame.setVisible(true);
    }

    @Override
    public void dispose() {
        frame.dispose();
    }

    public boolean isDisposable() {
        return disposable;
    }
}
