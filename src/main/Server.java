package main;
import java.awt.Color;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;

public class Server implements Runnable {
    private ServerSocket server;
    private Users[] user = new Users[10];
    private int randbomb, bx[], by[], randET, ETx[], ETy[], randenergy[] = { 50, 55, 60, 65, 70, 75, 80, 85, 90, 95 },
            energy[];
    private int randavatar[] = { 0, 1, 2, 3, 4}, avatar, randindex;

    public Server() {
        randbomb = new Random().nextInt((320 - 240) + 1) + 240;
        randET = new Random().nextInt((24 - 16) + 1) + 16;
        bx = new int[randbomb];
        by = new int[randbomb];
        ETx = new int[randET];
        ETy = new int[randET];
        energy = new int[randET];
        for (int i = 0; i < randbomb; i++) {
            bx[i] = (int) ((Math.random() * 79)) * 32;
            by[i] = (int) ((Math.random() * 99)) * 32;
        }
        for (int i = 0; i < randET; i++) {
            ETx[i] = (int) ((Math.random() * 79)) * 32;
            ETy[i] = (int) ((Math.random() * 99)) * 32;
        }
        for (int i = 0; i < randET; i++) {
            energy[i] = randenergy[new Random().nextInt(randenergy.length)];
        }
    }

    private int[] remove(int[] a, int index) {
        if (a == null || index < 0 || index >= a.length) {
            return a;
        }

        int[] result = new int[a.length - 1];
        for (int i = 0; i < index; i++) {
            result[i] = a[i];
        }

        for (int i = index; i < a.length - 1; i++) {
            result[i] = a[i + 1];
        }
        return result;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(1234);
            System.out.println("Server Activated");
            while (true) {
                Socket client = server.accept();
                for (int i = 0; i < 10; i++) {
                    if (user[i] == null) {
                        for (int j = 0; j < randavatar.length; j++) {
                            randindex = new Random().nextInt(randavatar.length);
                            avatar = randavatar[randindex];
                            randavatar = remove(randavatar, randindex);
                        }
                        Bomb b = new Bomb(bx, by);
                        ET et = new ET(ETx, ETy, energy);
                        user[i] = new Users(client, i, b, et, avatar);
                        Thread t = new Thread(user[i]);
                        t.start();
                        System.out.println("Connection from : " + client.getInetAddress() + " PID " + i);
                        break;
                    }
                }

            }

        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        new Server().run();
    }

    class Users implements Runnable {
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket client;
        BufferedImage pIMG;
        Bomb bomb, bombin;
        ET et, etin;
        String directionin, namein;
        Color colorin;
        ArrayList<Bullet> bullet, bulletin;
        int pid, pidin, xin, yin, healthin, randAvatar, avatarin;
        boolean fire, running;

        public Users(Socket client, int pid, Bomb bomb, ET et, int avatar) {
            this.client = client;
            this.pid = pid;
            this.bomb = bomb;
            this.et = et;
            this.randAvatar = avatar;
            running = true;
            bullet = new ArrayList<Bullet>();
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(client.getOutputStream());
                in = new ObjectInputStream(client.getInputStream());
                out.writeObject(pid);
                out.writeObject(randAvatar);
                out.writeObject(bomb);
                out.writeObject(et);
                out.writeObject(bullet);
            } catch (IOException e2) {
                System.out.println("Failed to send PID");
            }
            while (running) {
                try {
                    pidin = (int) in.readObject();
                    xin = (int) in.readObject();
                    yin = (int) in.readObject();
                    bombin = (Bomb) in.readObject();
                    fire = (boolean) in.readObject();
                    directionin = (String) in.readObject();
                    healthin = (int) in.readObject();
                    etin = (ET) in.readObject();
                    avatarin = (int) in.readObject();
                    namein = (String) in.readObject();
                    colorin = (Color) in.readObject();
                    bulletin = (ArrayList<Bullet>) in.readObject();
                    for (int i = 0; i < 10; i++) {
                        if (user[i] != null) {
                            user[i].out.writeObject(pidin);
                            user[i].out.writeObject(xin);
                            user[i].out.writeObject(yin);
                            user[i].out.writeObject(bombin);
                            user[i].out.writeObject(fire);
                            user[i].out.writeObject(directionin);
                            user[i].out.writeObject(healthin);
                            user[i].out.writeObject(etin);
                            user[i].out.writeObject(avatarin);
                            user[i].out.writeObject(namein);
                            user[i].out.writeObject(colorin);
                            user[i].out.writeObject(bulletin);
                        }
                    }
                } catch (IOException e) {
                    user[pid] = null;
                } catch (ClassNotFoundException e) {
                    System.out.println("Recieve Data Failed");
                    user[pid] = null;
                }
            }
        }

        public void shutdown() { //ปิด
            try {
                running = false;
                out.close();
                in.close();
                if (this.client.isClosed()) {
                    this.client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
