# MultiThreadedChatServer

A Chat Room Messaging system
Application developed using NetBeans IDE.
Instructions to Compile/Run:
1.Unzip the contents of zipfiles- ChatBoxServer.zip & ChatBoxClient.zip
2. Open NetBeans- Go to – File – Open Project – Select the directory of unzipped files and -> ChatBoxServer
3. Go to – File – Open Project – Select ChatBoxClient
4. Build the server and run to load up the GUI. Press Start to start the server.
5. Run the Client project, enter a username (address & port have been set to defaults for convenience purposes) and hit the connect button on GUI to connect to server.
6. Repeat step 5 for multiple clients to connect to the server.

Features:
Server GUI:
1.	Simple logs maintained on GUI.
2.	Option to see Online users in real time.
3.	Messages/logs generated during client connection and disconnection.
Client GUI: 
1.	Easy to use, set username, press connect to server and chat away!

Limitations:
1.	Supports upto 10 client threads.
2.	No database maintained if server shuts down and starts again. (Bonus feature not added)


References: 
1.	Jfame and GUI handling - https://netbeans.org/kb/docs/java/quickstart-gui.html
2.	Java Socket chat application - https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
3.	Socket programming for multithreaded client processes - http://cs.lmu.edu/~ray/notes/javanetexamples
4.	chat application using java sockets (with GUI) - https://www.youtube.com/watch?v=kqBmsLvWU14

