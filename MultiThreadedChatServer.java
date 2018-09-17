/**
// Name: Satyajit Deshmukh
// UTA ID: 1001417727
// Lab 2 - CSE 5306 - 2 Phase Commit Protocol
*/

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.BoxLayout;

public class MultiThreadedChatServer {
	
 static JFrame frame = new JFrame("Chat Room Server Logs");
	static JTextArea messageArea = new JTextArea(20,60);
 static JScrollPane scrollPane = new JScrollPane(messageArea);

	
	// The server socket.
	private static ServerSocket serverSocket = null;
	public static  HashSet<String> UsersDB =new HashSet<String>();  

	// The client socket.
	private static Socket clientSocket = null;
	public static StopWatch stopwatch;
	public static StopWatch stopwatch2;
	public static StopWatch stopwatch3;

    private ServerSocket socketB;
    private Runnable accepterB;

    // members to hold the two groups of clients
    private Set<Socket> groupB;
    private Set<Socket> groupA;
    
    // output thread count
    private final static int OUTPUT_THREADS = 5;
    
    // members to support the output thread pool
    private ExecutorService outputService;
    
	// This chat server can accept up to maxClientsCount clients' connections.
	private static final int maxClientsCount = 4;
	private static final clientThread[] threads = new clientThread[maxClientsCount];
	
	
	final static String lexicon = "12345674890";

	final static java.util.Random rand = new java.util.Random();
	//consider using a Map<String,Boolean> to say whether the identifier is being used or not 
	final static Set<String> identifiers = new HashSet<String>();
	
    public MultiThreadedChatServer(int portA, int portB) 
            throws IOException
        {
            if (portA == portB)
                throw new IllegalArgumentException("Ports can't be equal");
            groupA = Collections.synchronizedSet(new HashSet<Socket>());

            groupB = Collections.synchronizedSet(new HashSet<Socket>());

            socketB = new ServerSocket(portB);

           // accepterB = new ConnAccepter(socketB, groupB, groupA);

            Thread tB = new Thread(accepterB, "Group B");

            outputService = Executors.newFixedThreadPool(OUTPUT_THREADS);

            tB.start();
        }	
	
	
    
    /**
     * Each SocketInputReader object handles reading lines of
     * text from a Socket, using a BufferedReader.  For each
     * line of text, an OutputAction object is created for
     * each member of the outputgroup, and each OutputAction
     * object is given to the outputService to be handled.
     */
  


    
    
	public static String randomIdentifier() {
	    StringBuilder builder = new StringBuilder();
	    while(builder.toString().length() == 0) {
	        int length = rand.nextInt(2)+2;
	        for(int i = 0; i < length; i++) {
	            builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
	        }
	        if(identifiers.contains(builder.toString())) {
	            builder = new StringBuilder();
	        }
	    }
	    return builder.toString();
	}
	
