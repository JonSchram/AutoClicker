/*
 * This code was written by SineniF, and is hereby released under the WTFPL (more info at http://www.wtfpl.net/about/)
 * If ever the terms of the license are unclear, please refer to clause 0 of the license.
 * 
 * Enjoy!
 * 
 */

package sinenif.convenience;

import java.awt.AWTException;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class ClickerWindow {
	private JFrame mainWindow;

	private JButton helpButton;
	private JButton licenseButton;

	private JLabel clicksLabel;
	private JTextField clicksTextField;

	private JLabel delayLabel;
	private JTextField delayTextField;

	private JLabel initialDelayLabel;
	private JTextField initialDelayTextField;

	private JLabel startLabel;
	private JLabel endLabel;

	private JButton startButton;
	private JButton stopButton;
	private Timer clicker;

	private int clicks = 0;
	private int clickLimit = 10;
	private int clickDelay = 50;
	private int initialDelay = 1000;

	public ClickerWindow() {
		mainWindow = new JFrame("Auto clicker");
		mainWindow.setLayout(new GridLayout(0, 2));

		helpButton = new JButton("Help");
		helpButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(
								mainWindow,
								"All units are specified in milliseconds; 1000 milliseconds = 1 second\n"
										+ "If the click delay is too short, your computer may lag.\n"
										+ "If this is the case, then the program you are sending clicks into may not receive some of them,\n"
										+ "and the clicks actually performed will be less than what is expected.\n"
										+ "Therefore, please be reasonable with click delay");
			}
		});

		licenseButton = new JButton("License");
		licenseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(
								mainWindow,
								"This program has been written by SineniF, a.k.a. Sine/Sin.\n"
										+ "Code for JTextArea numeric validation from user \"Hovercraft Full Of Eels\" on http://stackoverflow.com/questions/11093326/restricting-jtextfield-input-to-integers\n"
										+ "This program is hereby released under the following license (more info at http://www.wtfpl.net/about/)\n\n"
										+ "======================================================\n"
										+ "DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE\n"
										+ "Version 2, December 2004\n"
										+ "\n"
										+ " Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>"
										+ "\n"
										+ "Everyone is permitted to copy and distribute verbatim or modified\n"
										+ "copies of this license document, and changing it is allowed as long\n"
										+ "as the name is changed.\n"
										+ "\n"
										+ "         DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE\n"
										+ " TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION\n"
										+ "\n"
										+ "0. You just DO WHAT THE FUCK YOU WANT TO.",
								"Software license",
								JOptionPane.INFORMATION_MESSAGE);
			}
		});

		clicksLabel = new JLabel("Number of clicks to perform:");
		clicksTextField = new JTextField(Integer.toString(clickLimit));
		((PlainDocument) clicksTextField.getDocument())
				.setDocumentFilter(new NumericFilter());
		clicksTextField.addFocusListener(new NullRemover());

		delayLabel = new JLabel("Milliseconds between clicks:");
		delayTextField = new JTextField(Integer.toString(clickDelay));
		((PlainDocument) delayTextField.getDocument())
				.setDocumentFilter(new NumericFilter());
		delayTextField.addFocusListener(new NullRemover());

		initialDelayLabel = new JLabel("Milliseconds before clicks begin:");
		initialDelayTextField = new JTextField(Integer.toString(initialDelay));
		((PlainDocument) initialDelayTextField.getDocument())
				.setDocumentFilter(new NumericFilter());
		initialDelayTextField.addFocusListener(new NullRemover());

		startLabel = new JLabel(
				"Press \"start\" to begin clicking frenetically.");
		endLabel = new JLabel(
				"<html>You can always press \"stop\" to stop clicking<br>before all clicks have completed.</html>");

		startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clicks = 0;
				clickLimit = Integer.parseInt(clicksTextField.getText());
				clickDelay = Integer.parseInt(delayTextField.getText());

				mainWindow.setTitle("Clicking " + clickLimit + " times ("
						+ clicks + " done)");

				clicker.setInitialDelay(initialDelay);
				clicker.setDelay(clickDelay);

				// disable resizing when clicking so that bad things don't
				// happen when clicking starts
				mainWindow.setResizable(false);
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				clicker.start();
			}
		});

		stopButton = new JButton("Stop");
		stopButton.setEnabled(false);
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainWindow.setTitle("Stopped");
				clicker.stop();
				stopButton.setEnabled(false);
				startButton.setEnabled(true);
				// re-enable resizing when the clicking is stopped
				mainWindow.setResizable(true);
			}
		});

		clicker = new Timer(clickDelay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (clicks == clickLimit - 1) {
					// this is the final click, so do the click and stop
					// so that the timer doesn't have to wait another click to
					// stop ticking
					doClick();
					stopClicking();

				} else if (clicks < clickLimit) {
					// can do this click and at least one more, so only do click
					doClick();

				} else {
					// this case is pretty much obsolete now, since timer is
					// stopped on last click, but just in case the number of
					// clicks has exceeded the limit for some weird reason
					stopClicking();
				}
			}

			private void stopClicking() {
				clicker.stop();
				mainWindow.setTitle("Stopped");
				stopButton.setEnabled(false);
				startButton.setEnabled(true);
				mainWindow.setResizable(true);
			}

			private void doClick() {
				try {
					Robot clickRobot = new Robot();

					clickRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					clickRobot.delay(10);
					clickRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
					clicks++;
					mainWindow.setTitle("Clicking " + clickLimit + " times ("
							+ clicks + " done)");

				} catch (AWTException e) {
					e.printStackTrace();
				}
			}
		});

		clicker.setInitialDelay(initialDelay);

		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JFrame.setDefaultLookAndFeelDecorated(true);

		mainWindow.add(helpButton);
		mainWindow.add(licenseButton);

		mainWindow.add(clicksLabel);
		mainWindow.add(clicksTextField);

		mainWindow.add(delayLabel);
		mainWindow.add(delayTextField);

		mainWindow.add(initialDelayLabel);
		mainWindow.add(initialDelayTextField);

		mainWindow.add(startLabel);
		mainWindow.add(endLabel);
		mainWindow.add(startButton);
		mainWindow.add(stopButton);
		mainWindow.setAlwaysOnTop(true);
		mainWindow.pack();
	}

	public void run() {
		mainWindow.setVisible(true);
	}

	public static void main(String[] args) {
		ClickerWindow autoClicker = new ClickerWindow();
		autoClicker.run();
	}

}

