package main;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tile
{
	Client c;
	BufferedImage image;
	public Tile(Client c)
	{
		this.c = c;
		try
		{
			image = ImageIO.read(getClass().getResourceAsStream("/Tile/Map_tile_10.png"));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public void draw(Graphics2D g)
	{
		int col = 0, row = 0;
		while(col < 80 && row < 100)
		{
			int worldx = col*c.tileSize;
			int worldy = row*c.tileSize;
			int screenx = worldx - c.x[c.pid] + c.Xscreen;
			int screeny = worldy - c.y[c.pid] + c.Yscreen;
			g.drawImage(image, screenx, screeny, c.tileSize, c.tileSize, null);
			col++;
			if(col == 80)
			{
				col = 0;
				row ++;
			}
		}
	}
}
