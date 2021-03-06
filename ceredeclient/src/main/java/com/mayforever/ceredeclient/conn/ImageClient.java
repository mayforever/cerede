/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mayforever.ceredeclient.conn;

import com.mayforever.ceredeclient.App;
import com.mayforever.ceredeclient.RemoteViewer;
import com.mayforever.ceredeclient.model.Authenticate;
import com.mayforever.ceredeclient.model.ChunkImageRequest;
import com.mayforever.ceredeclient.model.ChunkImageResponse;
import com.mayforever.ceredeclient.model.ImageRequest;
import com.mayforever.ceredeclient.model.ImageResponse;
import com.mayforever.queue.Queue;
import com.mayforever.thread.BaseThread;
import com.mayforever.tools.BitConverter;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;

/**
 *
 * @author mis
 */
public class ImageClient extends BaseThread 
        implements com.mayforever.network.newtcp.ClientListener{
    private com.mayforever.network.newtcp.TCPClient tcpClient = null;
    Logger logger = null;
    private byte CONTROL = 0;

    public Robot getRobot() {
        return robot;
    }
    private Robot robot = null;
    private byte[] tempData = null;
    private Queue<byte[]> dataProcess = null;
    private Queue<byte[]> dataToValidate = null;
    private ArrayList<byte[]> sendArraylistImage=null;
    private SessionRecieveMonitor sessionCleaner = null;
    private HashMap<String, byte[]> mapTempData = null;
    private HashMap<String, ArrayList<byte[]>> mapSendImageArrayList = null;
    private HashMap<String, byte[]> mapRecieverBufferImage = null;
    public ImageClient(){
        mapSendImageArrayList = new HashMap<>();
        logger = Logger.getLogger("IMAGECLIENT");
        boolean isAlive = false;
        while(!isAlive){
            try{
            
                this.tcpClient = new com.mayforever.network.newtcp.TCPClient(App.serverIP, App.serverPort);
                this.tcpClient.addListener(this);
                logger.debug("server reconnected sucessfuly");
                isAlive = true;
            }catch (NullPointerException e){
                logger.debug("reconecting to server");
//                App.imageClient = new ImageClient();
                
                try {
                    java.lang.Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    logger.warn("ERROR :"+ex.getMessage());
                    ex.printStackTrace();
                }

            }
        }
        
        this.tcpClient.setAllocation(2048*5);
        dataProcess = new Queue<>();
        dataToValidate = new Queue<>();
        new Processor();
        mapRecieverBufferImage = new HashMap<>();
        logger.debug("the authentication has been sent");
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
        try {
                robot = new Robot(graphicsDevice);
        } catch (AWTException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        this.startThread();
        
        this.sendAuthentication();
        sessionCleaner=new SessionRecieveMonitor();
        sessionCleaner.startSessionCleaner();
    }
    
    @Override
    public void packetData(byte[] data) {

	this.dataToValidate.add(data);
    }

    @Override
    public void socketError(Exception excptn) {
            this.stopThread();

            App.imageClient = new ImageClient();
            excptn.printStackTrace();
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
    
    public void sendImagePacket (byte[] data){
        try {
            this.tcpClient.sendPacket(data);
            logger.debug("data has been sent : "+data.length);
        } catch (IOException ex) {
           logger.error(ex.getMessage());
           logger.error(ex.getClass());
        }
    }

    @Override
    public void run() {
        while(this.getServiceState() == com.mayforever.thread.state.ServiceState.RUNNING) {
            byte[] data = null;
            try {
                data = dataToValidate.get();
                if (data != null) {

                    if (tempData == null || tempData.length  == 0) {
			tempData = data;
                    }else {

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
                        if(tempData == null){
                            logger.warn("tempData is empty");
                        }
                    }while(tempData.length > dataProcessSize);
                }
            } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
        }
    }
    
    public byte[] gettingScreenShot(){
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = null;
		capture = robot.createScreenCapture(screenRect);
		// System.out.
                byte[] bufferImage = null;
		if(capture!=null){

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] baosArrays = null;
			try {
				ImageIO.write(capture, "png", baos);
				baos.flush();

				bufferImage = baos.toByteArray();
                                baos.close();
                                baosArrays = baos.toByteArray();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return bufferImage;
		}
		return null;
	}
    
    public ArrayList<byte[]> chunckData(byte[] data, int chunkCount){
    	int dataCount = data.length;
    	ArrayList<byte[]> chunkdata = new ArrayList<byte[]>(); 

    	int lengthPerChunk = Math.floorDiv(dataCount ,chunkCount);
    	int indexOfData = 0;
    	for(int i = 1; i <= chunkCount ; i++) {
    		if(chunkCount ==i) {
    			byte[] dataChunk = new byte[data.length - indexOfData + 5];
    			System.arraycopy(data, indexOfData, dataChunk, 0, lengthPerChunk);
    			indexOfData+=lengthPerChunk;

    			chunkdata.add(dataChunk);
    		}else {
    			byte[] dataChunk = new byte[lengthPerChunk];
    			System.arraycopy(data, indexOfData, dataChunk, 0, lengthPerChunk);
    			indexOfData+=lengthPerChunk;
;
    			chunkdata.add(dataChunk);
    		}
    		
    	}
    	return chunkdata;
    	
    }
    
    private class Processor extends BaseThread{
        Processor(){
            this.startThread();
        }
        @Override
        public void run() {
            while(this.getServiceState() == com.mayforever.thread.state.ServiceState.RUNNING) {
            byte[] data = null;
            try {
                data = dataProcess.get();
                if (data != null && data.length != 0) {
//                    System.out.println("data to process :" + Arrays.toString(data));
                    
                    if(data[0] == 2) {
                       
                        ImageRequest imageRequest = new ImageRequest();
                        imageRequest.fromBytes(data);
                        
                        ImageResponse imageResponse = new ImageResponse();
                        imageResponse.setRequestorHash(imageRequest.getRequestorHash());
                        imageResponse.setHash(imageRequest.getHash());
                        imageResponse.setChunkCount(imageRequest.getTotalChunk());
                        imageResponse.setResult((byte)0);
                        imageResponse.setHeight(getHeight());
                        imageResponse.setWidth(getWidth());
                        
                        sendArraylistImage = chunckData(gettingScreenShot(), imageRequest.getTotalChunk());
                        mapSendImageArrayList.put(imageRequest.getRequestorHash(), sendArraylistImage);
                        byte[] dataToSend = null;
                        try {
                            dataToSend = imageResponse.toBytes();
                            tcpClient.sendPacket(dataToSend);
                            logger.debug("Message Response Send at 2 ");
                            logger.debug("data response result : "+ dataToSend.length);
                        } catch (IOException ex) {
                            java.util.logging.Logger.getLogger(ImageClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }else if(data[0] == 3){
                        ImageResponse imageResponse = new ImageResponse();
                        imageResponse.fromBytes(data);

//                        logger.info("the result is  "+imageResponse.getResult());
                        
                        if (imageResponse.getResult() == 0){
                            if(App.mapRemoteViewer
                                    .containsKey(imageResponse.getHash())){
                                RemoteViewer remoteViewer = App.mapRemoteViewer
                                     .get(imageResponse.getHash());
                                remoteViewer.updateJScrollViewSize(imageResponse);
                                logger.debug("if "+remoteViewer.chunkIndex+"<"+ imageResponse.getChunkCount());
                                if(remoteViewer.chunkIndex< imageResponse.getChunkCount()){
                                    ChunkImageRequest chunkImageRequest = new ChunkImageRequest();
                                    chunkImageRequest.setRequestorHash(imageResponse.getRequestorHash());
                                    chunkImageRequest.setHash(imageResponse.getHash());
                                    chunkImageRequest.setChunkNumber(remoteViewer.chunkIndex);
                                    chunkImageRequest.setProtocol((byte)4);

                                    App.imageClient.sendImagePacket(chunkImageRequest.toBytes());
                                    remoteViewer.chunkIndex++;
                                }
                                else{
                                    remoteViewer.chunkIndex = 0;
                                    ImageRequest imageRequest = new ImageRequest();

                                    imageRequest.setHash(imageResponse.getHash());
                                    imageRequest.setRequestorHash(imageResponse.getRequestorHash());
                                    imageRequest.setTotalChunk(App.chunkCount);
    //                                this.loadingFrame.getjLprocess().setText("Sending Image Request To Server ...");
                                    App.imageClient.sendImagePacket(imageRequest.toBytes());
                                }
                            }
                              
                        }else{
                                
                            RemoteViewer remoteViewer = App.mapRemoteViewer
                                 .get(imageResponse.getHash());
                            App.mapRemoteViewer.remove(imageResponse.getHash())
                            remoteViewer.dispose();
//                            logger.info("the result is  "+imageResponse.getResult());
                        }
                        
                    }else if(data[0] == 4){
                        ChunkImageRequest chunkImageRequest = new ChunkImageRequest();
                        chunkImageRequest.fromBytes(data);
                        
                        ChunkImageResponse chunkImageResponse = new ChunkImageResponse();
                        chunkImageResponse.setHash(chunkImageRequest.getHash());
                        chunkImageResponse.setRequestorHash(chunkImageRequest.getRequestorHash());
                        chunkImageResponse.setBufferImage(mapSendImageArrayList
                                .get(chunkImageRequest.getRequestorHash()).get(chunkImageRequest.getChunkNumber()));
                        chunkImageResponse.setTotalChunk(App.chunkCount);
//                        chunkImageResponse.setBufferImage(sendArraylistImage.get(chunkImageRequest.getChunkNumber()));
                        chunkImageResponse.setChunkNumber(chunkImageRequest.getChunkNumber());
                        chunkImageResponse.setProtocol((byte)5);
                        
                        sendImagePacket(chunkImageResponse.toBytes());
                    }else if(data[0] == 5){
                        logger.debug("Image Response Receive");
                        updateLastSessionDate();
                        ChunkImageResponse chunkImageResponse = new ChunkImageResponse();
//                        logger.info(data.length);
                        chunkImageResponse.fromBytes(data);
                        if(!mapRecieverBufferImage.containsKey(chunkImageResponse.getHash())){
                            mapRecieverBufferImage.put(chunkImageResponse.getHash(), new byte[0]);
                        }
                        byte[] receiveBufferImage = mapRecieverBufferImage.get(chunkImageResponse.getHash());
                        int oldBufferSize = receiveBufferImage.length;
                        byte[] bufferImageChunk = chunkImageResponse.getBufferImage();
                        byte[] oldReceiveBufferImage = receiveBufferImage;
                        receiveBufferImage = new byte[oldBufferSize + bufferImageChunk.length];
                        System.arraycopy(oldReceiveBufferImage, 0, receiveBufferImage,
                                0, oldBufferSize);
                        System.arraycopy(bufferImageChunk, 0, receiveBufferImage,
                                oldBufferSize, bufferImageChunk.length);

                        mapRecieverBufferImage.put(chunkImageResponse.getHash(), receiveBufferImage);
                        if(chunkImageResponse.getChunkNumber() == App.chunkCount-1){
                           
                            ImageRequest imageRequest = new ImageRequest();
        
                            imageRequest.setHash(chunkImageResponse.getHash());
                            imageRequest.setRequestorHash(chunkImageResponse.getRequestorHash());
                            imageRequest.setTotalChunk(App.chunkCount);
                            App.imageClient.sendImagePacket(imageRequest.toBytes());
                            logger.debug("Image Request Send"); 
                            logger.debug(receiveBufferImage.length);
                            RemoteViewer remoteViewer = App.mapRemoteViewer
                                    .get(chunkImageResponse.getHash());
                            remoteViewer.updateJScrollView(receiveBufferImage);
                            remoteViewer.chunkIndex = 0;
                            mapRecieverBufferImage.put(chunkImageResponse.getHash(), new byte[0]);

                        }else{
                            if(App.mapRemoteViewer
                                    .containsKey(chunkImageResponse.getHash())){
                                RemoteViewer remoteViewer = App.mapRemoteViewer
                                    .get(chunkImageResponse.getHash());
                                remoteViewer.sendChunkImageRequest(chunkImageResponse);
                            }
                            
                        }
                        
                    }
                }
            } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }catch (ArrayIndexOutOfBoundsException eiobe){
//                eiobe.printStackTrace();
                socketError(eiobe);
            }
        }
        }
        
        
    }
    
    private int getHeight(){
        Dimension dim=Toolkit.getDefaultToolkit().getScreenSize();
        return dim.height;
    }
    
    private int getWidth(){
        Dimension dim=Toolkit.getDefaultToolkit().getScreenSize();
        return dim.width;
    }
    private long lastImageDataTimeProcess = 0l;
    private void updateLastSessionDate(){
        Date date = new Date();
        lastImageDataTimeProcess = date.getTime();
    }
    private class SessionRecieveMonitor extends BaseThread{
        SessionRecieveMonitor(){
            
        }
        
        public void startSessionCleaner(){
            this.startThread();
        }
        
        public void stopSessionCleaner(){
            this.stopThread();
        }
        @Override
        public void run() {
            while(true){
                try {
                    java.lang.Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    logger.warn("ERROR :"+ex.getMessage());
                    ex.printStackTrace();
                }
                if(this.getServiceState() ==  com.mayforever.thread.state.ServiceState.RUNNING)
                {
                    
                    Date date = new Date();
                    long currentTime = date.getTime();
                    long timeDiff = currentTime - lastImageDataTimeProcess;
                    
                    int diffsec = (int) (timeDiff / (1000));
                    
                    if(diffsec >= 5){
                        tempData = null;
                    }
                }
            }
        }
    }
}
