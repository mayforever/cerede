package com.mayforever.ceredeserver.session;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.mayforever.ceredeserver.Launcher;
import com.mayforever.ceredeserver.model.ChunkImageRequest;
import com.mayforever.ceredeserver.model.ChunkImageResponse;
import com.mayforever.ceredeserver.model.CommandRequest;
import com.mayforever.ceredeserver.model.ImageRequest;
import com.mayforever.ceredeserver.model.ImageResponse;

public class SessionProcessor extends com.mayforever.thread.BaseThread{

	private Logger logger = null;
	
	public SessionProcessor() {
		// TODO Auto-generated constructor stub
		logger = Logger.getLogger("SessionProcessor");
	}
	
	SessionProcessor(SessionManager sessionManager){

	}
	
	public void startSession(){	
		this.startThread();
	}
	
	public void stopSession(){
		this.stopThread();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(this.getServiceState() == com.mayforever.thread.state.ServiceState.RUNNING) {
			byte[] data;
			try {
				data = Launcher.dataProcess.get();
				if (data != null) {
					if(data[0] == 2) {
						ImageRequest imageRequest = new ImageRequest();
						imageRequest.fromBytes(data);
						if(Launcher.controllerMap.containsKey(imageRequest.gethash())) {
							this.logger.debug("hash to : " + imageRequest.gethash());
							this.logger.debug("hash to user : " +  Launcher.controllerMap.get(imageRequest.gethash()).getUser());
							this.logger.debug("hash From : " + imageRequest.getRequestorHash());
							this.logger.debug("hash From user : " + Launcher.controllerMap.get(imageRequest.getRequestorHash()).getUser());
							try {
								Launcher.controllerMap.get(imageRequest.gethash()).getTcpImageClient().getTcpClient().sendPacket(data);
								logger.debug("Image Request send");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else {
							ImageResponse imageResponse = new ImageResponse();
							imageResponse.setHash(imageRequest.gethash());
							imageResponse.setRequestorHash(imageRequest.getRequestorHash());
							imageResponse.setWidth(0);
							imageResponse.setHeight(0);
//							imageResponse.setBufferImage(new byte[0]);
							imageResponse.setResult((byte)1);
							logger.debug("Image Response Send");
							try {
								Launcher.controllerMap.get(imageResponse.getRequestorHash()).getTcpImageClient()
											.getTcpClient().sendPacket(imageResponse.toBytes());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
//							Launcher.controllerMap.get(imageResponse.getRequestorHash()).deleteToMap();
						}
					}
					else if(data[0] == 3){
						logger.debug("image response to process");
						ImageResponse imageResponse = new ImageResponse();
						imageResponse.fromBytes(data);
						
//						logger.debug("Image Buffer : "+new java.lang.String(imageResponse.getBufferImage()));
						if(Launcher.controllerMap.containsKey(imageResponse.getRequestorHash())) {
							try {
								Launcher.controllerMap.get(imageResponse.getRequestorHash()).getTcpImageClient()
									.getTcpClient().sendPacket(imageResponse.toBytes());
//								logger.debug("Image Response Send to requestor");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else {
							imageResponse = new ImageResponse();
							imageResponse.setHash(imageResponse.getHash());
							imageResponse.setRequestorHash(imageResponse.getRequestorHash());
							imageResponse.setWidth(0);
							imageResponse.setHeight(0);
//							imageResponse.setBufferImage(new byte[0]);
							imageResponse.setResult((byte)2);
							
							logger.debug("Message Response Send");
							try {
								Launcher.controllerMap.get(imageResponse.getHash()).getTcpImageClient()
											.getTcpClient().sendPacket(imageResponse.toBytes());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
//							Launcher.controllerMap.get(imageResponse.getRequestorHash()).deleteToMap();
						}
					}
					else if(data[0] == 4){
						ChunkImageRequest chunkImageRequest = new ChunkImageRequest();
						chunkImageRequest.fromBytes(data);
						
						if(Launcher.controllerMap.containsKey(chunkImageRequest.getHash())) {
							try {
								Launcher.controllerMap.get(chunkImageRequest.getHash()).getTcpImageClient()
									.getTcpClient().sendPacket(chunkImageRequest.toBytes());
//								logger.debug("Image Response Send to requestor");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					else if(data[0] == 5){
						ChunkImageResponse chunkImageResponse = new ChunkImageResponse();
						chunkImageResponse.fromBytes(data);
						
						if(Launcher.controllerMap.containsKey(chunkImageResponse.getRequestorHash())) {
							try {
								Launcher.controllerMap.get(chunkImageResponse.getRequestorHash()).getTcpImageClient()
									.getTcpClient().sendPacket(chunkImageResponse.toBytes());
//								logger.debug("Image Response Send to requestor");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else {
							
						}
					}
					else if(data[0] ==6){
						CommandRequest commandRequest = new CommandRequest();
						commandRequest.fromBytes(data);
						
						if(Launcher.controllerMap.containsKey(commandRequest.getHash())) {
							try {
								synchronized (Launcher.controllerMap.get(commandRequest.getHash())) {
									Launcher.controllerMap.get(commandRequest.getHash()).getTcpCommandListener()
										.getTcpClient().sendPacket(commandRequest.toBytes());
								}
								
//								logger.debug("Image Response Send to requestor");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else {
							
						}
					}
					
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
}
