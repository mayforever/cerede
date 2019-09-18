/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mayforever.ceredeclient.conn;

import com.mayforever.ceredeclient.App;
import com.mayforever.ceredeclient.model.Authenticate;
import com.mayforever.ceredeclient.model.Command;
import com.mayforever.ceredeclient.model.CommandRequest;
import com.mayforever.network.newtcp.TCPClient;
import com.mayforever.queue.Queue;
import com.mayforever.thread.BaseThread;
import com.mayforever.tools.BitConverter;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.apache.log4j.Logger;
/**
 *
 * @author mis
 */


public class CommandClient  extends BaseThread
    implements com.mayforever.network.newtcp.ClientListener{
    private com.mayforever.network.newtcp.TCPClient tcpClient = null;

    public TCPClient getTcpClient() {
        return tcpClient;
    }
    Logger logger = null;
    private byte CONTROL = 1;
    private byte[] tempData = null;
    private Queue<byte[]> dataProcess = null;
    private Queue<byte[]> dataToValidate = null;
//    private 
    public CommandClient(){
        boolean isAlive = false;
        logger = Logger.getLogger("COMMAND CLIENT");
        while(!isAlive){
            try{
            
                this.tcpClient = new com.mayforever.network.newtcp.TCPClient(App.serverIP, App.serverPort);
                this.tcpClient.addListener(this);
                logger.info("server reconnected sucessfuly");
                isAlive = true;
            }catch (NullPointerException e){
                logger.info("reconecting to server");
//                App.imageClient = new ImageClient();
                
                try {
                    java.lang.Thread.sleep(3000);
                } catch (InterruptedException ex) {
    //                java.util.logging.Logger.getLogger(ImageClient.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        
//        this.tcpClient = new com.mayforever.network.newtcp.TCPClient(App.serverIP, App.serverPort);
//        this.tcpClient.addListener(this);
        
        this.sendAuthentication();
        logger.debug("the authentication has been sent");
        dataProcess = new Queue<>();
        dataToValidate = new Queue<>();
        this.startThread();
        new Processor();
    }
    @Override
    public void packetData(byte[] bytes) {
        this.dataToValidate.add(bytes);
//        logger.debug(Arrays.toString(bytes));
    }

    @Override
    public void socketError(Exception excptn) {
        excptn.printStackTrace();
        App.commandClient = new CommandClient();
    }
    
    private void reconnect(){
        
    }
    
    private void sendAuthentication(){
        Authenticate authenticate = new Authenticate();
        
        authenticate.setUsername(App.username);
        authenticate.setPassword(App.password);
        authenticate.setControl(CONTROL);
        
        try {
            this.tcpClient.sendPacket(authenticate.toBytes());
        } catch (IOException ex) {
           logger.error(ex.getMessage());
           logger.error(ex.getClass());
        }
    }

    @Override
    public void run() {
        while(this.getServiceState() == com.mayforever.thread.state.ServiceState.RUNNING) {
            byte[] data;
            try {
                data = dataToValidate.get();
                if (data != null) {
//                    System.out.println("data to process :" + Arrays.toString(data));
                    if (tempData == null || tempData.length  == 0) {
			tempData = data;
                    }else {
                            // tempData = new byte[tempData.length+data.length];
                            byte[] dataPending = tempData;
                            tempData = new byte[tempData.length + data.length];
                            System.arraycopy(dataPending, 0, tempData, 0, dataPending.length);
                            System.arraycopy(data, 0, tempData, dataPending.length, data.length);
                    }

                    logger.debug(tempData.length);
                    int dataProcessSize = BitConverter.bytesToInt(tempData, 1, ByteOrder.BIG_ENDIAN);

                    logger.debug(dataProcessSize);
		
                    do {
			if(dataProcessSize == tempData.length) {
				this.dataProcess.add(tempData);
				tempData = new byte[0];
			}else if(dataProcessSize < tempData.length) {
//				byte[] newTempData = tempData;
                                byte[] dataToProcess = new byte[dataProcessSize];
				System.arraycopy(tempData, 0, dataToProcess, 0, dataProcessSize);
				this.dataProcess.add(dataToProcess);
				byte[] newtempData = new byte[tempData.length - dataProcessSize];
				System.arraycopy(tempData, dataProcessSize, newtempData, 0,  tempData.length - dataProcessSize);
				this.tempData = newtempData;
				logger.debug("newtempData length : " + newtempData.length);
			}
			logger.debug("tempData length : " + tempData.length);
			if(this.tempData.length < 5) {
				break;
			}else {
				dataProcessSize = BitConverter.bytesToInt(tempData, 1, ByteOrder.BIG_ENDIAN);
			}
			logger.debug("dataProcessSize length : " + dataProcessSize );
                    }while(tempData.length > dataProcessSize);
                }
            } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
        }
    }
    
    private class Processor extends BaseThread{
        
        private Processor(){
            this.startThread();
        }
        
        @Override
        public void run() {
            while(this.getServiceState() == com.mayforever.thread.state.ServiceState.RUNNING){
                byte[] data = null;
                try {
                    data = dataProcess.get();
                    if(data != null){
                        if  (data[0] == 6){
                            CommandRequest commandRequest = new CommandRequest();
                            logger.debug(data.length);
                            commandRequest.fromBytes(data);
                            
                            logger.debug(commandRequest.getTotalSize());
                            doEvent(commandRequest);
                            logger.debug(Arrays.toString(data));
                        }else if (data[1] == 7){
                            
                        }
                    }
                } catch (InterruptedException ex) {
//                    java.util.logging.Logger.getLogger(CommandClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }  
        }
    }
    
    private void doEvent(CommandRequest commandRequest){
//		CommandRequest commandRequest = new CommandRequest();
		
		byte command = (byte)999;
		try{
//			commandRequest.fromBytes(data);
			command = commandRequest.getCommand();
		}catch (Exception e){
			System.out.println("failed to catch real command please repeat");
//			return;
		}
		
		if(command == Command.MOUSE_PRESSED){
//			System.out.println(commandRequest.getParams()[0]);
			App.imageClient.getRobot().mousePress(commandRequest.getParams()[0]);
//			robot.mouseMove(commandRequest.getParams()[1],
//			commandRequest.getParams()[2]);
			
		}else if (command == Command.MOUSE_RELEASED){
			
			App.imageClient.getRobot().mouseRelease(commandRequest.getParams()[0]);
		}else if (command == Command.MOUSE_MOVE){
			App.imageClient.getRobot().mouseMove(commandRequest.getParams()[0],
					commandRequest.getParams()[1]);
		}else if (command == Command.KEY_PRESSED){
			App.imageClient.getRobot().keyPress(commandRequest.getParams()[0]);
		}else if (command == Command.KEY_RELEASED){
			App.imageClient.getRobot().keyRelease(commandRequest.getParams()[0]);
		}
	}
}
