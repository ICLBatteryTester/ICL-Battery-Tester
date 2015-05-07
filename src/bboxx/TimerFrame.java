package bboxx;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.ImageIcon;


public class TimerFrame extends JFrame {

	private JPanel contentPane;
	int counter;
	int seconds = 36000;
	int minutes, hours, secs_left, mins_left;
	final int SECS_PER_MIN = 60;
	final int SECS_PER_HOUR = 3600;

	Timer timer;
	JLabel timerLabel;
	JButton button2;
	
	public TimerFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 538, 267);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblPleaseWait = new JLabel("BBOXX Battery Test");
		lblPleaseWait.setFont(new Font("Calibri", Font.PLAIN, 22));
		lblPleaseWait.setBounds(195, 26, 192, 31);
		contentPane.add(lblPleaseWait);
		
		JLabel lblTimerPicture = new JLabel("");
		lblTimerPicture.setIcon(new ImageIcon("C:\\Users\\Norbert Kisiel\\Desktop\\Project\\Project\\img\\Clock2.png"));
		lblTimerPicture.setBounds(10, 38, 125, 143);
		contentPane.add(lblTimerPicture);
		
		timerLabel = new JLabel("Press START ", SwingConstants.LEFT);
		timerLabel.setFont(new Font("Calibri", Font.PLAIN, 25));
		timerLabel.setBounds(145, 84, 367, 49);
		contentPane.add(timerLabel);
		
		JButton button2 = new JButton("START");
		button2.setBackground(UIManager.getColor("Button.background"));
		button2.setFont(new Font("Calibri", Font.PLAIN, 20));
		button2.setForeground(new Color(0, 128, 0));
		button2.setBounds(416, 150, 96, 31);
		contentPane.add(button2);
		
		JButton btnBackTimer = new JButton("Back");
		btnBackTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		btnBackTimer.setForeground(new Color(0, 0, 0));
		btnBackTimer.setFont(new Font("Calibri", Font.PLAIN, 16));
		btnBackTimer.setBackground(UIManager.getColor("Button.background"));
		btnBackTimer.setBounds(440, 198, 72, 20);
		contentPane.add(btnBackTimer);
		
		event e = new event();
		button2.addActionListener(e);
	}

	public class event implements ActionListener{
		public void actionPerformed(ActionEvent e){
			
			TimeClass tc = new TimeClass(seconds);
			timer = new Timer(1000, tc);
			timer.start();
		}
	}

	public class TimeClass implements ActionListener{
		int counter;
		
		public TimeClass(int counter){
			this.counter = counter;
		}
		
		public void actionPerformed(ActionEvent tc){
			counter--;
			seconds--;
			hours = seconds / SECS_PER_HOUR;
			minutes = seconds / SECS_PER_MIN;
			mins_left = minutes % SECS_PER_MIN;
			secs_left = seconds % SECS_PER_MIN;
			
			if(counter>=1){
				timerLabel.setText("Time Left: " + hours + " h " + mins_left + " min " + secs_left + " sec ");
				if(seconds == 0){
					seconds = 60;
				}
				else if(minutes == 0){
					minutes = 60;
				}
			} else{
				timer.stop();
				timerLabel.setText("Test Completed!");
			}
		}
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TimerFrame frame = new TimerFrame();
					frame.setVisible(true);
					frame.setTitle("Please Wait...");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}