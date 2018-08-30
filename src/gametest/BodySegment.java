/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gametest;

import java.awt.Graphics;
import java.awt.Image;
import java.util.List;

/**
 *
 * @author f_lapi
 */
public class BodySegment {

    private int x;
    private int y;
    private int length;
    private Image sprite;
    private BodySegment tail;

    public BodySegment() {

    }

    public BodySegment(int x, int y, Image sprite) {
        length = 1;
        this.x = x;
        this.y = y;
        this.sprite = sprite;
    }

    public boolean contains(int x, int y) {
        if (this.x == x && this.y == y) {
            return true;
        }
        return tail != null && tail.contains(x, y);
    }

    public void updateHead(int xDistance, int yDistance, List<Food> foods) {

        if (tail != null) {
            tail.update(x, y);
        }
        x += xDistance;
        y += yDistance;
        for (Food food : foods) {
            if (x == food.getX() && y == food.getY()) {
                addPiece();
                food.newLocation();                
            }
        }
    }

    /**
     * This method ads a body segment to the end of the snake
     */
    public void addPiece() {
        length++;
        if (tail == null) {
            tail = new BodySegment(x, y, sprite);
        } else {
            tail.addPiece();
        }
    }

    public void update(int x, int y) {
        if (tail != null) {
            tail.update(this.x, this.y);
        }
        this.x = x;
        this.y = y;
    }

    public void Paint(Graphics g, GameTest observer) {
        //g.drawImage(img, x, y, observer)
        g.drawImage(sprite, x, y, observer);

        if (tail != null) {
            tail.Paint(g, observer);
        }
    }

    public BodySegment getTail() {
        return tail;
    }

    public void setTail(BodySegment value) {
        tail = value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int value) {
        x = value;
    }

    public void setY(int value) {
        y = value;
    }

    public int getLength() {
        return length;
    }
    
    public void moveOffScreen()
    {
        x = 10000;
        y = 10000;
        if (tail != null)
            tail.moveOffScreen();
    }

}
