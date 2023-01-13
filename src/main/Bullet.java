package main;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.awt.Color;
public class Bullet implements Serializable
{
    int dx, dy, x, y, width, height, speed, xx, yy;
    String direction;
    public Bullet(int x, int y, String direction, int tileSize)
    {
        this.x = x;
        this.y = y;
        this.height = this.width = this.speed = tileSize;
        this.direction = direction;
        dx = dy = 0;
    }
    public void tick()
    {
        if(this.direction.equals("up"))
        {
            dx = 0;
            dy = -speed;
        }
        if(this.direction.equals("down"))
        {
            dx = 0;
            dy = speed;
        }
        if(this.direction.equals("left"))
        {
            dy = 0;
            dx = -speed;
        }
        if(this.direction.equals("right"))
        {
            dy = 0;
            dx = speed;
        }
        this.x += dx;
        this.y += dy;
    }
    public void draw(Graphics2D g, int px, int py , int Xscreen, int Yscreen)
	{
        xx = this.x -  px + Xscreen;
        yy = this.y - py + Yscreen;
        g.setColor(Color.MAGENTA);
		g.fillRect(xx+13, yy+13, this.width/4, this.height/4);
	}
}