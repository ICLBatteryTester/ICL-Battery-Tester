package bboxx;

import bboxx.UsbHid.HidDevice;
import bboxx.UsbHid.HidDeviceInfo;

import java.util.Scanner;


public class ReadCurrent {

	static final int HID_LEN = 64;
	
	
	public static String returnCurrent(){
	
		UsbHid hid; 
	    // Initialize 
	    hid = new UsbHid("hidapi");
	    hid.hidInit();
	    
	    // Open a device
	    HidDevice dev= hid.hidOpen(0xfffffeed, 0x1, "0000");
		System.out.println("\nhid.hidOpen() -> " + (dev==null ? "Error" : "Success"));

		byte data[] = new byte[HID_LEN];
		int val;
		
		// Write
	    String message="get current";
	    val=hid.hidWrite(dev, message.getBytes(), HID_LEN, (byte) 0);
	    if(val==-1) System.out.print("\nhid.hidWrite() ->\t<" + val+"> <Error: "  + hid.hidError(dev)+">\n");
	    
	    // Read - blocking
	 	val=hid.hidRead(dev, data); //wait for data
	 	if(val==-1) System.out.print("\nhid.hidRead() ->\t<" + val + "> <Error: " + hid.hidError(dev) + "> <" + showData(data, val) + ">\n");
	
	 	String response = new String(data);
	 	
	 	hid.hidClose (dev);
	 	hid.hidExit();
	 	
	 	Scanner scanner = new Scanner(response);
	 	String mssg = scanner.next();
	 	String current = scanner.next();
	 	
	 	scanner.close();
	 	
	 	return current;
	}
	
	
	static String showData(byte b[], int val){
	    String str="";
	    for(int i=0; i<b.length && i<val; i++) str+= " "+Integer.toHexString(b[i] & 0x0FF);
	    return str;
	} 
}
