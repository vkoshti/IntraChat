package intranet.source.client.controller;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.Priority;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.Tab;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;


public class UIController  implements Initializable
{
	@FXML private TabPane tabPane;
	@FXML private TextField msgarea;
	@FXML private Button send;
	@FXML private ComboBox<String> selectuser;
	@FXML private VBox tabview;
	private Socket client;
	private DataInputStream dis;
	private DataOutputStream dos;
	private String name;
	private String msg;
	private String from;
	private String frommsg;
	private ObservableList<String> userlist;
	private String borp;

	public UIController(String name,Socket client)
	{
		this.client = client;
		this.name = name;
		try
		{
			dis = new DataInputStream(client.getInputStream());
			dos = new DataOutputStream(client.getOutputStream());
			System.out.println("Writing name to server");
		
		}catch(Exception e){System.out.println("Constructor try");e.printStackTrace();}
		new Thread(new Runnable()
			{
				public void run()
				{
					createTo.reset();createTo.start();
				}
			}).start();
		//createDatabase();

	}

	public void sendMessage(ActionEvent e)
	{
		if (!sendmsg.isRunning())
		{
			sendmsg.reset();
			sendmsg.start();
		}
	}

	Service<String> sendmsg = new Service<String>()
	{
		public Task<String> createTask()
		{
			return new Task<String>()
			{
				protected String call()
				{
					
					msg = msgarea.getText();
					if(!msg.isEmpty())
					{
						try
						{
							if(tabPane.getSelectionModel().getSelectedItem().getText().equals("Broadcast"))
							{
								dos.writeUTF("bcast");	
								dos.writeUTF(name);
								dos.writeUTF("Broadcast");
								dos.writeUTF(msg);
							}
							else
							{
								dos.writeUTF("@messageTo@");
								System.out.println("I am :"+name);
								System.out.println("message :"+msg);
								dos.writeUTF(name);
								System.out.println("Current tab:"+tabPane.getSelectionModel().getSelectedItem().getText());
								dos.writeUTF(tabPane.getSelectionModel().getSelectedItem().getText());
								dos.writeUTF(msg);
							}
							Platform.runLater(new Runnable(){
									public void run()
									{
										if(!outgoingMessage.isRunning())
										{
											outgoingMessage.reset();
											outgoingMessage.start();
											msgarea.clear();
										}
									}
								});

						}
						catch(Exception e)
						{
							System.out.println("In send msg service try");
							System.out.println(e);
						}
					}
					
					

					return null;
				}
			}; 
		}
	};

	Service<Void> createTo = new Service<Void>(){
		public Task<Void> createTask()
		{
			return new Task<Void> ()
			{
				protected Void call()
				{
					try
					{
						dos.writeUTF(name);	
					}
					catch(Exception e)
					{
						System.out.println("In create to service ");
						System.out.println(e);
					}
					
					return null;
				}
			};
		}
	};

	Service<Void> incomingMessage = new Service<Void>(){
		public Task<Void> createTask()
		{
			return new Task<Void>(){
				public Void call()
				{
					try{
					if(frommsg.length()>0)
					{
						System.out.println("incomingMessage thread called");
						TilePane tilepane = new TilePane();
						tilepane.setPrefColumns(1);
						tilepane.setPrefRows(1);
						tilepane.setMaxWidth(Double.MAX_VALUE);
						tilepane.setMaxHeight(Double.MAX_VALUE);
						VBox vbox = new VBox();
						
						tilepane.setAlignment(Pos.TOP_LEFT);
						TilePane.setAlignment(vbox,Pos.TOP_LEFT);
						Label fromMsg = new Label(from);
						fromMsg.setId("ousername");
						Label message = new Label(frommsg);
						message.setId("outgoing");
						message.setStyle("-fx-text-fill:black;");
						message.setWrapText(true);
						message.setMaxWidth(300);
						vbox.getChildren().addAll(fromMsg,message);
						tilepane.getChildren().addAll(vbox);
						System.out.println("____________Tabs available : "+tabPane.getTabs().size());
						System.out.println("____________Getting tab of : "+from);
						System.out.println("Index : "+tabPane.getTabs().indexOf(new Tab(from)));
						Tab t;
						if(borp.equals("b"))
						{
							from = "Broadcast";
						}
						for(Tab tabs:tabPane.getTabs())
						{
							if (tabs.getText().equals(from))
							{
								t = tabs;
								ScrollPane s = (ScrollPane)t.getContent();
								s.setVvalue(1.0);
								VBox v = (VBox)s.getContent();
								Platform.runLater(new Runnable(){
									public void run()
									{
										System.out.println("incoming message created");
										v.getChildren().add(tilepane);
										s.setContent(v);
										
										t.setContent(s);
										s.setVvalue(1.0);
										s.setVvalue(1.0);									

									}
								});
								break;
							}
						}
						
					

						
					}
					}catch(Exception e){System.out.println("In incoming msg service");e.printStackTrace();}
					return null;

				}
			};
		}
	};

