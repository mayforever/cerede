/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mayforever.ceredeclient;

import com.mayforever.ceredeclient.conf.Configuration;
import com.mayforever.ceredeclient.conn.CommandClient;
import com.mayforever.ceredeclient.conn.ImageClient;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
// import javax.security.auth.login.Configuration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 *
 * @author mis
 */

public class App {
    public static CeredeForm ceredeForm = null;
    public static String username = null;
    public static String password = null;
    private static String projectPath = null;
    private static Logger logger =null;
    public static String serverIP = null;
    public static int serverPort;
    public static CommandClient commandClient = null;
    public static ImageClient imageClient = null;
    public static String hash = null;
    public static Map<String, RemoteViewer> mapRemoteViewer = null;
    public static int chunkCount = 0;
    public static Configuration config = null;
    
    public static void main(String[]  arg0){
        ApplicationContext applicationContextLauncher = null;
        if(arg0.length == 0) {
                applicationContextLauncher = new FileSystemXmlApplicationContext("conf"+File.separator+"ceredeclient.conf.xml");
        }else {
                applicationContextLauncher = new FileSystemXmlApplicationContext(arg0[0]+File.separator
                         +"conf"+File.separator+"ceredeclient.conf.xml");
        }
       
        config = (Configuration) applicationContextLauncher.getBean("ceredeclient");
        projectPath = config.getFilePath();
        String log4jPropertiesPath = projectPath + 
            "conf/log4j.properties";
        PropertyConfigurator.configure(log4jPropertiesPath);
        
        logger =Logger.getLogger("MAIN");
        serverIP = config.getServerAddress();
        serverPort = config.getServerPort();
        chunkCount = config.getChunkCount();
        username = config.getUsername();
        
        password = config.getPassword();
        
        hash = toHash(username + password);
        logger.debug("this pc hash is "+hash);
        
        mapRemoteViewer = new HashMap<String, RemoteViewer>();
        
        ceredeForm = new CeredeForm();
        logger.debug("Cerede Form has been created");
        
        commandClient = new CommandClient();
        logger.debug("Command Client Object has been created");
        
        imageClient = new ImageClient();
        logger.debug("Image Client Object has been created");
        
        new TrayLoader();
        logger.debug("Tray loader has been loaded");
    }
    
    public static String toHash(String userPassword){
            String toHash = userPassword;
            String hash = "";
            MessageDigest md;
            
            try {
                StringBuilder sb = null;
                md = MessageDigest.getInstance("MD5");
                byte[] hashInBytes = md.digest(toHash.getBytes(StandardCharsets.UTF_8));

                sb = new StringBuilder();
                for (byte b : hashInBytes) {
                    sb.append(String.format("%02x", b));
                }
                hash = sb.toString();
            } catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
            return hash;
    }
}
