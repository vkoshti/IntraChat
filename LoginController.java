package intranet.source.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.concurrent.Task;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javafx.application.Platform;
import intranet.source.client.loader.UILoader;

public class LoginController
{
	@FXML private Button submitbutton;
	@FXML private TextField yname;
	@FXML private Label status;
	private Socket client;

	public LoginController()
	{
		try
		{
			client = new Socket("127.0.0.1",4005);
		}
		catch(Exception e)
		{
			System.out.println("Error occurred");
		}
	}

	public void joinChat(ActionEvent e)
	{
		String yName = yname.getText();
		if (yName.isEmpty() || !yName.matches("([a-z]|[A-Z])+"))
		{
			status.setText("Text should contain alphabets, no space and no numbers");
			clearStatus();
			return;
		}
		System.out.println("in join chat");
		UILoader.getObject().openChatBox(yName,client);
		System.out.println("Method called in UILoader");
	}

	Service<Void> clearstatus = new Service<Void>()
	{
		public Task<Void> createTask()
		{
			return new  Task<Void>()
			{
				protected Void call()
				{
					try
					{
						Thread.sleep(3000);
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					Platform.runLater(new Runnable()
						{
							public void run()
							{
								status.setText("");
							}
						});
					return null;
				}
			};
		}
	};

	public void clearStatus()
	{
		if(!clearstatus.isRunning())
		{
			clearstatus.reset();
			clearstatus.start();
		}
	}
}