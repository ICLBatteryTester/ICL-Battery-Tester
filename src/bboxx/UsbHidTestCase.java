package bboxx;

import java.util.Scanner;

import bboxx.UsbHid.HidDevice;
import bboxx.UsbHid.HidDeviceInfo;





/*
 * Use this program to test with the TestCaseCode in the embed
 *  code in  http://mbed.org/cookbook/USBHID-bindings-
 * 
 * Victorix 2013.02.04
*/

public class UsbHidTestCase {
	
	/*static {
	    try {
	    	
	    	System.loadLibrary("hidapi");
	    	
	    } catch (UnsatisfiedLinkError e) {
	      System.err.println("Native code library failed to load.\n" + e);
	      System.exit(1);
	    }
	}
*/
	static final int HID_LEN = 64;

		public static String UsbHidTest(){
	  //public static void main(String[] args) {
	    UsbHid hid;
	    
	    // Initialize 
	    hid = new UsbHid("hidapi");
	    hid.hidInit();
	    
	 // Get and Show all hid devices if any
	    HidDeviceInfo info=hid.hidEnumerate(0, 0);
		if(info!=null){
		  HidDeviceInfo h=info;
		  do{
		    System.out.println(h.show());
		    h=h.next();
		  }while (h!=null);
		  hid.hidFreeEnumeration(info); //dispose of the device list
		}
		
	    // Open a device
		//HidDevice dev= hid.hidOpen(0x1234, 0x0006, null);
		HidDevice dev= hid.hidOpen(0xfffffeed, 0x1, "0000");
		System.out.println("\nhid.hidOpen() -> " + (dev==null ? "Error" : "Success"));

		
		byte data[] = new byte[HID_LEN];
		int val;
		
		// Read - blocking
		//hid.hidNonBlocking(dev, false);
		//val=hid.hidRead(dev, data); //wait for data
		//System.out.println("val: " + val);
		//System.out.print("\nhid.hidRead() ->\t<" + val+"> <Error: "  + hid.hidError(dev)+"> <"  +showData(data, val)+">\n");
		
		
	    // Write
	    String message="get current";
	    val=hid.hidWrite(dev, message.getBytes(), HID_LEN, (byte) 0);
	    if(val==-1) System.out.print("\nhid.hidWrite() ->\t<" + val+"> <Error: "  + hid.hidError(dev)+">\n");

	    // Read - blocking
	 	//hid.hidNonBlocking(dev, false);
	 	val=hid.hidRead(dev, data); //wait for data
	 	System.out.print("\nhid.hidRead() ->\t<" + val + "> <Error: " + hid.hidError(dev) + "> <" + showData(data, val) + ">\n");
	    
	 	String response = new String(data);
	 	System.out.print(response);
	 	
	 	hid.hidClose (dev);
	 	hid.hidExit();
	 	
	 	//return response;
	 	
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
