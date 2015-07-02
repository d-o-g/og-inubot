package builder.map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Jamie on 9/14/2014.
 */
public class Start {

    private static int index = 0;

    //index x y z edgecount edges[] regular
    //index x y z edgecount edges[] name action object
    public static void main(String... args) {
        Map map = new Map();
        JFrame frame = new JFrame("memekit");
        frame.setLayout(new FlowLayout());
        JButton addReg = new JButton("Add regular");
        addReg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(index++ + " " + map.mtx + " " + map.mty + " 0 0 regular");
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
    }
}
