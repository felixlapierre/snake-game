/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gametest;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author f_lapi
 */
public class Food {
    private int TilesWide;
    private int TilesHigh;
    private int TileSize;
    
    private List<BodySegment> snakes = new ArrayList<>();
    
    private int x;
    private int y;
    private Image sprite;
    
    public Food(Image sprite, int TilesWide, int TilesHigh, int TileSize, List<BodySegment> snakes)
    {
        this.snakes = snakes;
        this.sprite = sprite;
        this.TileSize = TileSize;
        this.TilesHigh = TilesHigh;
        this.TilesWide = TilesWide;
    }
    
    public int getX() {return x;}
    public int getY() {return y;}

    public void newLocation()
    {
        boolean validLocation;
        do
        {
            validLocation = true;
            x = TileSize * (int)(Math.random() * TilesWide);
            y = TileSize * (int)(Math.random() * TilesHigh);
            for (BodySegment snake : snakes)
            {
                if (snake.contains(x,y))
                    validLocation = false;
            }
        } while(!validLocation);
    }
    
    public void setLocation(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    public void Paint(Graphics g, GameTest observer)
    {
        g.drawImage(sprite, x, y, observer);
    }
}
