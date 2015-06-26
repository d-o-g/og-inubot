package com.inubot.bot.ui;

import javax.swing.*;
import java.awt.*;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

/**
 *
 *
 * WHY U NO WORK?!?!?
 *
 *
 *
 * @author Septron
 * @since June 22, 2015
 */
public class JConsole extends JInternalFrame implements Runnable {

    public static void main(String... args) {

        JFrame frame = new JFrame("Test");
        frame.add(new JConsole(100, 100));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(100, 100));
        frame.setVisible(true);
    }

    private JTextArea text = new JTextArea(30, 30);

    private PipedOutputStream out;
    private PipedInputStream in;

    public JConsole(int width, int height) {
        try {
            out = new PipedOutputStream();
            in = new PipedInputStream(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.setOut(new PrintStream(out));
        setSize(width, height);
        init();
        new Thread(this).start();
    }

    public void init() {
        JScrollPane scroll = new JScrollPane(text);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        text.setFont(new Font("Courier", Font.PLAIN, 10));
        text.setAutoscrolls(true);
        text.setEditable(false);
        setContentPane(scroll);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
                if (in.available()> 0)
                {
                    byte buf[] = new byte[in.available()];
                    if (in.read(buf, 0, buf.length) > 0) {
                        String output = text.getText();
                        output += new String(buf);
                        int l = output.length();
                        if (l > (1024 * 10))
                            output = output.substring(l - (1024 * 10), l - 1);
                        text.setText(output);

                        text.setCaretPosition(text.getDocument().getLength());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
