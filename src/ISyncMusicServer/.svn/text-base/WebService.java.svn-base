/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ISyncMusicServer;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.*;

/**
 *
 * @author michael
 */
public class WebService {
        // custom https trust manager to allow self signed ssl certificate
	X509TrustManager[] trustAllCerts = new X509TrustManager[] { 
		    new X509TrustManager() {     
		        public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
		            return null;
		        }
		        public void checkClientTrusted( 
		            java.security.cert.X509Certificate[] certs, String authType) {
		            } 
		        public void checkServerTrusted( 
		            java.security.cert.X509Certificate[] certs, String authType) {
		        }
		    } 
		};
        public String create(String username, String password){
            String query;
            try {
                query = "email="+URLEncoder.encode(username,"UTF-8")+"&";
                query += "pass="+URLEncoder.encode(password,"UTF-8")+"&";
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(WebService.class.getName()).log(Level.SEVERE, null, ex);
                return "0";
            }
            
            // connect; will be migrated into single function for all three ws actions
            try {
                // set the custom trust store
                SSLContext sc = SSLContext.getInstance("SSL"); 
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                // set hostname verification
                    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                            public boolean verify(String hostname, SSLSession session) {
                                    return (hostname.equals("wallaceitlogistics.com")?true:false);
                            }
                    });
                // set url
                    URL myurl = new URL("https://wallaceitlogistics.com/isyncmusic/createaccn.php");
                    HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
                    // set request headers
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-length", String.valueOf(query.length())); 
                    con.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
                    //con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)"); 
                    con.setDoOutput(true); 
                    con.setDoInput(true);
                    // send post vars
                    DataOutputStream output = new DataOutputStream(con.getOutputStream());  
                    output.writeBytes(query);
                    output.close();
                    // read response
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String response = "";
                    String currentline;
                    while ((currentline = in.readLine()) != null){
                            response+=currentline;
                    }
                    in.close();
                    System.out.println("server response: "+response);
                    if (!response.equals("1")){
                        response = (response.equals("2")?"Email already in use":"Server error!");
                    }
                    return response;
            } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "IO Error";
            } catch (KeyManagementException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return "HTTPS key error";
            } catch (NoSuchAlgorithmException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return "No algorithm exception";
            }
        }
        public String check(String username, String password){
            String query;
            try {
                query = "email="+URLEncoder.encode(username,"UTF-8")+"&";
                query += "pass="+URLEncoder.encode(password,"UTF-8")+"&";
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(WebService.class.getName()).log(Level.SEVERE, null, ex);
                return "0";
            }
            // connect; will be migrated into single function for all three ws actions
            try {
                // set the custom trust store
                SSLContext sc = SSLContext.getInstance("SSL"); 
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                // set hostname verification
                    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                            public boolean verify(String hostname, SSLSession session) {
                                    return (hostname.equals("wallaceitlogistics.com")?true:false);
                            }
                    });
                // set url
                    URL myurl = new URL("https://wallaceitlogistics.com/isyncmusic/checkaccn.php");
                    HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
                    // set request headers
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-length", String.valueOf(query.length())); 
                    con.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
                    //con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)"); 
                    con.setDoOutput(true); 
                    con.setDoInput(true);
                    // send post vars
                    DataOutputStream output = new DataOutputStream(con.getOutputStream());  
                    output.writeBytes(query);
                    output.close();
                    // read response
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String response = "";
                    String currentline;
                    while ((currentline = in.readLine()) != null){
                            response+=currentline;
                    }
                    in.close();
                    System.out.println("server response: "+response);
                    // determine text response from int value
                    if (!response.equals("1")){
                        response = (response.equals("2")?"Email or password incorrect":(response.equals("3")?"Account not activated":"Server error!"));
                    }
                    return response;
            } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "IO Error";
            } catch (KeyManagementException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return "HTTPS key error";
            } catch (NoSuchAlgorithmException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return "No algorithm exception";
            } 
        }
	public String update(String username, String hashedpass, String extip, String intip){
		// connect to webservice and get ip addresses
		String query;
		try {
			/*String hashedpass = "";
			// hash the password; will eventually be done when user submits it
			try {
				MessageDigest digest = MessageDigest.getInstance("MD5");
				byte[] hashedbytes = digest.digest(password.getBytes("UTF-8"));
				hashedpass = new BigInteger(1, hashedbytes).toString(16);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Password hashing failed!";
			}*/
			query = "email="+URLEncoder.encode(username,"UTF-8")+"&";
			query += "pass="+URLEncoder.encode(hashedpass,"UTF-8")+"&";
                        query += "extip="+URLEncoder.encode(extip,"UTF-8")+"&";
                        query += "intip="+URLEncoder.encode(intip,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "Url encoding failed!";
		}
		// connect
		try {
			// set the custom trust store
			SSLContext sc = SSLContext.getInstance("SSL"); 
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			// set hostname verification
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
				public boolean verify(String hostname, SSLSession session) {
					return (hostname.equals("wallaceitlogistics.com")?true:false);
				}
                });
		    // set url
			URL myurl = new URL("https://wallaceitlogistics.com/isyncmusic/setip.php");
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
			// set request headers
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-length", String.valueOf(query.length())); 
			con.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
			//con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)"); 
			con.setDoOutput(true); 
			con.setDoInput(true);
			// send post vars
			DataOutputStream output = new DataOutputStream(con.getOutputStream());  
			output.writeBytes(query);
			output.close();
			// read response
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String response = "";
			String currentline;
			while ((currentline = in.readLine()) != null){
				response+=currentline;
			}
			in.close();
			System.out.println("server response: "+response);
			// Check response, if response does not contain "extip" return false; return server error message TBC
			if (!response.equals("OK")){
				return response;
			}
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "IO Error";
		} catch (KeyManagementException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "HTTPS key error";
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "No algorithm exception";
		}
		return "OK";
	}
}
