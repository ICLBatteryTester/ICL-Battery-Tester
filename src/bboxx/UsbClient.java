package bboxx;

import bboxx.UsbHid.HidDevice;
import bboxx.UsbHid.HidDeviceInfo;

import java.awt.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.LinkedList;


public class UsbClient {

	static final int HID_LEN = 64;
	
	static byte data[] = new byte[HID_LEN];
	static int val;
	
	static UsbHid hid;
	static HidDevice dev;
	
    // Initialize 
    //hid = new UsbHid("hidapi");
    //hid.hidInit();
	
    
    // Open a device
    //static HidDevice dev= hid.hidOpen(0xfffffeed, 0x1, "0000");
	//System.out.println("\nhid.hidOpen() -> " + (dev==null ? "Error" : "Success"));
	
	
	public static void openDevice(){
		//Initialize 
	    hid = new UsbHid("hidapi");
	    hid.hidInit();
	    
	    //Open a device
	    dev= hid.hidOpen(0xfffffeed, 0x1, "0000");
		System.out.println("\nhid.hidOpen() -> " + (dev==null ? "Error" : "Success"));
	}
	

	
	public static String returnCurrent(){
		
		// Write
	    String message="get current";
	    val=hid.hidWrite(dev, message.getBytes(), HID_LEN, (byte) 0);
	    if(val==-1) System.out.print("\nhid.hidWrite() ->\t<" + val+"> <Error: "  + hid.hidError(dev)+">\n");
	    
	    // Read - blocking
	    hid.hidNonBlocking(dev, false);
	 	val=hid.hidRead(dev, data); //wait for data
	 	if(val==-1) System.out.print("\nhid.hidRead() ->\t<" + val + "> <Error: " + hid.hidError(dev) + "> <" + showData(data, val) + ">\n");
	
	 	String response = new String(data);
	 	
	 	Scanner scanner = new Scanner(response);
	 	String mssg = scanner.next();
	 	String current = scanner.next();
	 	
	 	scanner.close();
	 	
	 	return current;
	}
	
	
	
	public static ArrayList<TestInfo> returnHistory(){
		
		ArrayList<String> history = new ArrayList<String>();
		// Write
	    String message="list results";
	    val=hid.hidWrite(dev, message.getBytes(), HID_LEN, (byte) 0);
	    if(val==-1) System.out.print("\nhid.hidWrite() ->\t<" + val+"> <Error: "  + hid.hidError(dev)+">\n");
	     
	    do{
	    	// Read - blocking
	    	hid.hidNonBlocking(dev, false);
	    	//val=hid.hidRead(dev, data); //wait for data
	    	val = hid.hidRead(dev, data, 50);
	    	if(val==-1) System.out.print("\nhid.hidRead() ->\t<" + val + "> <Error: " + hid.hidError(dev) + "> <" + showData(data, val) + ">\n");
	
	    	String response = new String(data);
	    	history.add(response);
	    	
	    }while(val!=0);

	    history.remove(0);
	    int size = history.size();
	    history.remove(size-1);
	    history.remove(size-2);
	    
	    //TestInfo[] testInfo;
	    
	    ArrayList<TestInfo> pastData = new ArrayList<TestInfo>();
	    
	    for(String item: history){
	    	Scanner scanner = new Scanner(item);
	    	String mssg = scanner.next();
		 	String name = scanner.next();
		 	String timeStamp = scanner.next();
		 	int timeStampInt = Integer.parseInt(timeStamp);
		 	scanner.close();
		 	
		 	Date date = new Date((long)timeStampInt*1000);
		 	
		 	pastData.add(new TestInfo(name, date));
		} 
	    
	    
	 	return pastData;
	
	}
	
	
	
	static String showData(byte b[], int val){
	    String str="";
	    for(int i=0; i<b.length && i<val; i++) str+= " "+Integer.toHexString(b[i] & 0x0FF);
	    return str;
	} 
	
	
	
	public static void closeDevice(){
		hid.hidClose(dev);
	 	hid.hidExit();
	}
	
	
	/*
	public static void main(String[] args) {
		openDevice();
		String current = returnCurrent();
		ArrayList<TestInfo> pastInfo = returnHistory();
		closeDevice();
		
		System.out.println("current: " + current);
	
		
		
		for(TestInfo info: pastInfo){
			System.out.println("Name: " + info.name + "   " + "Date: " + info.date);
		}
	}
	*/
}
