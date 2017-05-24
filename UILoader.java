package intranet.source.client.loader;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import java.net.Socket;
import intranet.source.client.controller.UIController;

public class UILoader extends Application
{
	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage)
	{
		try
		{
			GridPane root = FXMLLoader.load(getClass().getResource("/LoginInterface.fxml"));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setResizable(false);
			
		//	scene.getStylesheets().add("/Mycss.css");
			mainstage = stage;
			object = this;
			stage.show();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void openChatBox(String name1,Socket client)
	{
		try
		{
			this.client = client;
			System.out.println("Method called");
			mainstage.close();
			System.out.println("loading interface");
			FXMLLoader loader =new  FXMLLoader(getClass().getResource("/UserInterface.fxml"));
			UIController uicontroller = new UIController(name1,client);
			loader.setController(uicontroller);
			System.out.println("Loaded interface and uicontroller set");
			BorderPane root = loader.load();
			Scene scene = new Scene(root);
			chatstage = new Stage();
			chatstage.setScene(scene);
			scene.getStylesheets().add("/Mycss.css");
			chatstage.setTitle(name1);
			chatstage.show();
			chatstage.setResizable(false);
		}
		catch(Exception e)
		{
			System.out.println("In loading second ui");
			e.printStackTrace();
		}


	}

	public void stop()
	{
		try
		{
			this.client.close();
		}
		catch(Exception e)
		{

		}
	}

	public Stage getStage()
	{
		return chatstage;
	}

	public static UILoader getObject()
	{
		return object;
	}
	private Stage mainstage, chatstage;
	private static UILoader object;
	private Socket client;
}