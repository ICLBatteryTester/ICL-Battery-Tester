package bboxx;

import java.sql.*;

import javax.swing.JOptionPane;

public class DATAConnectionClass {

	Connection conn=null;
	public static Connection dbConnector()
	{
		try{
			
			Class.forName("org.sqlite.JDBC");
			Connection conn=DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Eleftheriou\\workspace\\BBOXX\\DATA.sqlite");
			JOptionPane.showMessageDialog(null, "Connection Succesful");
			return conn;
			
		}	catch(Exception e)
		    {
			  JOptionPane.showMessageDialog(null, e);
			  return null;
		    }
	
	
   }
	
	
}
