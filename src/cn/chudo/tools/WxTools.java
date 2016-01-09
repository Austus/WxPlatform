package cn.chudo.tools;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;
import net.sf.json.JSONObject;
public class WxTools {
	// ��ӿ�������Ϣ�е�TokenҪһ��
	private static String token="zxcasdqwe123";
    public static String getJsApiTicket(String token){
    	String result=HttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/ticket/getticket","access_token="+token+"&type=jsapi");
		JSONObject obj=JSONObject.fromObject(result);
		String ticket=obj.getString("ticket");
		if(ticket!=null&!ticket.equals(""))
			return ticket;
		else
			return "��ȡƾ֤����";
    }
    
    public static String getAccessToken(String AppID,String AppSecret){
    	String jsonStr=HttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/token","grant_type=client_credential&appid="+AppID+"&secret="+AppSecret);
		JSONObject jObj=JSONObject.fromObject(jsonStr);
		String token=jObj.getString("access_token");
		if(token!=null&&!token.equals(""))
			return token;
		else
			return "��ȡ���Ƴ���";
    }
    public static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }
    public static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
    public static String getSignature(String AppID,String AppSecret, String timestamp, String nonce, String jsurl) throws IOException {
    /****
     * �� jsapi_ticket�� timestamp �� nonce ���ֵ����� �����д�ǩ�����������ֶ����� ASCII
     * ���С���������ֵ��򣩺�ʹ�� URL ��ֵ�Եĸ�ʽ����key1=value1&key2=value2����ƴ�ӳ��ַ���
     * string1��������Ҫע��������в�������ΪСд�ַ��� �������� string1 �� sha1 ���ܣ��ֶ������ֶ�ֵ������ԭʼֵ��������
     * URL ת�塣�� signature=sha1(string1)��
     * **���û�а������ɵ�key1=value&key2=valueƴ�ӵĻ��ᱨ��
     */
    	String token=getAccessToken(AppID,AppSecret);
    	String jsapi_ticket=getJsApiTicket(token);
    	System.out.println("jsapi_ticket:"+jsapi_ticket);
	    String[] paramArr = new String[] { "jsapi_ticket=" + jsapi_ticket,"timestamp=" + timestamp, "noncestr=" + nonce, "url=" + jsurl };
	    Arrays.sort(paramArr);
	    // �������Ľ��ƴ�ӳ�һ���ַ���
	    String content = paramArr[0].concat("&"+paramArr[1]).concat("&"+paramArr[2]).concat("&"+paramArr[3]);
	    String gensignature = null;
	    try {
	        MessageDigest md = MessageDigest.getInstance("SHA-1");
	        // ��ƴ�Ӻ���ַ������� sha1 ����
	        byte[] digest = md.digest(content.toString().getBytes());
	        gensignature = byteToStr(digest);
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    // �� sha1 ���ܺ���ַ����� signature ���жԱ�
	    if (gensignature != null) {
	        return gensignature.toLowerCase();// ����signature
	    } else {
	        return "false";
	    }
	    // return (String) (ciphertext != null ? ciphertext: false);
     }
	/**
	 * ��֤ǩ��
	 * 
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	public static boolean checkSignature(String signature, String timestamp,String nonce) {
		String[] arr = new String[] { token, timestamp, nonce };
		// ��token��timestamp��nonce�������������ֵ�������
		Arrays.sort(arr);
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			content.append(arr[i]);
		}
		MessageDigest md = null;
		String tmpStr = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			// �����������ַ���ƴ�ӳ�һ���ַ�������sha1����
			byte[] digest = md.digest(content.toString().getBytes());
			tmpStr = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		content = null;
		// ��sha1���ܺ���ַ�������signature�Աȣ���ʶ��������Դ��΢��
		return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
	}
    /** 
     * ���ֽ�����ת��Ϊʮ�������ַ��� 
     *  
     * @param byteArray 
     * @return 
     */  
    private static String byteToStr(byte[] byteArray) {  
        String strDigest = "";  
        for (int i = 0; i < byteArray.length; i++) {  
            strDigest += byteToHexStr(byteArray[i]);  
        }  
        return strDigest;  
    }  
  
    /** 
     * ���ֽ�ת��Ϊʮ�������ַ��� 
     *  
     * @param mByte 
     * @return 
     */  
    private static String byteToHexStr(byte mByte) {  
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };  
        char[] tempArr = new char[2];  
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];  
        tempArr[1] = Digit[mByte & 0X0F];  
        String s = new String(tempArr);  
        return s;  
    } 
}
