package bboxx;

import java.awt.EventQueue;

//import java.awt.event.*;
//import java.awt.*;
//import javax.swing.*;

import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;


public class Interface {

	private JFrame frame;
	private JLabel lblProgress;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface window = new Interface();
					window.frame.setVisible(true);
					window.frame.setTitle("BBOXX Battery Test");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Interface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(SystemColor.text);
		frame.setBounds(100, 100, 603, 356);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblBboxxBatteryTest = new JLabel("BBOXX Battery Test");
		lblBboxxBatteryTest.setForeground(Color.BLACK);
		lblBboxxBatteryTest.setFont(new Font("Calibri", Font.BOLD, 29));
		lblBboxxBatteryTest.setHorizontalAlignment(SwingConstants.CENTER);
		lblBboxxBatteryTest.setBounds(164, 28, 356, 38);
		frame.getContentPane().add(lblBboxxBatteryTest);
		
		JButton btnStart = new JButton("START");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TimerFrame timer_frame = new TimerFrame();
				timer_frame.setVisible(true);
				//lblProgress.setText("Test in Progress...");
			}
		});
		btnStart.setFont(new Font("Calibri", Font.BOLD, 20));
		btnStart.setForeground(new Color(0, 128, 0));
		btnStart.setBackground(UIManager.getColor("Button.background"));
		btnStart.setBounds(40, 162, 96, 38);
		frame.getContentPane().add(btnStart);
		
		JButton btnAdv = new JButton("Advanced ");
		btnAdv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Advanced adv_frame = new Advanced();
				adv_frame.setVisible(true);
			}
		});
		btnAdv.setBackground(UIManager.getColor("Button.background"));
		btnAdv.setForeground(new Color(0, 0, 0));
		btnAdv.setFont(new Font("Calibri", Font.PLAIN, 13));
		btnAdv.setBounds(470, 286, 96, 24);
		frame.getContentPane().add(btnAdv);
		
		lblProgress = new JLabel("");
		lblProgress.setForeground(new Color(0, 0, 0));
		lblProgress.setHorizontalAlignment(SwingConstants.CENTER);
		lblProgress.setFont(new Font("Calibri", Font.BOLD, 16));
		lblProgress.setBounds(164, 150, 166, 20);
		frame.getContentPane().add(lblProgress);
		
		JLabel lblTimer = new JLabel("New label");
		lblTimer.setBackground(SystemColor.textHighlight);
		lblTimer.setForeground(SystemColor.text);
		lblTimer.setBounds(148, 241, 133, 29);
		frame.getContentPane().add(lblTimer);
		
		JLabel lblResult = new JLabel("RESULT:");
		lblResult.setBackground(SystemColor.textHighlight);
		lblResult.setForeground(new Color(0, 0, 0));
		lblResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblResult.setFont(new Font("Calibri", Font.BOLD, 20));
		lblResult.setBounds(40, 240, 96, 29);
		frame.getContentPane().add(lblResult);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\Norbert Kisiel\\Desktop\\Project\\Project\\img\\bboxx-logo.png"));
		lblNewLabel.setBounds(51, 18, 82, 82);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setIcon(new ImageIcon("C:\\Users\\Norbert Kisiel\\Desktop\\Project\\Project\\img\\prev2.png"));
		lblNewLabel_1.setBounds(421, 124, 166, 142);
		frame.getContentPane().add(lblNewLabel_1);
	}
}


