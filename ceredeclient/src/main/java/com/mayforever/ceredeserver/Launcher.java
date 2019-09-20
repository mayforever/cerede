package com.mayforever.ceredeserver;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.mayforever.ceredeserver.conf.Configuration;
import com.mayforever.ceredeserver.conn.TCPServer;
import com.mayforever.ceredeserver.conn.data.TCPData;
import com.mayforever.ceredeserver.session.SessionManager;
import com.mayforever.ceredeserver.session.SessionProcessor;
import com.mayforever.queue.Queue;


/**
 * Hello world!
 *
 */
public class Launcher 
{

	public static Configuration config = null;
	public static HashMap<String, TCPData> controllerMap = null;  
	public static SessionManager sessionManager = null;
	public static Logger logger = Logger.getLogger("MAIN");
	public static String logsConfigPath = "";
	public static Queue<byte[]> dataProcess = null;
	
	
	public static void main(String[] args)
    {

		ApplicationContext applicationContextLauncher = null;
		if(args.length == 0) {
			applicationContextLauncher = new FileSystemXmlApplicationContext("conf"+File.separator+"ceredeserver.conf.xml");
		}else {
			 applicationContextLauncher = new FileSystemXmlApplicationContext(args[0]+File.separator+"conf"+File.separator+"ceredeserver.conf.xml");
		}
       
        config = (Configuration) applicationContextLauncher.getBean("ceredeserver");
    	

    	controllerMap = new HashMap<String, TCPData>();
    	
    	logsConfigPath = config.getFilePath();
    	
    	PropertyConfigurator.configure(logsConfigPath+"conf"+File.separator+"log4j.properties");
    	
    	new TCPServer(config.getPort(), config.getAddress());
    	logger.info("The server has been lunched at port "+config.getPort()+" with address of "+ config.getAddress());
    	logger.info("waiting for client to connect");
    	
    	Launcher.dataProcess = new Queue<>();
    	
    	int numberOfProcessor = config.getProcessorCount();
    	
    	for(int i = 0; i < numberOfProcessor; i++) {
    		SessionProcessor sessionProcessor = new SessionProcessor();
        	sessionProcessor.startSession();
    	}
    	
    	while(true){
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
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
//             System.out.println("remote hash : " + hash);
        } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        return hash;
}
}
