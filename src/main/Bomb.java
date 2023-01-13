package main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;


public class Bomb implements Serializable
{
	int x[], y[], positionx[], positiony[];
	public Bomb(int[] x, int[] y)
	{
		this.x = x;
		this.y = y;
		positionx = new int[x.length];
		positiony = new int[y.length];
		// x = (int)((Math.random() * 80)) * 32;
		// y = (int)((Math.random() * 100)) * 32;
	}
	public void draw(Graphics2D g, int px, int py, int Xscreen, int Yscreen, BufferedImage img)
	{
		for (int i = 0; i < x.length; i++)
		{
			positionx[i] =  x[i] - px + Xscreen;
			positiony[i] = y[i] - py + Yscreen;
			g.drawImage(img, positionx[i], positiony[i], 32, 32, null);
		}
		
	}
	public int newposX()
	{
		return (int)((Math.random() * 80)) * 32;
	}
	public int newposY()
	{
		return (int)((Math.random() * 100)) * 32;
	}
}
