package com.ChatApplication;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class MyServer extends JFrame{  // extended only for Jframe(window) // all is in constructor.
    ServerSocket server;// class
    Socket socket; // class
    BufferedReader br; // reads but need opened file's obj
    PrintWriter out; // writes but need opened file's obj   // jo apan ne write kiya wo send ho gayanga.
    // now swing GUI instant variable
    private JLabel heading = new JLabel("Server Area");
    private JTextArea massageArea = new JTextArea();
    private JTextField massageInput = new JTextField();
    private Font font = new Font("Roboto",Font.PLAIN,20);
    private Font font1 = new Font("Roboto",Font.BOLD,30);

    // constructor
    public MyServer() {// thread
        try {
            server = new ServerSocket(7778);
            System.out.println("server is ready to accept connection");
            System.out.println("waiting....");
            socket = server.accept(); // waiting waiting & accepting client request(i.e. massage) and stored in socket
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));// <<-- byte stream converted in char then stored in buffer
            // pipe mil gaya. now yenare can read
            out = new PrintWriter(socket.getOutputStream());// pipe mil gaya to send massage // jo write kiya send ho gayanga
            creatGUIServ();

            startReading();
           // startWriting();
            // handle events
            this.setVisible(true);
            handleEvents();

        } catch (Exception e) {

        }
    }

    // GUI method def
    private void creatGUIServ(){
        // this -- for window
        this.setTitle("Server Massanger[END]");
        this.setLocation(700,10); // centre pe window
        this.setSize(600,700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// on clicking of cross - close of program.
        // coading for component
        heading.setFont(font1);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        heading.setIcon(new ImageIcon("C:\\Users\\NEXTGENPC\\Desktop\\chaticon.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);



        massageArea.setFont(font);
        massageArea.setEditable(false);
        massageInput.setFont(font);
        massageInput.setHorizontalAlignment(SwingConstants.CENTER);

        // frame ka layout set karange
        this.setLayout(new BorderLayout());
        // adding the components to frame
        this.add(heading,BorderLayout.NORTH); // this - window, add - to add , kiskio - heading ko, kaha - borderLayout ke north me
        JScrollPane jScrollPane = new JScrollPane(massageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        //JScrollBar jScrollBar = new JScrollBar();
        this.add(massageInput,BorderLayout.SOUTH);



    } private void handleEvents(){
        massageInput.addKeyListener(new KeyListener() { // its an interface t.f. all will astomatically came with overrride
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                //  System.out.println("key realsed   "  +e.getKeyCode());
                if(e.getKeyCode()==10){
                    String contentToSend = massageInput.getText();
                    massageArea.append("Me  : " +contentToSend +"\n");
                    out.println(contentToSend);// massage chala jayanga &
                    out.flush();
                    massageInput.setText(""); // & khali ho jayanga
                    massageInput.requestFocus(); // focus same jagapa aa jayanga
                }
            }
        });
    }
// startReading
    public void startReading() {// thread
        // aaya hua masssage ko read karke deta rahanga.
        // thread bcz both work read & write at a  time hone chiye
        Runnable r1 = () -> {
            System.out.println("reader started...");
            try {
                while (true) {
                    String msg = null;


                    msg = br.readLine();// try catch surrounded tha


                    if (msg.equalsIgnoreCase("exit")) {
                        System.out.println("client terminated chat");
                        JOptionPane.showMessageDialog(this,"client Terminnated the chat");
                        massageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    massageArea.append("server  :  " + msg+"\n");
                }
            } catch (IOException e) {
                // throw new RuntimeException(e);
                System.out.println("connection closed");

            }
        };
        new Thread(r1).start();
    }
// startWriting
    public void startWriting() {
        // user ne yaha likha hua  send ho jayanga to other

        Runnable r2 = () -> {
            System.out.println("Writer started");
            try {
                while (!socket.isClosed()) {
                    // start try
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    // send
                    out.println(content);
                    out.flush();
                    if (content.equalsIgnoreCase("exit")) {
                        JOptionPane.showMessageDialog(this,"chat terminnated by client");
                        massageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    // end try catch
                }
            } catch (IOException e) {
                // throw new RuntimeException(e);
                System.out.println("connection closed");
            }
        };
        new Thread(r2).start();


    }

    public static void main(String[] args) {
        System.out.println("This is server ... going to start");
        new MyServer();

    }
}
