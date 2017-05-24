package intranet.source.server;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import intranet.source.users.ConnectedClients;
import java.util.LinkedHashMap;
import intranet.source.database.MessageDatabase;


class CatchClient extends Thread
{
	private Socket client;
	private DataOutputStream dos;
	private DataInputStream dis;
	private LinkedHashMap<String,ConnectedClients> users;
	private LinkedHashSet<String> userlist;
	private String bmsg;
	private MessageDatabase db;
	private String msg;
//	private LinkedHashMap<String,String> pendingMessages;
	private int oneTime = 0;
	CatchClient(Socket client,LinkedHashMap<String,ConnectedClients> users,LinkedHashSet<String> userlist)
	{
		this.client = client;
		try
		{
			this.dos = new DataOutputStream(this.client.getOutputStream());
			this.dis = new DataInputStream(this.client.getInputStream());
		}
		catch(Exception e)
		{
			System.out.println("Constructor try");
			e.printStackTrace();
		}

		this.users = users;
		this.userlist = userlist;
	//	db = new MessageDatabase();
	}

	public void run()
	{
		try
		{
			boolean t = true;
			System.out.println("Run Started");
			System.out.println("Reading from");
			String from = dis.readUTF();
			System.out.println("From:"+from);
			userlist.add(from);
			ConnectedClients user = new ConnectedClients(from,client,dis,dos);
			if(!users.containsValue(user))
			{
				users.put(from,user);
			}
			new Thread(new Runnable(){
				public void run()
				{
					
					
					for(String s:userlist)
					{
						String data = new String();
						for(String s1:userlist)
						{
							data = data+s1+"@";
						}
						ConnectedClients c = users.get(s);

							try{
								DataOutputStream doo = new DataOutputStream(c.getSocket().getOutputStream());
								doo.writeUTF("userlistfromserver");
								doo.writeUTF(data);
							}
							catch(Exception e)
							{
								System.out.println("In sending userlist");
							}
					}

				}
			}).start();
			while(client.isConnected())
			{
				try
				{	
					/*if(oneTime==0)
					{
						pendingMessages = db.selectMessage();
						for(int i = 0;i<pendingMessages.size();i++)
						{
							dos.writeUTF(pendingMessages.getKey(i));
							dos.writeUTF(pendingMessages.getValueAt(i));
						}
					}*/
					boolean check = true;
					String command = dis.readUTF();
					String fromwhere = dis.readUTF();
					System.out.println("From where:"+fromwhere);
					String toClient = dis.readUTF();
					System.out.println("to client:"+toClient);
					if (command.equals("@messageTo@"))
					{
						ConnectedClients c = users.get(toClient);
						if (c!=null)
						{
							Socket s = c.getSocket();
							DataInputStream todis = new DataInputStream(s.getInputStream());
							DataOutputStream todos = new DataOutputStream(s.getOutputStream());
							todos.writeUTF("getMesSagefrom");
							todos.writeUTF("p");
							todos.writeUTF(from);
							msg = dis.readUTF();
							todos.writeUTF(msg);
							System.out.println("messgage:"+msg);
							
						}
					/*	else 
						{
							new Thread(new Runnable()
							{
								public void run()
								{
									addToDatabase(fromwhere,toClient,msg);
								}
							}).start();
							
						}*/
					}
					if(command.equals("bcast"))
					{
						bmsg = dis.readUTF();

						for(String s:userlist)
						{
							if(!s.equals(fromwhere))
							{
					
						
								new Thread(new Runnable()
									{
										public void run()
										{
											
											try{
												
												Thread.sleep(100);
												ConnectedClients c1 = users.get(s);
												if(c1!=null)
												{
												DataOutputStream doo = new DataOutputStream(c1.getSocket().getOutputStream());
												doo.writeUTF("getMesSagefrom");
												doo.writeUTF("b");
												doo.writeUTF(fromwhere);
												doo.writeUTF(bmsg);
												System.out.println("messgage:"+bmsg);}
											}
											catch(Exception e)
											{
												System.out.println("In sending userlist");
											}
										}
									}).start();
								}
						}
					}
				}

				catch(Exception e)
				{
					System.out.println("In first try");
					client.close();
					dos.close();
					dis.close();
					if(!client.isConnected())
					{
						System.out.println("User disconnected");
					}
					return;
				}
			}

		}


		catch(Exception e)
		{
			System.out.println("In CatchClient second try");
			if(!client.isConnected())
					{
						System.out.println("User not connected");
					}
			return;
		}
	}

/*	public void addToDatabase(String from,String to,String message)
	{
		db.insertMessage(from,to,message);
	}*/
}

class Server
{
	private ServerSocket serversocket;
	private LinkedHashMap<String,ConnectedClients> users;
	private LinkedHashSet<String> userlist;
	Server()
	{
		users = new LinkedHashMap<String,ConnectedClients>();
		userlist = new LinkedHashSet<String>();
		try
		{
			serversocket = new ServerSocket(4005);	

		}
		catch(Exception e)
		{
			System.out.println("Creating server failed...");
			e.printStackTrace();
		}
	}

	public void acceptClient()
	{
		while(true)
		{
			try
			{
				Socket client = serversocket.accept();
				System.out.println("Connected : "+client.getInetAddress());
				new Thread(new CatchClient(client,users,userlist)).start();
			}
			catch(Exception e)
			{
				System.out.println("In Server class");
				e.printStackTrace();	
			}

		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.acceptClient();
	}
}
