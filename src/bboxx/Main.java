package bboxx;

import java.awt.EventQueue;
import java.awt.event.*;
import java.awt.*;

import javax.swing.*;

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

import java.awt.BorderLayout;
import java.awt.BorderLayout;
import java.util.TimerTask;

import bboxx.UsbHid.HidDevice;
import bboxx.UsbHid.HidDeviceInfo;

import java.awt.event.*;

import javax.swing.*;

//import javax.swing.Timer;
import java.util.*;
import java.util.Timer;  


public class Main {

	private JFrame frame;
	private JLabel lblProgress;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
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
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(SystemColor.text);
		frame.setBounds(100, 100, 620, 356);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnStart = new JButton("START");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblProgress.setText("Test in Progress...");
				//String start = UsbHidTestCase.UsbHidTest(); 
				//lblProgress.setText(start); 
				//GetCurrent task = new GetCurrent();
				//Timer timer = new Timer();
				//timer.scheduleAtFixedRate(task, 0, 1000);
				
				for(int i = 0; i< 10; i++) {
				    try {
				        //sending the actual Thread of execution to sleep X milliseconds
				        Thread.sleep(1000);
				    } catch(Exception e1) {
				        System.out.println("Exception : " + e1.getMessage());
				    }
				    String start = ReadCurrent.returnCurrent();
				    System.out.println(start);
					//lblProgress.setText(start); 
				}
			}
		});
		btnStart.setFont(new Font("Calibri", Font.BOLD, 20));
		btnStart.setForeground(new Color(0, 128, 0));
		btnStart.setBackground(UIManager.getColor("Button.background"));
		btnStart.setBounds(40, 124, 96, 38);
		frame.getContentPane().add(btnStart);
		
		JButton btnSTOP = new JButton("STOP");
		btnSTOP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblProgress.setText("Test Completed!");
			}
		});
		btnSTOP.setForeground(new Color(255, 0, 0));
		btnSTOP.setFont(new Font("Calibri", Font.BOLD, 20));
		btnSTOP.setBackground(UIManager.getColor("Button.background"));
		btnSTOP.setBounds(40, 173, 96, 38);
		frame.getContentPane().add(btnSTOP);
		
		JButton btnAdv = new JButton("Advanced ");
		btnAdv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Adv adv_frame = new Adv();
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
		lblResult.setBounds(40, 283, 96, 29);
		frame.getContentPane().add(lblResult);
		
		JLabel labelT = new JLabel("Timer:");
		labelT.setHorizontalAlignment(SwingConstants.CENTER);
		labelT.setForeground(new Color(0, 0, 0));
		labelT.setFont(new Font("Calibri", Font.PLAIN, 20));
		labelT.setBackground(SystemColor.textHighlight);
		labelT.setBounds(40, 240, 96, 29);
		frame.getContentPane().add(labelT);
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setIcon(new ImageIcon("C:\\Users\\User\\workspace\\BBOXX\\img\\prev2.png"));
		lblNewLabel_1.setBounds(421, 124, 166, 142);
		frame.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setIcon(new ImageIcon("C:\\Users\\User\\workspace\\BBOXX\\img\\bboxx_sign_in5.png"));
		lblNewLabel_2.setBounds(197, 15, 195, 82);
		frame.getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\User\\workspace\\BBOXX\\img\\bboxx_small_logo2.png"));
		lblNewLabel.setBounds(120, 15, 81, 69);
		frame.getContentPane().add(lblNewLabel);
	}
}


