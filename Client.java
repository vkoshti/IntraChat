import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Scanner;

class Client
{

	public static void main(String[] args) {
		try
		{

			Socket s = new Socket("127.0.0.1",4005);
			Scanner scan = new Scanner(System.in);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			System.out.println(dis.readUTF());
			dos.writeUTF(scan.nextLine());
			System.out.println(dis.readUTF());
			dos.writeUTF(scan.nextLine());
			new Thread(new Runnable()
			{
				public void run()
				{
					while(true)
					{
						try
						{
							dos.writeUTF(scan.nextLine());
						}
						catch(Exception e)
						{

						}
						
					}
				}
			}).start();
			new Thread(new Runnable()
				{
					public void run()
					{
						while(true)
						{
							try
							{
								System.out.println(dis.readUTF());
							}
							catch(Exception e)
							{

							}
						}
					}
				}).start();
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}