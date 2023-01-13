package main;

import java.util.Random;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class ET implements Serializable
{
	int x[], y[], positionx[], positiony[],energy[];
	public ET(int[] x, int[] y, int[] energy)
	{
        this.x = x;
        this.y = y;
        this.positionx = new int[x.length];
        this.positiony = new int[y.length];
		// x = (int)((Math.random() * 80)) * 32;
		// y = (int)((Math.random() * 100)) * 32;
        this.energy = energy;
	}
	public void draw(Graphics2D g, int px, int py, int screenX, int screenY, BufferedImage img)
	{
		g.setColor(Color.BLACK);
		g.setFont(new Font("Angsana New", Font.BOLD, 24));
        for(int i = 0 ; i < x.length ; i++)
        {
            positionx[i] =  x[i] - px + screenX;
		    positiony[i] = y[i] - py + screenY;
            g.drawImage(img, positionx[i], positiony[i], 32, 32, null);
		    g.drawString(Integer.toString(energy[i]), positionx[i]+8,positiony[i]+24);
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
	public void gooutside(int index)
	{
		x[index] = -20*32;
        y[index] = 0;
	}
	public void randomnewpos(int index)
	{
		x[index] = newposX();
        y[index] = newposY();
		int randenergy[] = {50,55,60,65,70,75,80,85,90,95};
        energy[index] = randenergy[new Random().nextInt(randenergy.length)];
	}
}
