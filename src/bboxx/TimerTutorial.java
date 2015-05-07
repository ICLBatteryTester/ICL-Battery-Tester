package bboxx;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class TimerTutorial extends JFrame{
	
	JLabel promptLabel, timerLabel;
	int counter;
	JTextField tf;
	JButton button;
	Timer timer;
	
	public TimerTutorial(){
		setLayout(new GridLayout(2, 2, 5, 5));
		
		promptLabel = new JLabel("Enter seconds:", SwingConstants.CENTER);
		add(promptLabel);
		tf = new JTextField(5);
		add(tf);
		
		button = new JButton("Start timing");
		add(button);
		
		timerLabel = new JLabel("Waiting...", SwingConstants.CENTER);
		add(timerLabel);
		
		event e = new event();
		button.addActionListener(e);
		
	}
	
	public class event implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int count = (int)(Double.parseDouble(tf.getText()));
			timerLabel.setText("Time left: " + count);
			
			TimeClass tc = new TimeClass(count);
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
			
			if(counter>=1){
				timerLabel.setText("Test in progress...  " + counter);
			} else{
				timer.stop();
				timerLabel.setText("Test Completed!");
			}
		}
	}
	public static void main(String args[]){
		
		TimerTutorial gui = new TimerTutorial();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setSize(250,100);
		gui.setTitle("Timer program");
		gui.setVisible(true);
	}
}

