package com.inubot.script.bytehound;

import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.Tile;
import com.inubot.script.Script;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.Tile;
import com.inubot.script.Script;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Cameron on 2015-05-05.
 */
public class AutoWalker extends Script {

    private static final Tile RELLEKKA = new Tile(2625, 3677, 0);
    private Tile destination = null;

    @Override
    public boolean setup() {
        new GUI().setVisible(true);
        return true;
    }

    @Override
    public int loop() {
        if (destination != null) {
            while (destination.distance() > 3) {
                Movement.walkTo(destination);
            }
        }
        return 500;
    }

    private class GUI extends JFrame {

        private final JComboBox locations = new JComboBox(new String[] { "Custom", "Rellekka" });
        private final JButton traverse = new JButton("Traverse");
        private final JTextField xInput = new JTextField(10);
        private final JTextField yInput = new JTextField(10);

        public GUI() {
            this.setLayout(new FlowLayout());

            traverse.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    switch (String.valueOf(locations.getSelectedItem())) {
                        case "Custom":
                            if (xInput.getText().isEmpty() || yInput.getText().isEmpty()) {
                                JOptionPane.showInputDialog("Please enter both an X and a Y value!");
                            } else {
                                boolean failed = false;
                                for (char c : xInput.getText().toCharArray()) {
                                    if (!isNumerical(c)) {
                                        JOptionPane.showInputDialog("Please enter only numerical digits for the X value!");
                                        failed = true;
                                        break;
                                    }
                                }
                                for (char c : yInput.getText().toCharArray()) {
                                    if (!isNumerical(c)) {
                                        JOptionPane.showInputDialog("Please enter only numerical digits for the Y value!");
                                        failed = true;
                                        break;
                                    }
                                }
                                if (!failed) {
                                    destination = new Tile(Integer.parseInt(xInput.getText()), Integer.parseInt(yInput.getText()));
                                }
                            }
                            break;
                        case "Rellekka":
                            destination = RELLEKKA;
                            break;
                    }
                }
            });
            this.add(locations);
            this.add(xInput);
            this.add(yInput);
            this.add(traverse);

            this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        }
    }

    private boolean isNumerical(char c) {
        if (c == 0 || c == 1 || c == 2 || c == 3 || c == 4 || c == 5 || c == 6 || c == 7 || c == 8 || c == 9) {
            return true;
        }
        return false;
    }
}
