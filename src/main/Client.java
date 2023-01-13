package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.awt.GridLayout;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

public class Client extends JPanel implements Runnable, KeyListener {
    Socket client;
    ObjectOutputStream out;
    ObjectInputStream in;
    BufferedImage BombIMG, ETIMG;
    BufferedImage pIMG[] = new BufferedImage[10];
    BufferedImage upwalk[] = new BufferedImage[5];
    BufferedImage downwalk[] = new BufferedImage[5];
    BufferedImage leftwalk[] = new BufferedImage[5];
    BufferedImage rightwalk[] = new BufferedImage[5];
    Color color[] = new Color[10];
    Thread t1, t2, t3, t4, t5, t6, t7;
    Tile tile;
    Bomb bomb;
    ET et;
    ArrayList<Bullet> bullet;
    public int tileSize = 32;
    public int ScreenCol = 25;
    public int ScreenRow = 20;
    public int screenWidth = tileSize * ScreenCol;
    public int screenHeight = tileSize * ScreenRow;
    int Xscreen = (25 * 32) / 2 - (32) / 2;
    int Yscreen = (20 * 32) / 2 - (32) / 2;
    int pid, a = 0;
    int c_up = 0;
    int c_left = 0; 
    int c_down = 0;
    int c_right = 0;
    int[] x = new int[10];
    int[] y = new int[10];
    int[] health = new int[10];
    int dx = 0, dy = 0, move = 0, Myavatar[] = new int[10];
    boolean running;
    boolean up = false, left = false, down = false, right = false, Pfire = false, quit = false, Phit = false;;
    boolean count = false;
    boolean showdead = false;
    boolean step = false;
    boolean fire[] = new boolean[10];
    String direction[] = new String[10];
    String Pdirection = "up";
    String name[] = new String[10];
    String whodead;

