package bboxx;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.JTable;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.swing.JList;
import javax.swing.UIManager;




public class Adv extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Adv frame = new Adv();
					frame.setVisible(true);
					frame.setTitle("BBOXX Battery Test");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	Connection connection=null;
	public Adv() {
		
		connection=DATAConnectionClass.dbConnector();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 655, 336);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblV = new JLabel("");
		lblV.setForeground(Color.BLACK);
		lblV.setFont(new Font("Narkisim", Font.PLAIN, 20));
		lblV.setBounds(125, 133, 66, 28);
		contentPane.add(lblV);
		
		JLabel label_1 = new JLabel("");
		label_1.setForeground(Color.BLACK);
		label_1.setFont(new Font("Narkisim", Font.PLAIN, 20));
		label_1.setBounds(112, 133, 66, 28);
		contentPane.add(label_1);
		
		JLabel lblI = new JLabel("");
		lblI.setForeground(Color.BLACK);
		lblI.setFont(new Font("Narkisim", Font.PLAIN, 20));
		lblI.setBounds(125, 172, 66, 28);
		contentPane.add(lblI);
		
		JLabel lblT = new JLabel("");
		lblT.setForeground(Color.BLACK);
		lblT.setFont(new Font("Narkisim", Font.PLAIN, 20));
		lblT.setBounds(125, 212, 66, 28);
		contentPane.add(lblT);
		
		JButton btnBack = new JButton("Back");
		btnBack.setBackground(UIManager.getColor("Button.background"));
		btnBack.setForeground(new Color(0, 0, 0));
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		btnBack.setFont(new Font("Calibri", Font.PLAIN, 16));
		btnBack.setBounds(10, 11, 66, 28);
		contentPane.add(btnBack);
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setIcon(new ImageIcon("C:\\Users\\User\\workspace\\BBOXX\\img\\next.png"));
		lblNewLabel_1.setBounds(221, 69, 207, 198);
		contentPane.add(lblNewLabel_1);
		
		JButton buttonVoltage = new JButton("Voltage");
		buttonVoltage.setForeground(new Color(0, 0, 0));
		buttonVoltage.setFont(new Font("Calibri", Font.PLAIN, 15));
		buttonVoltage.setBackground(UIManager.getColor("Button.background"));
		buttonVoltage.setBounds(10, 133, 112, 28);
		contentPane.add(buttonVoltage);
		
		JButton buttonCurrent = new JButton("Current");
		buttonCurrent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		buttonCurrent.setForeground(Color.BLACK);
		buttonCurrent.setFont(new Font("Calibri", Font.PLAIN, 15));
		buttonCurrent.setBackground(UIManager.getColor("Button.background"));
		buttonCurrent.setBounds(10, 172, 112, 28);
		contentPane.add(buttonCurrent);
		
		JButton buttonTemp = new JButton("Temperature");
		buttonTemp.setForeground(Color.BLACK);
		buttonTemp.setFont(new Font("Calibri", Font.PLAIN, 14));
		buttonTemp.setBackground(UIManager.getColor("Button.background"));
		buttonTemp.setBounds(10, 213, 112, 28);
		contentPane.add(buttonTemp);
		
		JButton buttonVvsT = new JButton("Voltage vs Time");
		buttonVvsT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//try {
				
				//String query="select Voltage(V),Time(s) from DATA ";
				//JDBCCategoryDataset dataset=new JDBCCategoryDataset(DATAConnectionClass.dbConnector(),query);
				//JFreeChart chart=ChartFactory.createLineChart("Voltage against Time Plot","Time(s)","Voltage(V)", dataset);
				//BarRendereer renderer=null;
				//CategoryPlot plot=null;
				//renderer=new BarRenderer();
				//ChartFrame frame=new ChartFrame("Voltage vs Time Graph",chart);
				//frame.setVisible(true);
				//frame.setSize(400,650);
				//}
				  //catch (Exception e){
					//  JOptionPane.showMessageDialog(null, e);
				  //}
				
			}
		});
		buttonVvsT.setForeground(Color.BLACK);
		buttonVvsT.setFont(new Font("Calibri", Font.PLAIN, 15));
		buttonVvsT.setBackground(UIManager.getColor("Button.background"));
		buttonVvsT.setBounds(491, 133, 137, 28);
		contentPane.add(buttonVvsT);
		
		JButton buttonCvsT = new JButton("Current vs Time");
		buttonCvsT.setForeground(Color.BLACK);
		buttonCvsT.setFont(new Font("Calibri", Font.PLAIN, 15));
		buttonCvsT.setBackground(UIManager.getColor("Button.background"));
		buttonCvsT.setBounds(491, 172, 137, 28);
		contentPane.add(buttonCvsT);
		
		JButton buttonTmpvsT = new JButton("Temp vs Time");
		buttonTmpvsT.setForeground(Color.BLACK);
		buttonTmpvsT.setFont(new Font("Calibri", Font.PLAIN, 15));
		buttonTmpvsT.setBackground(UIManager.getColor("Button.background"));
		buttonTmpvsT.setBounds(491, 212, 137, 28);
		contentPane.add(buttonTmpvsT);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\User\\workspace\\BBOXX\\img\\advanced_settings.png"));
		lblNewLabel.setBounds(180, 8, 283, 50);
		contentPane.add(lblNewLabel);
	}
}