	Service<Void> outgoingMessage = new Service<Void>(){
		public Task<Void> createTask()
		{
			return new Task<Void>(){
				protected Void call()
				{
					try{System.out.println("outgoingMessage thread called");
						TilePane tilepane = new TilePane();
						tilepane.setPrefColumns(1);
						tilepane.setPrefRows(1);
						tilepane.setMaxWidth(Double.MAX_VALUE);
						tilepane.setMaxHeight(Double.MAX_VALUE);
						tilepane.setAlignment(Pos.TOP_RIGHT);
						VBox vbox = new VBox();
						
						TilePane.setAlignment(vbox,Pos.TOP_RIGHT);
						Label fromMsg = new Label("You");
						fromMsg.setId("iusername");
						Label message = new Label(msg);
						System.out.println(msg);
						message.setId("incoming");
						message.setWrapText(true);
						message.setMaxWidth(300);
						vbox.getChildren().addAll(fromMsg,message);
						tilepane.getChildren().addAll(vbox);
						Tab t = tabPane.getSelectionModel().getSelectedItem();
						System.out.println(tabPane.getSelectionModel().getSelectedItem().getText());
						ScrollPane s =(ScrollPane) t.getContent();
						s.setVvalue(1.0);
						VBox v =(VBox) s.getContent();
						Platform.runLater(new Runnable(){
							public void run()
							{
								
								System.out.println("outgoing message created");
								v.getChildren().add(tilepane);
								s.setContent(v);
								
								t.setContent(s);	
								s.setVvalue(1.0);
								
								

							}
						});
						
					}catch(Exception e)
					{System.out.println("In outgoing message service ");
						System.out.println(e);}
					return null;
				}
			};
		}
	};