	public static void main(String args[]) throws IOException {

		stopwatch = new StopWatch();
		stopwatch.start();
		stopwatch2 = new StopWatch();
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		messageArea.setEditable(false);
		frame.getContentPane().add(scrollPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		

		
		//get the port number from the dat file
		BufferedReader inputS = null;
		// The default port number.
		inputS = new BufferedReader(new FileReader("serverinfo.dat"));
		String l = null;
		String IP;
		l = inputS.readLine();
		//read textfile and translate to integer number.
		int portNumber = Integer.parseInt(l.substring(13, l.length()));

		if (args.length < 1) {
			System.out.println(
			"Usage: java MultiThreadChatServer <portNumber>\n" + "Now using port number=" + portNumber);
		} else {
			portNumber = Integer.valueOf(args[0]).intValue();
		}
		MultiThreadedChatServer server;
		//Open a server socket on the portNumber.
		try {
			serverSocket = new ServerSocket(portNumber);
            server = new MultiThreadedChatServer(2222, 3333);

		} catch (IOException e) {
			System.out.println(e);
		}
		//a client socket for each connection and pass it to a new a client thread.
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				int i = 0;
				for (i = 0; i < maxClientsCount; i++) {
					if (threads[i] == null) {
						(threads[i] = new clientThread(clientSocket, threads)).start();
						break;
					}
				}
				if (i == maxClientsCount) {
					PrintStream os = new PrintStream(clientSocket.getOutputStream());
					os.println("Server too busy. Try later.");
					os.close();
					clientSocket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}



	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	
}



/*
* The chat client thread. This client thread opens the input and the output
* streams for a particular client, ask the client's name, informs all the
* clients connected to the server about the fact that a new client has joined
* the chat room, and as long as it receive data, echos that data back to all
* other clients. When a client leaves the chat room this thread informs also
* all the clients about that and terminates.
*/
class clientThread extends Thread {
	static int minutes;
	static int seconds;
	private DataInputStream is = null;
	private  PrintStream os = null;
	private Socket clientSocket = null;
	private final clientThread[] threads;
	private int maxClientsCount;
	private ArrayList<String> clientList;
	private String process_state="READY";
	private String name;
	private int counter,counter2,counter3;
	
	
	   public  void setStatus(String newState){
		   if(newState.equals("VOTE_REQUEST"))
		   {
			MultiThreadedChatServer.stopwatch2.start();   
		   process_state = "VOTE_REQUEST_RECEIVED";
		   }
		   else
			   process_state = newState;

		   }

	   public String getStatus()
	   {
		      return (process_state);
	   }
	   
	public clientThread(Socket clientSocket, clientThread[] threads) {
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClientsCount = threads.length;

		clientList = new ArrayList<String>();
	}
	//check clientList.
	public boolean isBlocked(String name) {
		if (clientList.size() < 0)
		return false;
		else {
			return clientList.contains(name);
		}
	}
	
public boolean checkVOTES()
{ int flag=0;
	int i=0;
	for ( i = 0; i < maxClientsCount; i++) {
		if (threads[i] != null  && (threads[i].getStatus().contains("WAITING AFTER VOTE_COMMIT") || threads[i].getStatus().contains("VOTE_REQUEST_SENT_WATIING"))) 
		{
			threads[i].os.println("idher aaya: " + i);
			flag=1;
		}
			else
				flag=0;
	}
	if(flag>0)
	return true;
	else
		return false;
}

public int checktimer()
{
	int time;
	time = (int) MultiThreadedChatServer.stopwatch2.getElapsedTimeSeconds() - counter;
	os.println("time: " + time);
	return time;
}



	interface MyFun {
	    static void fun2(String X) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public void run() {


		int maxClientsCount = this.maxClientsCount;
		clientThread[] threads = this.threads;
		//stopwatch2 = new StopWatch();
		int timeSpan;
		
		MyFun fun2 = new MyFun() {
		      public void fun2(String HTTPPacket) {
		          		os.println(HTTPPacket);
		          		System.out.println("wow");
		      }
		};
		  

		try {
			is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
			os.println("Enter your name.");

			//get the name. It will be used in chat room.
			
			do 
			{   			name = is.readLine().trim();
				 if(MultiThreadedChatServer.UsersDB.contains(name))
				 {
				os.println ("User: "+ name + " is already registered! \nAssigning available random name..." );
				name = name + MultiThreadedChatServer.randomIdentifier();
				 }
			} while ( MultiThreadedChatServer.UsersDB.add(name) == false) ;
			MultiThreadedChatServer.UsersDB.add(name); 
			os.println("Welcome: " + name + " has joined the chat room.");
			
	        int delay = 60000; // delay for 5 sec.
	        //int period = 60000; // repeat every sec.
	        Timer timer = new Timer();


	      
	
			MultiThreadedChatServer.messageArea.append("\n" + name + " has connected to the server."  + "\n" );
			
			if(name.contains("Co-ordinator")==false)
			{
			os.println("Current Status: " + getStatus());
			os.println("\nWaiting for VOTE_REQUEST Message(60 Seconds)");
	        timer.schedule(new TimerTask() {
		          public void run() {
		        	  if(getStatus().contains("READY"))
		        	  {
		            os.println("TIMEOUT! Writing ABORT TO LOG."); 
		        	  }
		          }
		        }, delay);
			}

			//broadcast that enter the chatting room!
			for (int i = 0; i < maxClientsCount; i++) {
				if (threads[i] != null && threads[i] != this) {
					threads[i].os.println("* A new user " + name + " entered the chat room!*");
					
				}
			}
			
			while (true) {
				String line = is.readLine();
				if (line.length() <= 0)
				continue;
				//enter quit, if you want to get out the chat room.
				else if (line.startsWith("/quit")) {
					break;
				}
				
			
				// Co-ordinator Code
				//write log
				// multicast VOTE_request to all participants
				// wait until all votes collected
				else if (line.equalsIgnoreCase("VOTE_REQUEST")  ) 
				{
					 counter =  (int) MultiThreadedChatServer.stopwatch2.getElapsedTimeSeconds();
					for (int i = 0; i < maxClientsCount; i++) {
						if (threads[i] != null && (!threads[i].name.equals("Co-ordinator")) ) {
							threads[i].setStatus("VOTE_REQUEST RECEIVED");
							threads[i].os.println("Current Status: " + threads[i].getStatus());
							
						}
						if (threads[i] != null && threads[i].name.contains("Co-ordinator"))
						{
							threads[i].setStatus("VOTE_REQUEST_SENT_WAITING");
							 timer.schedule(new TimerTask() {
						          public void run() {
						        	  if(getStatus().contains("VOTE_REQUEST_SENT_WAITING"))
						        	  {
						            os.println("TIMEOUT occurred waiting for Votes from participants."); 
									for (int i = 0; i < maxClientsCount; i++) {
										if(threads[i] != null && (!threads[i].name.equals("Co-ordinator")))
										threads[i].os.println("GLOBAL_ABORT");
									}
						            
						        	  }
						          }
						        }, 120000);
						}
					}
				}
				
				else if(line.equalsIgnoreCase("checkTimer"))
				{
					checktimer();
				}
				// Participant Code
				// write INIT TO LOG
				//WAIT FOR VOTE_REQUEST FROM co-ordinator 
				// if timeout -> write VOTE_ABORT TO LOG
				else if (line.equalsIgnoreCase("VOTE_COMMIT")  ) 
				{
					
						String whis = "co-ordinator"; //whis is client's name who get the message.
						 timeSpan =  (int) MultiThreadedChatServer.stopwatch2.getElapsedTimeSeconds();
						 minutes = timeSpan/60;
						 seconds = timeSpan%60;
						 if(minutes==0 && seconds<60)
						 {
						for (int i = 0; i < maxClientsCount; i++) {
							if (threads[i] != null ) {
								threads[i].os.println(name + "'s MESSAGE: " + line);
								if(threads[i].name.equals(name)) 
									threads[i].setStatus("WAITING AFTER VOTE_COMMIT");
							}
						}
						 }
						 else
							 os.println("VOTE_ABORT");
							 
					continue;
				}
				
				else if (line.equalsIgnoreCase("/get_status" )  ) 
				{
					
					for (int i = 0; i < maxClientsCount; i++) {
						if (threads[i] != null && threads[i].name.equals(name) ) 
						{
							threads[i].os.println(name + "'s State: " +	threads[i].getStatus() );
						}
					}
				}
				

				else if (line.equalsIgnoreCase("checkVotes")  ) 
				{
						if(checkVOTES()==true)
							os.println("GLOBAL_COMMIT!");
						else if (checkVOTES()==false)
							os.println("GLOBAL_ABORT!");
				}
				
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null) {
						if (clientList.contains((String) threads[i].name))
						continue;
						//broadcast the chat string to all users.
						if (!threads[i].isBlocked(name))
						{
							 timeSpan =  (int) MultiThreadedChatServer.stopwatch.getElapsedTimeSeconds();
							 minutes = timeSpan/60;
							 seconds = timeSpan%60;
							 
							threads[i].os.println(name+ "(" + String.format("%02d", minutes) +":"+String.format("%02d", seconds) +") - " + line);
							
						}
					}
				}
				
				MultiThreadedChatServer.messageArea.append((name+ "(" + String.format("%02d", minutes) +":"+String.format("%02d", seconds) +") - " + line + "\n"));

				MultiThreadedChatServer.scrollPane.getVerticalScrollBar().setValue(MultiThreadedChatServer.scrollPane.getVerticalScrollBar().getMaximum());
				
			   	HTTPPacket requestPacket = new HTTPPacket();
				requestPacket.setRequest_type("GET");
				requestPacket.setHttp_version("HTTP/1.1");
				requestPacket.setHost("localhost");
				requestPacket.setUser_agent("HTTPTool/1.0");
				requestPacket.setAccept_type("text/plain");
				requestPacket.setAccept_language("en-us");
				requestPacket.setResource("http://localhost/messages/" + String.valueOf(1));
				requestPacket.setDate_time(new Date());		
				String chu = requestPacket.toString(); 

				MultiThreadedChatServer.messageArea.append( chu + "\n");

			}
			//broadcast when user exit the chatting room.
			for (int i = 0; i < maxClientsCount; i++) {
				if (threads[i] != null && threads[i] != this) {
					threads[i].os.println("*User" + name + " has left the chat room. *");
				}
			}
			MultiThreadedChatServer.messageArea.append((name+ "(" + String.format("%02d", minutes) +":"+String.format("%02d", seconds) +")  has left the chat room!" + "\n"));
			os.println("* Bye " + name + " *");
			
			/*
			* Clean up. Set the current thread variable to null so that a new
			* client could be accepted by the server.
			*/
			for (int i = 0; i < maxClientsCount; i++) {
				if (threads[i] == this) {
					threads[i] = null;
				}
			}
			/*
			* Close the output stream, close the input stream, close the
			* socket.
			*/
			is.close();
			os.close();
			clientSocket.close();
		} catch (IOException e) {
		}
	}
	

}
