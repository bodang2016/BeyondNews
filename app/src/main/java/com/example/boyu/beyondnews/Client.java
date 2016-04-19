package com.example.boyu.beyondnews;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by boyu on 2016/4/11.
 */
public class Client {
    private static Socket socket;
    private ServerSocket server;
    private Socket client;
    private static PrintWriter out;
    private static BufferedReader in;
    private static String name = null;
    ArrayList<String> files;
    ArrayList<String> accounts;
    ArrayList<String> paths;

    public static int Init() {
        try {
            if (socket == null) {
                socket = new Socket("10.19.41.85", 1216);
            }
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            return 1;
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return -1;
        }
    }

    public String LogIn(String account, String password) {
        out.println(1);
        out.println(account);
        out.println(password);
        String feedback = null;
        try {
            feedback = in.readLine().toString();
            if (feedback.equals("true"))
                name = in.readLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return feedback;
    }

    public void Registration(String account, String password, String name, String birthday) {
        // TODO Auto-generated method stub
        out.println(0);
        out.println(account);
        out.println(password);
        out.println(name);
        out.println(birthday);
    }

    public String getName() {
        return name;
    }

    public String getAccount(int index) {
        if (accounts != null)
            return accounts.get(index);
        else
            return null;
    }

    public int LogOut(String account) {
        // TODO Auto-generated method stub
        out.println(7);
        out.println(account);
        int success = 0;
        try {
            success = Integer.parseInt(in.readLine());
        } catch (NumberFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return success;
    }

    public ArrayList<String[]> getNews(int type) {
        out.println(1);
        out.println(type);
        String read;
        ArrayList<String[]> list = new ArrayList<String[]>();
        try {
            read = in.readLine();
            while (!read.equals("-1")) {
                String[] str = new String[6];
                str[0] = read;
                str[1] = in.readLine();
                str[2] = in.readLine();
                str[3] = in.readLine();
                str[4] = in.readLine();
                System.out.println(str[3]);
                str[5] = in.readLine();
                list.add(str);
                read = in.readLine();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<String[]> getNews(int type, int index) {
        out.println(2);
        out.println(type);
        out.println(index);
        String read;
        ArrayList<String[]> list = new ArrayList<String[]>();
        try {
            read = in.readLine();
            while (!read.equals("-1")) {
                String[] str = new String[6];
                str[0] = read;
                str[1] = in.readLine();
                str[2] = in.readLine();
                str[3] = in.readLine();
                str[4] = in.readLine();
                System.out.println(str[3]);
                str[5] = in.readLine();
                list.add(str);
                read = in.readLine();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    public String[] getContent(int id) {
        out.println(4);
        out.println(id);
        String[] news = new String[2];
        try {
            news[0] = in.readLine();
            news[1] = in.readLine();
            return news;
        } catch (IOException E) {
            E.printStackTrace();
            return null;
        }
    }

    public int signAndRegister(String email, String password) {
        out.println(5);
        out.println(email);
        out.println(password);
        try {
            return Integer.parseInt(in.readLine());
        } catch (IOException E) {
            E.printStackTrace();
            return -1;
        }
    }

    public void close() {
        try {
            out.println(3);
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}