package com.mayforever.ceredeserver.conf;

public class Configuration {
	
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String configPath) {
		this.filePath = configPath;
	}
	public int getProcessorCount() {
		return processorCount;
	}
	public void setProcessorCount(int processorCount) {
		this.processorCount = processorCount;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	private String filePath;
	private int processorCount;
	private int port;
	private String address;
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
