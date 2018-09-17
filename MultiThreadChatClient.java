/**
// Name: Satyajit Deshmukh
// UTA ID: 1001417727
// Lab 2 - CSE 5306 - 2 Phase Commit Protocol
*/
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;

public class MultiThreadChatClient implements Runnable, ActionListener {

	// The client socket
	private static Socket clientSocket = null;
	// The output stream
	private static PrintStream os = null;
	// The input stream
	private static DataInputStream is = null;
	private static BufferedReader inputLine = null;
	private static boolean closed = false;
	// GUI Component Declaration
	private static JFrame frame = new JFrame();
	private static JPanel panel = new JPanel();
	private static JTextField textField = new JTextField();
	private static JTextArea messageArea = new JTextArea();
	private static JScrollPane scroll;
	private final JButton btnNewButton = new JButton("Disconnect");
	private final JButton btnNewButton_1 = new JButton("ABORT");
	private final JButton btnCommit = new JButton("VOTE_COMMIT");

	public MultiThreadChatClient() {
		// Layout GUI
		// set the basic frame
		frame.setBounds(100, 100, 403, 504);
		frame.setTitle("Chatter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);
		frame.setVisible(true);
		// make form
		messageArea.setEditable(false);
		messageArea.setLineWrap(true);
		 scroll = new JScrollPane(messageArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// scroll.setPreferredSize(new Dimension(400,400));
		panel.setLayout(new BorderLayout());
		panel.add(BorderLayout.NORTH, textField);
		// panel.add(BorderLayout.CENTER, scroll);
		panel.add(messageArea);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				os.println("/quit");
				textField.setText("");
			}
		});

		panel.add(btnNewButton, BorderLayout.SOUTH);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				os.println("ABORT");
				textField.setText("");
			}
		});

		panel.add(btnNewButton_1, BorderLayout.EAST);
		btnCommit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				os.println("VOTE_COMMIT");
				textField.setText("");

			}
		});

		panel.add(btnCommit, BorderLayout.WEST);
		// add Listener
		textField.addActionListener(this);
		WindowListener listen = new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		};
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// get and print textmessage on textfield
		os.println(textField.getText());
		textField.setText("");
	}

	public static void main(String[] args) {
		MultiThreadChatClient client = new MultiThreadChatClient();
		// The default port.
		int portNumber = 2222;
		// The default host.
		String host = "localhost";
		if (args.length < 2) {
			System.out.println("Usage: java MultiThreadChatClient <host> <portNumber>\n" + "Now using host=" + host
					+ ", portNumber=" + portNumber);
		} else {
			host = args[0];
			portNumber = Integer.valueOf(args[1]).intValue();
		}
		/*
		 * Open a socket on a given host and port. Open input and output streams.
		 */

		try {
			clientSocket = new Socket(host, portNumber);
			inputLine = new BufferedReader(new InputStreamReader(System.in));
			os = new PrintStream(clientSocket.getOutputStream());
			is = new DataInputStream(clientSocket.getInputStream());
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + host);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to the host " + host);
		}

		/*
		 * If everything has been initialized then we want to write some data to the
		 * socket we have opened a connection to on the port portNumber.
		 */
		if (clientSocket != null && os != null && is != null) {
			try {

				/* Create a thread to read from the server. */
				new Thread(new MultiThreadChatClient()).start();
				while (!closed) {
					os.println(inputLine.readLine().trim());
				}
				/*
				 * Close the output stream, close the input stream, close the socket.
				 */
				os.close();
				is.close();
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		/*
		 * Keep on reading from the socket till we receive "Bye" from the server. Once
		 * we received that then we want to break.
		 */
		String responseLine;
		// Process messages from server, according to the protocol.
		try {
			HTTPPacket requestPacket = new HTTPPacket();
			requestPacket.setRequest_type("GET");
			requestPacket.setHttp_version("HTTP/1.1");
			requestPacket.setHost("localhost");
			requestPacket.setUser_agent("HTTPTool/1.0");
			requestPacket.setAccept_type("text/plain");
			requestPacket.setAccept_language("en-us");
			os.println("client1");
			while ((responseLine = is.readLine()) != null) {
				Thread.sleep(500);
				messageArea.append(responseLine + "\n");
				requestPacket.setResource("http://localhost/messages/" + String.valueOf(1));
				requestPacket.setDate_time(new Date());
				String chu = requestPacket.toString();
				// messageArea.append(String.valueOf(Thread.activeCount()));
				clientThread.MyFun.fun2(chu);
				// messageArea.append(chu);
				if (responseLine.indexOf("* Bye") != -1)
					break;
			}
			closed = true;
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getServerAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
