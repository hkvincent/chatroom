/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myserver;

import mychat.*;
import com.sun.java.swing.plaf.windows.resources.windows;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Administrator
 */
public class Server implements Runnable {

    ServerSocket ss;
    ServerFrame sf;
    List<clientMessage> cmList = new ArrayList<>();

    //妲嬮�鏂规硶锛屽瓨鍏ヤ簡涓��绔彛鏁稿瓧

    public Server(int port) {
        try {
             System.out.println("Server:" + Thread.currentThread().getName());
            System.out.println("reach");
            sf = new ServerFrame();
//            new Thread(sf).start();
            ss = new ServerSocket(port);

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //鍟熷嫊鐒＄窔杓看锛岀瓑寰呯敤鎴剁閫插叆锛屼笉浣跨敤鐨勮┍涓��鐢ㄦ埗绔�鍏ュ氨鏈冭蛋瀹屻�

    public void start() {
        try {
            while (true) {
                Socket s = s = ss.accept();//闃诲
                clientMessage cm = new clientMessage(s);
                
                new Thread(cm).start();
                cmList.add(cm);
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//绾跨▼鏂规硶锛屽浠诲嫏鐨勬檪鍊欒鐢ㄥ绶氱▼銆�

    @Override
    public void run() {
        System.out.println("Server:" + Thread.currentThread().getName());
        start();

    }
//鍓甸�涓��鍏ч儴class,閫欏�class鐢ㄤ締铏曠悊鎺ユ敹鍜岀櫦閫佷俊鎭�

    private class clientMessage implements Runnable {

        Socket s;
        DataInputStream dis = null;
        DataOutputStream dos = null;
        boolean connect;

        public clientMessage(Socket s) {
            this.s = s;
            try {
                //浣跨敤鏁告摎娴�
                dis = new DataInputStream(s.getInputStream());
                dos = new DataOutputStream(s.getOutputStream());
                connect = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //鍌抽�淇℃伅绲﹀悇澶х敤鎴剁鍜屾湇鍕欏櫒鑷韩

        public void send(String str) {
            try {
                dos.writeUTF(str);

            } catch (SocketException e) {
                System.out.println("log out");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            System.out.println("clientMessage;" + Thread.currentThread().getName());
            clientMessage cm = null;
            try {
                while (connect) {
                    //鐒¤珫杓看鍦拌畝鍙栨暩鎿氾紝閫欏�鏂规硶涔熸槸闃诲锛岀瓑鏁告摎閫插叆鎵嶆渻璧颁笅鍘�
                    String str = dis.readUTF();
                    // System.out.println(str);
                    for (int i = 0; i < cmList.size(); i++) {//閬嶆鎵�湁鐢ㄦ埗绔�鐢变己鏈嶅櫒鐧奸�鐢ㄦ埗鐨勪俊鎭郸鎵�湁鐢ㄦ埗
                        cm = cmList.get(i);
                        cm.send(str);
                    }
                    cm.sendServer(str);
                }
            } catch (EOFException e) {
                System.out.println("someone out");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (dis != null) {
                    if (s != null) {
                        try {
                            dis.close();
                            s.close();
                            dos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private void sendServer(String str) {
            sf.setWrod(str);
        }
    }
//閫欏�椤炴槸鐢ㄤ締椤ず鏈嶅嫏鍣ㄧ殑淇℃伅

    private class ServerFrame extends JFrame implements Runnable {

        TextArea ta = new TextArea();

        public ServerFrame() {
            init();
            System.out.println("ServerFrame:" + Thread.currentThread().getName());
            this.setBounds(300, 300, 300, 300);
            ta.setEditable(false);
            this.add(ta, BorderLayout.NORTH);
            this.setVisible(true);
            this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {

                    System.exit(0);
                }
            });
        }

        @Override
        public void run() {

        }

        private void setWrod(String str) {
            ta.append(str + "\n");
            System.out.println("ServerFrame:" + Thread.currentThread().getName());
        }

        private void init() {
            this.setTitle("ServerSetting");
            UIGov.setFrameCenter(this);
            this.setResizable(false);
        }

    }
}
