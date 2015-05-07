package bboxx;

/*
 *  Use this class to communicate with a usb hid device using windows 7
 *   
 *   You need:
 *     jna.jar
 *     hidapi.dll 
 *     
 *   Refer to https://github.com/twall/jna for jna.jar
 *   To build hidapi.dll:
 *     download (hidapi-0.7.0.zip) https://github.com/signal11/hidapi/downloads
 *     build it with VisualStudio
 *     
 * References:
 * 
 *   http://www.signal11.us/oss/hidapi/hidapi/doxygen/html/hidapi_8h.html
 *   https://github.com/twall/jna
 *   http://jna.java.net/javadoc/overview-summary.html#wide-strings
 *   
 * @name UsbHid
 * @author victorix - 04.02.2013
 * @version 1 
*/
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.ByReference;
import com.sun.jna.WString;

/**
 *
 * @author Victorix
 */
public class UsbHid{
  static  String dllFich;
  public static int WSTR_LEN=512;
  static final String DEVICE_NULL="Device null";
  static final int DEVICE_ERROR=-2;
  Dll dll;
  
  public UsbHid(String f){
    dllFich=f;
    dll=Dll.INSTANCE;
  }
  
  public void hidInit(){
    dll.hid_init();
  }
    
  public void hidExit(){
    dll.hid_exit();
  }
    
  String hidGetManufacturer(HidDevice device){
    if(device==null) return DEVICE_NULL;
    MatrixByte wStr = new MatrixByte(WSTR_LEN);
    int res = dll.hid_get_manufacturer_string(device.ptr(), wStr, WSTR_LEN);
    return wStr.toString();
  }
    
  String hidGetProductId(HidDevice device){
    if(device==null) return DEVICE_NULL;
    MatrixByte wStr = new MatrixByte(WSTR_LEN);
    int res = dll.hid_get_product_string(device.ptr(), wStr, WSTR_LEN);
    return wStr.toString();
  }
            
  String hidGetSerialNumber(HidDevice device){ 
    if(device==null) return DEVICE_NULL;
    MatrixByte wStr = new MatrixByte(WSTR_LEN);
    int res = dll.hid_get_serial_number_string(device.ptr(), wStr, WSTR_LEN);
    return wStr.toString();
  }

  public HidDevice hidOpen(int vendor, int product, String serial_number){
    Pointer p = dll.hid_open((short)vendor, (short)product, serial_number==null ? null : new WString(serial_number));
    return (p==null ? null : new HidDevice(p)); 
  }
 
  public HidDevice hidOpenPath(String path){
    Pointer p = dll.hid_open_path(path);
    return (p==null ? null : new HidDevice(p)); 
  }
 
  public void hidClose(HidDevice device){
    if(device!=null)dll.hid_close(device.ptr());
  }    

  public String hidError(HidDevice device){
    if(device==null) return DEVICE_NULL;
    Pointer p=dll.hid_error(device.ptr());
    return p==null ? null :new MatrixByte(p.getByteArray(0, WSTR_LEN)).toString();
  }
    
  public boolean hidNonBlocking(HidDevice device, boolean lock){
    if(device==null) return false;
  	return 0==dll.hid_set_nonblocking(device.ptr(), lock?1:0);
  }

  int hidRead(HidDevice device, byte[] bytes){
    if(device==null || bytes==null) return DEVICE_ERROR;
  	MatrixByte m = new MatrixByte(bytes);
    int res=dll.hid_read(device.ptr(), m, m.matrix.length);
    return res;
  }

  int hidRead(HidDevice device, byte[] bytes, int tmout){
    if(device==null || bytes==null) return DEVICE_ERROR;
    MatrixByte m=new MatrixByte(bytes);
    int res=dll.hid_read_timeout(device.ptr(), m, bytes.length, tmout);
    return res;
  }    

  int hidGetFeatureReport(HidDevice device, byte[] bytes, byte reportId){
    if(device==null || bytes==null) return DEVICE_ERROR;
    MatrixByte m=new MatrixByte(WSTR_LEN); //bytes.length+1);
    m.matrix[0]=reportId;
    int res=dll.hid_get_feature_report(device.ptr(), m, bytes.length+1);
    if(res==-1) return res;
    System.arraycopy(m.matrix, 1, bytes, 0, res);
    return res;    	
  }

  int hidSendFeatureReport(HidDevice device, byte[] bytes, byte reportId){
    if(device==null || bytes==null) return DEVICE_ERROR;
    MatrixByte m=new MatrixByte(bytes.length+1);
    m.matrix[0]=reportId;
    System.arraycopy(bytes, 0, m.matrix, 1, bytes.length);
    int res=dll.hid_get_feature_report(device.ptr(), m, m.matrix.length);
    return res;    	
  }

