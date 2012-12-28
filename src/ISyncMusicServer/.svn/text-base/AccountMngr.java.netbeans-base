/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ISyncMusicServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michael Account manager is used to update IP addresses and change
 * passwords of the webservices account.
 */
public class AccountMngr {

    public String lastextip;
    public String lastintip;
    public String newextip;
    public String newintip;

    public void AccountMngr() {
        lastextip = new String();
        lastintip = new String();
        newextip = new String();
        newintip = new String();
    }

    public String[] runIPCheck() {
        // return array; ip not changed (0=0), changed (0=1, 1=extip, 2=intip, webserviceok=wsresult), erroradds (4=webserviceerror)
        // get ip of internal host and external ip from webservice
        getNewIP();
        // set results
        // check if the same
        boolean ipsame = isSameIP();
        // update using webservice if changed and WS account is setup and set return values accordingly
        if (!ipsame) {
            // get ws config; update ips on webservice if config exsists
            Setup read = new Setup();
            String[] wsconf = read.getWSConfig();
            if (wsconf[0].equals("1")) {
                // conf exists
                String wsresult = updateIP(wsconf[1], wsconf[2]);
                if (wsresult.equals("OK")) {
                    String[] result = {"1", newextip, newintip, "1"};
                    return result;
                } else {
                    String[] result = {"1", newextip, newintip, "0", wsresult};
                    return result;
                }
            } else {
                System.out.println("Web Service not enabled, skipping update");
                String[] result = {"1", newextip, newintip, "3"};
                return result;
            }
        } else {
            String[] result = {"0", newextip, newintip, "3"}; // ips are the same
            return result;
        }
    }

    private void getNewIP() {
        try {
            // get internal IP
            newintip = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Current local IP is: " + newintip);
        } catch (UnknownHostException ex) {
            Logger.getLogger(AccountMngr.class.getName()).log(Level.SEVERE, null, ex);
        }
        // get new external IP
        getNewExtIP();
    }

    private void getNewExtIP() {
        try {
            // get external IP using web service (upnp just a pain in the ass) 
            URL url = new URL("http://wallaceit.com.au/public/whatsmyip.php");
            URLConnection urlcon = url.openConnection();
            try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                            urlcon.getInputStream()))) {
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    newextip = inputLine;
                    System.out.println("Current external IP is: " + inputLine);
                }
            }
        } catch (IOException ex) {
            newextip = "0.0.0.0";
            Logger.getLogger(AccountMngr.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean isSameIP() {
        if (newextip.equals(lastextip) && newintip.equals(lastintip)) {
            System.out.println("IP addresses have not changed");
            return true;
        } else {
            System.out.println("IP addresses have change or this is the first run. Updating on webservice");
            return false;
        }
    }

    // get account creds and update the account IPs on the web service database
    private String updateIP(String email, String hashedpass) {
        // get account details from setup
        WebService wsupdate = new WebService();
        String wsresult = wsupdate.update(email, hashedpass, newextip, newintip);
        if (wsresult.equals("OK")) {
            // update last IP vars
            System.out.println("Webservice success; setting current ip vars");
            // setting lastips here will cause the process to complete if update fails
            lastextip = newextip;
            lastintip = newintip;
        }
        return wsresult;
    }

    public String createAccn(String username, String password) { // password should be hashed
        System.out.println("Creating WS account");
        WebService wservice = new WebService();
        // hash the password
        String hashedpass = new String();
        // hash the password
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hashedbytes = digest.digest(password.getBytes("UTF-8"));
            hashedpass = new BigInteger(1, hashedbytes).toString(16);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AccountMngr.class.getName()).log(Level.SEVERE, null, ex);
            return "Password encoding!";
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "Password hashing failed!";
        }
        // create account on server
        String wsresult = wservice.create(username, hashedpass);
        return wsresult;
    }

    public String checkAccn(String username, String hashedpass) {
        // check the status a web service account, returns 0 on failure, 1 on success, 2 incorrent email/password and 3 if account is not activated
        // checks the status of an account, adds the account to the config on success
        System.out.println("Checking WS account");
        WebService wservice = new WebService();
        // check account
        String wsresult = wservice.check(username, hashedpass);
        // if account check is successful (result equals 1), save account to the config; TBC will be handled directly in main class
        return wsresult;
    }
}