// How to do this comes from user "Hovercraft Full Of Eels"
// http://stackoverflow.com/users/522444/hovercraft-full-of-eels
// on question at page:
// http://stackoverflow.com/questions/11093326/restricting-jtextfield-input-to-integers
class NumericFilter extends DocumentFilter {

	/**
	 * Checks that the string is a NONNEGATIVE integer (>=0)
	 */
	private boolean isNumeric(String s) {
		if (s.equals("")) {
			return true;
		}
		try {
			int i = Integer.parseInt(s);
			// true if i>=0, false if i<0
			return i >= 0;
		} catch (NumberFormatException e) {
			// number didn't parse, so invalid
			return false;
		}
	}

	@Override
	public void insertString(FilterBypass fb, int offset, String string,
			AttributeSet attr) throws BadLocationException {

		Document d = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(d.getText(0, d.getLength()));
		sb.insert(offset, string);

		if (isNumeric(sb.toString())) {
			super.insertString(fb, offset, string, attr);
		} else {
			// warn the user and don't allow the insert
		}

	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text,
			javax.swing.text.AttributeSet attrs) throws BadLocationException {
		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.replace(offset, offset + length, text);

		if (isNumeric(sb.toString())) {
			super.replace(fb, offset, length, text, attrs);
		} else {
			// warn the user and don't allow the insert
		}
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length)
			throws BadLocationException {
		Document doc = fb.getDocument();
		StringBuilder sb = new StringBuilder();
		sb.append(doc.getText(0, doc.getLength()));
		sb.delete(offset, offset + length);

		if (isNumeric(sb.toString())) {
			super.remove(fb, offset, length);
		} else {
			// warn the user and don't allow the remove
		}
	}

}

class NullRemover implements FocusListener {

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getComponent() instanceof JTextField) {
			JTextField textField = (JTextField) e.getComponent();
			if (textField.getText().equals("")) {
				textField.setText("0");
			}
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
	}
}
