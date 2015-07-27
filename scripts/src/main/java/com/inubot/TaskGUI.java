package com.inubot;

import javax.swing.*;

/**
 * @author Septron
 * @since July 25, 2015
 */
public class TaskGUI extends JFrame {

	public static void main(String... args) {
		new TaskGUI().setVisible(true);
	}

	public TaskGUI() {
		GroupLayout layout = new GroupLayout(getContentPane());
		setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		JButton a = new JButton("A");
		JButton b = new JButton("B");
		JButton c = new JButton("C");
		JButton d = new JButton("D");
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
						.addComponent(a)
						.addComponent(b)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(c)
								.addComponent(d))
		);
		layout.setVerticalGroup(
				layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(a)
								.addComponent(b)
								.addComponent(c))
						.addComponent(d)
		);
		pack();
	}
}
