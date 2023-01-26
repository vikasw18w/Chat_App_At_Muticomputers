package com.ChatApplication;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class MyClient extends JFrame {  // extended only for Jframe(window)
    Socket socket;
    BufferedReader br;
    PrintWriter out;
    // now swing GUI instant variable
    private JLabel heading = new JLabel("Client Area");
    private JTextArea massageArea = new JTextArea();
    private JTextField massageInput = new JTextField();

    private Font font = new Font("Roboto", Font.PLAIN, 20);
    private Font font1 = new Font("Roboto", Font.BOLD, 30);


    // constructor
    public MyClient() {
        try {
            System.out.println("sending request to server");
            socket = new Socket("127.0.0.1", 7778);
            System.out.println("connection done");
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));// <<-- byte stream converted in char then stored in buffer
            // pipe mil gaya. now yenare can read
            out = new PrintWriter(socket.getOutputStream());// pipe mil gaya to send massage // jo write kiya send ho gayanga
            // creating first of all GUI
            creatGUI();

            startReading();
            //startWriting();  // only for consol

            // handle events
            this.setVisible(true);
            handleEvents();


        } catch (Exception e) {
        }
    }

    // GUI Method .code
    private void creatGUI() {
        // this -- for window
        this.setTitle("client Massanger[END]");
        this.setLocation(0, 10); // centre pe window
        this.setSize(600, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// on clicking of cross - close of program.
        // coading for component
        heading.setFont(font1);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
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
        this.add(heading, BorderLayout.NORTH); // this - window, add - to add , kiskio - heading ko, kaha - borderLayout ke north me
        JScrollPane jScrollPane = new JScrollPane(massageArea);
        this.add(jScrollPane, BorderLayout.CENTER);
        //JScrollBar jScrollBar = new JScrollBar();
        this.add(massageInput, BorderLayout.SOUTH);


    }

    private void handleEvents() {
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
                if (e.getKeyCode() == 10) {
                    String contentToSend = massageInput.getText();
                    massageArea.append("Me  : " + contentToSend + "\n");
                    out.println(contentToSend);// massage chala jayanga &
                    out.flush();
                    massageInput.setText(""); // & khali ho jayanga
                    massageInput.requestFocus(); // focus same jagapa aa jayanga


                }

            }
        });

    }

    //     startReading
    public void startReading() { // thread // i am reading coming massages
        // aaya hua masssage ko read karke deta rahanga.
        // thread bcz both work read & write at a  time hone chiye
        Runnable r1 = () -> {
            System.out.println("reader started...");
            try {
                while (true) {
                    String msg = null;


                    msg = br.readLine();// socket se massage aaya & i am reading there


                    if (msg.equalsIgnoreCase("exit")) {
                        System.out.println("server terminated chat");
                        JOptionPane.showMessageDialog(this, "server Terminnated the chat");
                        massageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    //    System.out.println("server  :  " + msg);  // bz ye consol pe likhenga
                    // lrtewrite in window
                    massageArea.append("server  :  " + msg + "\n");
                }
            } catch (IOException e) {
                // throw new RuntimeException(e);
                System.out.println("connection closed");

            }
        };
        new Thread(r1).start();
    }

    //startWriting
    public void startWriting() {// thread
        // user ne yaha likha hua  send ho jayanga to other

        Runnable r2 = () -> {
            System.out.println("Writer started");
            try {
                while (!socket.isClosed()) {

                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));// read honga jo syste.in hua& store in this.
                    String content = br1.readLine();
                    // send
                    out.println(content);
                    out.flush();
                    if (content.equalsIgnoreCase("exit")) {
                        JOptionPane.showMessageDialog(this, "chat terminnated by client");
                        massageInput.setEnabled(false);
                        socket.close();
                        break;
                    }

                }
            } catch (IOException e) {
                //  throw new RuntimeException(e);
                System.out.println("connection closed");

            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is client...  going to start");
        new MyClient();
    }


}