	public void addusers(ActionEvent e)
	{
		System.out.println("Add user method called");
		ScrollPane chatbox = new ScrollPane();
		chatbox.setPannable(true);
		chatbox.setFitToHeight(true);

		chatbox.setHbarPolicy(ScrollBarPolicy.NEVER);
		chatbox.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		VBox chatcontainer = new VBox();
		chatcontainer.setStyle("-fx-background-color:white");
		chatcontainer.setPrefWidth(550);
		chatcontainer.setPrefHeight(500);
		chatcontainer.setMaxHeight(Double.MAX_VALUE);
		chatcontainer.setMaxWidth(500);
		chatcontainer.setPadding(new Insets(10,30,10,10));
		chatbox.setContent(chatcontainer);
		System.out.println("Selected user"+selectuser.getValue().toString());
		String selecteduser = selectuser.getValue().toString();
		Tab tab = new Tab(selecteduser);
		tab.setGraphic(createTabHeader(selecteduser));
		tabPane.setSide(Side.LEFT);
		tabPane.setTabMinWidth(50);
		tabPane.setTabMaxWidth(50);
		tabPane.setTabMinHeight(295);
		tabPane.setTabMaxHeight(295);
		VBox.setVgrow(tabPane,Priority.ALWAYS);
		tab.setContent(chatbox);
		tabPane.getTabs().add(tab);
		FadeTransition ft = new FadeTransition(Duration.millis(100),tabPane);
		ft.setFromValue(0);
		ft.setToValue(1.0);
		ft.play();
		selectuser.getSelectionModel().clearSelection();

	}
	private StackPane createTabHeader(String text){
		Label l = new Label(text);
		l.setId("TabLabel");
		l.setAlignment(Pos.CENTER);
		l.setRotate(90);
		l.setPrefWidth(295);
		l.setPrefHeight(40);
        return new StackPane(new Group(l));
}

public void initialize(URL u,ResourceBundle rb)
{
	ScrollPane chatbox = new ScrollPane();
		chatbox.setPannable(true);
		chatbox.setFitToHeight(true);

		chatbox.setHbarPolicy(ScrollBarPolicy.NEVER);
		chatbox.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		VBox chatcontainer = new VBox();
		chatcontainer.setStyle("-fx-background-color:white");
		chatcontainer.setPrefWidth(550);
		chatcontainer.setPrefHeight(500);
		chatcontainer.setMaxHeight(Double.MAX_VALUE);
		chatcontainer.setMaxWidth(500);
		chatcontainer.setPadding(new Insets(10,30,10,10));
		chatbox.setContent(chatcontainer);
		Tab tab = new Tab("Broadcast");
		tab.setGraphic(createTabHeader("Broadcast"));
		tabPane.setSide(Side.LEFT);
		tabPane.setTabMinWidth(50);
		tabPane.setTabMaxWidth(50);
		tabPane.setTabMinHeight(295);
		tabPane.setTabMaxHeight(295);
		VBox.setVgrow(tabPane,Priority.ALWAYS);
		tab.setContent(chatbox);
		tabPane.getTabs().add(tab);
		FadeTransition ft = new FadeTransition(Duration.millis(100),tabPane);
		ft.setFromValue(0);
		ft.setToValue(1.0);
		ft.play();
	new Thread(new Runnable(){
					public void run()
					{
						try{Thread.sleep(2000);}catch(Exception e){System.out.println("Thread awake");}
								
								while(client.isConnected())
								{	
									synchronized(this)
									{
										if(!client.isConnected())
										{
											System.out.println("Disconnected");
											return;
										}
									
										try
										{

											System.out.println("get Message thread started");
											String command = dis.readUTF();
											System.out.println("Command:"+command);

											if(command.equals("userlistfromserver"))
											{
												userlist = FXCollections.observableArrayList();
												String data[] = dis.readUTF().split("@");
												System.out.println("userlistfromserver received"+data.length);

												for(int i=0;i<data.length;i++)
												{
													System.out.println("Adding:"+data[i]);
													userlist.add(data[i]);
												}
												userlist.remove(name);
												Platform.runLater(new Runnable(){
														public void run()
														{
															selectuser.setItems(userlist);
														}
													});
											}


											else if(command.equals("getMesSagefrom"))
											{
												borp = dis.readUTF();
												if(borp.equals("b"))
												{
													from = dis.readUTF();
													System.out.println("Message from:"+from);
													frommsg = dis.readUTF();
													System.out.println("Message:"+frommsg);
													System.out.println("From:"+from+"\nmessage:"+frommsg);
												}
												if(borp.equals("p"))
												{
													from = dis.readUTF();
													System.out.println("Message from:"+from);
													frommsg = dis.readUTF();
													System.out.println("Message:"+frommsg);
													System.out.println("From:"+from+"\nmessage:"+frommsg);
												}
												System.out.println("****Message coming****");
													
												Platform.runLater(new Runnable(){
												public void run()
												{
													if(!incomingMessage.isRunning())
													{
														incomingMessage.reset();incomingMessage.start();
													}
												}
												});
												
											}
											
										}
										catch(Exception e)
										{
											System.out.println("In getmessage thread");
											return;
										}
										
									}
								}
								return;
					}
				}).start();

	
	}

}
