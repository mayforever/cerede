package com.mayforever.ceredeserver.conn.data;


//import com.mayforever.ceredeserver.Launcher;
import com.mayforever.ceredeserver.conn.TCPClient;


public class TCPData 	{
	
	public TCPData (String user, String password) {

		this.user = user;
		this.password = password;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public TCPClient getTcpImageClient() {
		return tcpImageClient;
	}
	public void setTcpImageClient(TCPClient tcpImageClient) {
		this.tcpImageClient = tcpImageClient;
	}
	public TCPClient getTcpCommandListener() {
		return tcpCommandListener;
	}
	public void setTcpCommandListener(TCPClient tcpCommandListener) {
		this.tcpCommandListener = tcpCommandListener;
	}
	private String user = null;
	private String password = null;

	
	private TCPClient tcpImageClient = null;
	private TCPClient tcpCommandListener = null;
	

}
