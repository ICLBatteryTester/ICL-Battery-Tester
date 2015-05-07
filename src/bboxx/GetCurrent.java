package bboxx;

import java.util.Timer;  
import java.util.TimerTask;

import javax.swing.JLabel;

import org.jfree.data.time.Millisecond;

/*
public class GetCurrent extends TimerTask {
 
public String start;	

	public void run() {
		start = UsbHidTestCase.UsbHidTest();
		System.out.println(start);
    }
	
 }
*/


public class GetCurrent implements Runnable {

    
	DynamicData data;
	
	public GetCurrent(DynamicData dynData){
		this.data = dynData;
	}
	
	
	public void run() {
    	
		UsbClient.openDevice();
		
    	int i=100;
        
        do{            
		    try {
		        //sending the actual Thread of execution to sleep X milliseconds
		        Thread.sleep(1000);
		    
		    } catch(Exception e1) {
		        System.out.println("Exception : " + e1.getMessage());
		    }
		    double currentD = Double.parseDouble(UsbClient.returnCurrent());
	        data.lastValue[1] = currentD;
	        data.datasets[1].getSeries(0).add(new Millisecond(), data.lastValue[1]); 
		    i--;
		    
        }while(i>0);
        
        UsbClient.closeDevice();
    }
}