    public Client() {
        addKeyListener(this);
        setBackground(Color.decode("#84a43c"));
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setDoubleBuffered(true);
        setFocusable(true);
        tile = new Tile(this);
        try {
            BombIMG = ImageIO.read(getClass().getResourceAsStream("/bomb/land_mine.png"));
            ETIMG = ImageIO.read(getClass().getResourceAsStream("/ET/Energy.png"));
            upwalk[0] = ImageIO.read(getClass().getResourceAsStream("/player/P1/up.png"));
            downwalk[0] = ImageIO.read(getClass().getResourceAsStream("/player/P1/down.png"));
            leftwalk[0] = ImageIO.read(getClass().getResourceAsStream("/player/P1/left.png"));
            rightwalk[0] = ImageIO.read(getClass().getResourceAsStream("/player/P1/right.png"));

            upwalk[1] = ImageIO.read(getClass().getResourceAsStream("/player/P2/up.png"));
            downwalk[1] = ImageIO.read(getClass().getResourceAsStream("/player/P2/down.png"));
            leftwalk[1] = ImageIO.read(getClass().getResourceAsStream("/player/P2/left.png"));
            rightwalk[1] = ImageIO.read(getClass().getResourceAsStream("/player/P2/right.png"));

            upwalk[2] = ImageIO.read(getClass().getResourceAsStream("/player/P3/up.png"));
            downwalk[2] = ImageIO.read(getClass().getResourceAsStream("/player/P3/down.png"));
            leftwalk[2] = ImageIO.read(getClass().getResourceAsStream("/player/P3/left.png"));
            rightwalk[2] = ImageIO.read(getClass().getResourceAsStream("/player/P3/right.png"));

            upwalk[3] = ImageIO.read(getClass().getResourceAsStream("/player/P4/up.png"));
            downwalk[3] = ImageIO.read(getClass().getResourceAsStream("/player/P4/down.png"));
            leftwalk[3] = ImageIO.read(getClass().getResourceAsStream("/player/P4/left.png"));
            rightwalk[3] = ImageIO.read(getClass().getResourceAsStream("/player/P4/right.png"));

            upwalk[4] = ImageIO.read(getClass().getResourceAsStream("/player/P5/up.png"));
            downwalk[4] = ImageIO.read(getClass().getResourceAsStream("/player/P5/down.png"));
            leftwalk[4] = ImageIO.read(getClass().getResourceAsStream("/player/P5/left.png"));
            rightwalk[4] = ImageIO.read(getClass().getResourceAsStream("/player/P5/right.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
    }

    public void init() {
        try {
            client = new Socket("localhost", 1234);
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            pid = (int) in.readObject();
            Myavatar[pid] = (int) in.readObject();
            bomb = (Bomb) in.readObject();
            et = (ET) in.readObject();
            bullet = (ArrayList<Bullet>) in.readObject();
            name[pid] = JOptionPane.showInputDialog("Please Enter Username");
            health[pid] = 100;
            pIMG[pid] = downwalk[Myavatar[pid]];
            direction[pid] = Pdirection;
            Random rand = new Random();
            color[pid] = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
            running = true;
            Input input = new Input(in, this);
            t1 = new Thread(input);
            t1.start();
            t2 = new Thread(this);
            t2.start();
            t3 = new Thread(this, "Fire");
            t3.start();
            t4 = new Thread(this, "BulletTick");
            t4.start();
            t5 = new Thread(this, "Event");
            t5.start();
            t6 = new Thread(this, "RandomNewETpos");
            t6.start();
            t7 = new Thread(this, "Whodead");
            t7.start();
        } catch (IOException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Cannot Get PID");
        }
    }

    public void shutdown() {
        try {
            running = false;
            out.close();
            in.close();
            if (!client.isClosed()) {
                client.close();
            }
            System.out.println("Player " + pid + " Disconnect");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLocation(int pid, int x, int y, Bomb b, boolean fire, String direction, int health, ET et,
            int avatar, String name, Color color, ArrayList<Bullet> bull) {
        this.x[pid] = x;
        this.y[pid] = y;
        this.bomb = b;
        this.fire[pid] = fire;
        this.direction[pid] = direction;
        this.health[pid] = health;
        this.et = et;
        this.Myavatar[pid] = avatar;
        if(this.direction[pid].equals("up"))
        {
            this.pIMG[pid] = upwalk[Myavatar[pid]];
        } 
        else if (this.direction[pid].equals("down"))
        {
            this.pIMG[pid] = downwalk[Myavatar[pid]];
        } else if (this.direction[pid].equals("left"))
        {
            this.pIMG[pid] = leftwalk[Myavatar[pid]];
        } else if (this.direction[pid].equals("right"))
        {
            this.pIMG[pid] = rightwalk[Myavatar[pid]];
        }
        this.name[pid] = name;
        this.color[pid] = color;
        this.bullet = bull;
    }

    public void paint(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        tile.draw(g2);
        bomb.draw(g2, x[pid], y[pid], Xscreen, Yscreen, BombIMG);
        et.draw(g2, x[pid], y[pid], Xscreen, Yscreen, ETIMG);
        String pos = "X : " + x[pid] / tileSize + " Y : " + y[pid] / tileSize;
        g2.setColor(Color.orange);
        g2.setFont(new Font("Angsana New", Font.BOLD, 58));
        g2.drawString(pos, 0, 30);
        g2.drawString("HP : " + health[pid], 650, 30);
        if(showdead == true)
        {
            g2.setFont(new Font("Angsana New", Font.BOLD, 50));
            g2.drawString(whodead, 280, 150);
        }
        for (int i = 0; i < bullet.size(); i++)
        {
            bullet.get(i).draw(g2, x[pid],y[pid], Xscreen, Yscreen);
        }
        Font f = new Font("Angsana New", Font.BOLD, 35);
        FontMetrics metrics = g2.getFontMetrics(f);
        g2.setFont(f);
        for (int i = 0; i < 10; i++)
        {
            if (name[i] != null && direction[i] != null)
            {
                if (i == pid)
                {
                    if (health[i] > 0)
                    {
                        int xf = Xscreen + (tileSize - metrics.stringWidth(name[pid])) / 2;
                        int yf = Yscreen + ((tileSize - metrics.getHeight()) / 2) + metrics.getAscent();
                        g2.setColor(color[pid]);
                        g2.drawString(name[pid], xf, yf - 25);
                        g2.drawImage(pIMG[pid], Xscreen, Yscreen, 32, 32, null);
                    }
                }
                else
                {
                    int screenX = x[i] - x[pid] + ((25 * 32) / 2 - 32 / 2);
                    int screenY = y[i] - y[pid] + ((20 * 32) / 2 - 32 / 2);
                    int xf = screenX + (tileSize - metrics.stringWidth(name[i])) / 2;
                    int yf = screenY + ((tileSize - metrics.getHeight()) / 2) + metrics.getAscent();
                    if (health[i] > 0)
                    {
                        g2.setColor(color[i]);
                        g.drawString(name[i], xf, yf - 25);
                        g2.drawImage(pIMG[i], screenX, screenY, 32, 32, null);
                    }
                }
            }
        }
    }

    public void update() {
        if (right) {
            c_up = c_down  = c_left = 0;
            if(c_right == 0)
            {
                count = false;
            }
            if(count == false)
            {
                dx += 0;
                count = true;
                c_right++;
            }
            else if(count == true)
            {
                dx += 32;
            }
        }
        if (left) {
            c_up = c_down  = c_right = 0;
            if(c_left == 0)
            {
                count = false;
            }
            if(count == false)
            {
                dx -= 0;
                count = true;
                c_left++;
            }
            else if(count == true)
            {
                dx -= 32;
            }
        }
        if (up) {
            c_left = c_down  = c_right = 0;
            if(c_up == 0)
            {
                count = false;
            }
            if(count == false)
            {
                dy -= 0;
                count = true;
                c_up++;
            }
            else if(count == true)
            {
                dy -= 32;
            }
        }
        if (down) {
            c_left = c_up  = c_right = 0;
            if(c_down == 0)
            {
                count = false;
            }
            if(count == false)
            {
                dy += 0;
                count = true;
                c_down++;
            }
            else if(count == true)
            {
                dy += 32;
            }
        }
        if (dy < 0) {
            dy = 0;
        } else if (dx < 0) {
            dx = 0;
        } else if (dx > 79 * tileSize) {
            dx = 79 * tileSize;
        } else if (dy > 99 * tileSize) {
            dy = 99 * tileSize;
        }
        if (up || right || left || down || Pfire || quit || step || Phit) {
            System.out.println(dx + " " + dy);
            try {
                out.writeObject(pid);
                out.writeObject(dx);
                out.writeObject(dy);
                out.writeObject(bomb);
                out.writeObject(Pfire);
                out.writeObject(Pdirection);
                out.writeObject(health[pid]);
                out.writeObject(et);
                out.writeObject(Myavatar[pid]);
                out.writeObject(name[pid]);
                out.writeObject(color[pid]);
                out.writeObject(bullet);
                Pfire = up = down = left = right = step = Phit = false;
                if(quit)
                {
                    shutdown();
                    quit = false;
                }
            } catch (Exception e) {
                System.out.println("Error Sending Location ");
                e.printStackTrace();
            }
        }

    }

    public void event() {
        int bn = bomb.x.length;
        int ETn = et.x.length;
        int removebomb = 0;
        boolean checkhitbomb = false, hit = false;
        for (int i = 0; i < bn; i++) // เช็คว่าตัวละครเหยียบระเบิดไหม
        {
            if (x[pid] == bomb.x[i] && y[pid] == bomb.y[i]) {
                checkhitbomb = true;
                removebomb = i;
                break;
            }

        }
        if (checkhitbomb) {
            checkhitbomb = false;
            bomb.x[removebomb] = bomb.newposX();
            bomb.x[removebomb] = bomb.newposY();
            health[pid] -= 5;
            step = true;
        }
        for (int i = 0; i < ETn; i++) // เช็คว่าตัวละครเหยียบ ET
        {
            if (x[pid] == et.x[i] && y[pid] == et.y[i]) {
                if (et.energy[i] > 0 && health[pid] < 100) {
                    health[pid] += 5;
                    et.energy[i] -= 5;
                    step = true;
                    break;
                }
            }
        }
        for (int i = 0; i < et.x.length; i++)// เอา et ออกไปข้างนอก
        {
            if (et.energy[i] == 0) {
                et.gooutside(i);
            }
        }
        if(health[pid] <= 0)
        {
            x[pid] = y[pid] = -3000;
            quit = true;
            return;
        }
        for (int i = 0; i < x.length; i++)
        {
            if(health[i] <= 0 && name[i] != null)
            {
                whodead =  "Player " + name[i] + " " + " Dead";
                // System.out.println(whodead);
                showdead = true;
                name[i] = null;
                direction[i] = null;
            }
        }
        for (int i = 0; i < bullet.size(); i++)// Border hit by bullet
        {
            if (bullet.get(i).x < 0 || bullet.get(i).x > 80 * tileSize || bullet.get(i).y < 0
                    || bullet.get(i).y > 100 * tileSize) {
                bullet.remove(i--);
                break;
            }
        }
        for (int i = 0; i < bullet.size(); i++) {
            for (int j = 0; j < bn; j++) // เช็คว่ากระสุนชนระเบิด
            {
                if (bullet.get(i).x == bomb.x[j] && bullet.get(i).y == bomb.y[j]) {
                    bomb.x[j] = bomb.newposX();
                    bomb.x[j] = bomb.newposY();
                    bullet.remove(i--);
                    break;
                }

            }
        }
        for (int i = 0; i < ETn; i++) {
            for (int j = 0; j < bullet.size(); j++)// เช็คว่ายิงET
            {
                if (bullet.get(j).x == et.x[i] && bullet.get(j).y == et.y[i]) {
                    bullet.remove(j--);
                    break;
                }
            }
        }
        for (int j = 0; j < bullet.size(); j++)// เช็คว่าโดนยิง
        {
            if (bullet.get(j).x == x[pid] && bullet.get(j).y == y[pid]) {
                Phit = hit = true;
                bullet.remove(j--);
                break;
            }
        }
        if (hit) {
            hit = false;
            health[pid] -= 50;
        }
    }

    @Override
    public void run() {
        while (running) {
            if (Thread.currentThread().getName().equals("Fire")) {
                for (int i = 0; i < 10; i++) {
                    if (fire[i]) {
                        if (direction[i].equals("up")) {
                            bullet.add(new Bullet(x[i], y[i] - tileSize, direction[i], tileSize));
                        } else if (direction[i].equals("down")) {
                            bullet.add(new Bullet(x[i], y[i] + tileSize, direction[i], tileSize));
                        } else if (direction[i].equals("left")) {
                            bullet.add(new Bullet(x[i] - tileSize, y[i], direction[i], tileSize));
                        } else if (direction[i].equals("right")) {
                            bullet.add(new Bullet(x[i] + tileSize, y[i], direction[i], tileSize));
                        }
                        fire[i] = false;
                        try {
                            Thread.currentThread().sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (Thread.currentThread().getName().equals("BulletTick")) {
                for (int i = 0; i < bullet.size(); i++) {
                    bullet.get(i).tick();
                }
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (Thread.currentThread().getName().equals("Event")) {
                event();
            } else if (Thread.currentThread().getName() == "RandomNewETpos") {
                for (int i = 0; i < et.x.length; i++) {
                    if (et.energy[i] == 0) {
                        try {
                            Thread.currentThread().sleep(20000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        et.randomnewpos(i);
                    }
                }
            } else if(Thread.currentThread().getName().equals("Whodead")) {
                showdead = false;
                try {
                    Thread.currentThread().sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                update();
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            repaint();
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            up = true;
            Pdirection = "up";
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            down = true;
            Pdirection = "down";
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            left = true;
            Pdirection = "left";
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            right = true;
            Pdirection = "right";
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            Pfire = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            shutdown();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            up = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            down = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            left = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            right = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            Pfire = false;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Bomb");
        frame.setSize(818, 680);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout(1, 1, 0, 0));
        frame.add(new Client());
        frame.setVisible(true);
    }
    class Input implements Runnable {
        ObjectInputStream in;
        Client c;
    
        public Input(ObjectInputStream in, Client c) {
            this.in = in;
            this.c = c;
        }
    
        @Override
        public void run() {
            while (running) {
                try {
                    int pid = (int) in.readObject();
                    int x = (int) in.readObject();
                    int y = (int) in.readObject();
                    Bomb b = (Bomb) in.readObject();
                    boolean fire = (boolean) in.readObject();
                    String direction = (String) in.readObject();
                    int health = (int) in.readObject();
                    ET et = (ET) in.readObject();
                    int p = (int) in.readObject();
                    String name = (String) in.readObject();
                    Color color = (Color) in.readObject();
                    ArrayList<Bullet> bullet = (ArrayList<Bullet>) in.readObject();
                    c.updateLocation(pid, x, y, b, fire, direction, health, et, p, name, color, bullet);
                } catch (IOException e) {
                    System.out.println("Fail to update");
                    shutdown();
                    // e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    System.out.println("Recieve Data Failed");
                    shutdown();
                }
            }
        }
    
        public void shutdown() {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}