  String hidGetIndexedString(HidDevice device, int idx){
    if(device==null) return DEVICE_NULL;
    MatrixByte wStr = new MatrixByte(WSTR_LEN);
    int res=dll.hid_get_indexed_string(device.ptr(), idx, wStr, WSTR_LEN);
    return res==-1 ? null: wStr.toString();    	
  }        

  int hidWrite(HidDevice device, byte[] bytes, int len, byte reportId){
    if(device==null || bytes==null) return DEVICE_ERROR;
    MatrixByte m=new MatrixByte(len+1);
    m.matrix[0]=reportId;
    if(bytes.length<len) len=bytes.length;
    if(len>1) System.arraycopy(bytes, 0, m.matrix, 1, len);
    return dll.hid_write(device.ptr(), m, m.matrix.length);
  }

  public HidDeviceInfo hidEnumerate(int vendor, int product){
    HidDeviceInfo p = dll.hid_enumerate((short)vendor, (short)product);
    return p;
  }    
    
  public void hidFreeEnumeration(HidDeviceInfo list){
    dll.hid_free_enumeration(list.getPointer());
  }
    
  /**************************************/

  public static class MatrixByte extends Structure implements ByReference { 
    public byte [] matrix=null;
        
    MatrixByte(int len){matrix = new byte[len]; }
    MatrixByte(byte[] bytes) {matrix=bytes;}    	

	/* wchars are written l i k e   t h i s (with '\0' in between) */
    public String toString(){
      String str="";
      for(int i=0; i<matrix.length && matrix[i]!=0; i+=2)
    	  str+= (char)(matrix[i] | matrix[i+1]<<8);
      return str;
    }      
  }

/****************************************************/
  public static class HidDevice extends Structure implements ByReference {
    public Pointer ptr;
    	
    public HidDevice(Pointer p){ ptr=p; }
    public Pointer ptr() {return ptr;}
  }
      
  public static class HidDeviceInfo extends Structure implements ByReference{
    public String path;
    public short vendor_id;
    public short product_id;
    public WString serial_number;
    public short release_number;
    public WString manufacturer_string;
    public WString product_string;       // Usage Page for this Device/Interface    	(Windows/Mac only).
    public short usage_page;             // Usage for this Device/Interface    	(Windows/Mac only).
    public short usage;   
    public int interface_number;
    public HidDeviceInfo next;           //public HidDeviceInfo.ByReference next;
        
    public HidDeviceInfo next(){return next;}     
        
    public boolean hasNext() { return next!=null;}
    
    public String show(){
      HidDeviceInfo u=this;
      String str="HidDeviceInfo\n";
      str+="\tpath<"+u.path+">\n";
      str+="\tvendor_id "+Integer.toHexString(u.vendor_id)+"\n";
      str+="\tproduct_id "+Integer.toHexString(u.product_id)+"\n";
      str+="\tserial_number<"+u.serial_number+">\n";
      str+="\trelease_number "+u.release_number+"\n";
      str+="\tmanufacturer_string<"+u.manufacturer_string+">\n";
      str+="\tproduct_string<"+u.product_string+">\n";         	
      str+="\tusage_page "+u.usage_page+"\n";
      str+="\tusage "+u.usage+"\n";
      str+="\tinterface_number "+u.interface_number+"\n";         	       	       	
      return str;
    }
  }    
    /***********************************************/    

  public interface Dll extends Library {

    Dll INSTANCE = (Dll) Native.loadLibrary(
            (Platform.isWindows() ? dllFich : "c"), Dll.class);

    void hid_init();
    void hid_exit(); 	
    Pointer hid_open(short vendor_id, short product_id, WString serial_number);
 	void hid_close(Pointer device);
    Pointer hid_error(Pointer device);
    int hid_read(Pointer device, MatrixByte.ByReference bytes, int length);
    int hid_read_timeout(Pointer device, MatrixByte.ByReference bytes, int length, int timeout);
    int hid_write(Pointer device, MatrixByte.ByReference data, int len); 	
    int hid_get_feature_report(Pointer device, MatrixByte.ByReference data, int length);
    int hid_send_feature_report(Pointer device, MatrixByte.ByReference data, int length);
    int hid_get_indexed_string(Pointer device, int idx, MatrixByte.ByReference string, int len);    	
    int hid_get_manufacturer_string(Pointer device, MatrixByte.ByReference str, int len);  
    int hid_get_product_string(Pointer device, MatrixByte.ByReference str, int len);
    int hid_get_serial_number_string(Pointer device, MatrixByte.ByReference str, int len);
    int hid_set_nonblocking (Pointer device, int nonblock);
    HidDeviceInfo hid_enumerate(short vendor_id, short product_id);
    void  hid_free_enumeration (Pointer devs);
    Pointer hid_open_path (String path);
  }
}