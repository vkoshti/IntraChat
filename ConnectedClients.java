package intranet.source.users;

import java.net.Socket;
import java.io.DataOutputStream;
import java.io.DataInputStream;

public class ConnectedClients
{
	private Socket client;
	private DataInputStream dis;
	private DataOutputStream dos;
	private String name;

	public ConnectedClients(String name,Socket client,DataInputStream dis,DataOutputStream dos)
	{
		this.name = name;
		this.client = client;
		this.dis = dis;
		this.dos = dos;
	}

	public Socket getSocket()
	{
		return this.client;
	}

	public String getName()
	{
		return this.name;
	}

	public DataOutputStream getoutputstream()
	{
		return this.dos;
	}

	public DataInputStream getinputstream()
	{
		return this.dis;
	}
	public boolean equals(Object o)
	{
		return (o instanceof ConnectedClients) && (((ConnectedClients)o).name.equals(name));
	}
}