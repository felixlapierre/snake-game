package gametest;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.input.KeyCode;

public class GameTest extends Applet implements Runnable, KeyListener {

    long previousMilliseconds;
    int timeSinceSnakeMoved;
    final int timeBetweenSnakeMoves = 100;
    final int tileSize = 40;
    int timeBetweenExtraFood = 1500;
    int timeBetweenExtraFoodRemaining = timeBetweenExtraFood;
    
    List<Integer> nextSnakeMoveX;
    List<Integer> nextSnakeMoveY;
    List<Integer> previousSnakeMoveX;
    List<Integer> previousSnakeMoveY;

    int startingLength = 3;
    long timeSinceStart = 0;
    
    
    List<BodySegment> snakes = new ArrayList<>();
    
    //tilesHigh and tilesWide must always be multiples of four
    private final int tilesWide = 36;
    private final int tilesHigh = 24;
    
    private final int WIDTH = tileSize * tilesWide;
    private final int HEIGHT = tileSize * tilesHigh;

    private final int[][] startingLocations = {
        {tileSize * (tilesWide / 4 - 1), tileSize * tilesHigh / 4},
        {tileSize * (tilesWide  * 3 / 4 + 1), tileSize * tilesHigh * 3 / 4},
        {tileSize * tilesWide * 3 / 4, tileSize * (tilesHigh / 4 - 1)},
        {tileSize * tilesWide / 4, tileSize * (tilesHigh * 3 / 4 + 1)}
    };
    private final int[][] startingSpeeds = {
        {0,1}, {0,-1}, {-1,0},{1,0}
    };
    
    private final Color BACKGROUND_COLOR = Color.BLACK;
    private Image image, foodImage;
    private List<Image> bodyImages;
    private Graphics second;
    
    private URL base;
    
    List<Food> foods = new ArrayList<>();
    
    Random random = new Random();
    
    private final boolean mapLoops = true;
    
    private final boolean infinite = false;
    
    private final boolean superFoodMode = false;
    
    private final int numberOfSnakes = 2;
    
    private final int numberOfFoods = numberOfSnakes;
    
    int[] scores = new int[numberOfSnakes];
    private boolean[] snakeLost;
    int[][] snakeKeys = {{KeyEvent.VK_W ,KeyEvent.VK_A ,KeyEvent.VK_S, KeyEvent.VK_D},
        {KeyEvent.VK_UP, KeyEvent.VK_LEFT, KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT},
        {KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD6},
        {KeyEvent.VK_U, KeyEvent.VK_H, KeyEvent.VK_J, KeyEvent.VK_K}
    };
    
    
    @Override
    public void init() {
        
        
        if (infinite)
            startingLength = 1000;
        setSize(WIDTH, HEIGHT);
        setBackground(BACKGROUND_COLOR);
        setFocusable(true);
        Frame frame = (Frame) this.getParent().getParent();
        frame.setTitle("SNAKE");
        addKeyListener(this);
        
        previousMilliseconds = System.currentTimeMillis();
        
        try {
            base = getCodeBase();
        } catch (Exception e) {
            //TODO: Catch a cold
        }
        
        //Setup image:
        bodyImages = new ArrayList<>();
        bodyImages.add(getImage(base, "graphics/snakeboi1.png"));
        bodyImages.add(getImage(base, "graphics/snakeboi2.png"));
        bodyImages.add(getImage(base, "graphics/snakeboi3.png"));
        bodyImages.add(getImage(base, "graphics/snakeboi4.png"));

        foodImage = getImage(base, "graphics/foodboi.png");
        
        //Create snakes
        resetGame();

        
    }
    
    public void resetGame()
    {
        nextSnakeMoveX = new ArrayList<>();
        nextSnakeMoveY = new ArrayList<>();
        previousSnakeMoveX = new ArrayList<>();
        previousSnakeMoveY = new ArrayList<>();
        snakeLost = new boolean[numberOfSnakes];
        
        snakes.clear();
        for(int i = 0; i < numberOfSnakes; i++)
        {
            snakes.add(new BodySegment(startingLocations[i][0], startingLocations[i][1], bodyImages.get(i)));
            nextSnakeMoveX.add(startingSpeeds[i][0] * tileSize);
            nextSnakeMoveY.add(startingSpeeds[i][1] * tileSize);
            previousSnakeMoveX.add(0);
            previousSnakeMoveY.add(0);
        }
        
        foods.clear();
        for (int i = 0; i < numberOfFoods; i++)
        {
            foods.add(new Food(foodImage, tilesWide, tilesHigh, tileSize, snakes));
        }

        if (infinite)
        {
            for (Food food : foods)
                food.setLocation(1000,1000);
        }
        else
        {
            for (Food food : foods)
                food.newLocation();
        }
        for (int i = 0; i < startingLength - 1; i++)
        {
            for(BodySegment snake : snakes)
                snake.addPiece();
        }

        timeSinceStart = 0;
    }

    @Override
    public void start() {


        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void run() {
        while (true) {
            repaint();
            try {
                Thread.sleep(17);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    /**
     * Updates the game
     */
    public void update(Graphics g) {
        //Figure out how much time has elapsed since the last update
        long elapsedTime = System.currentTimeMillis() - previousMilliseconds;
        
        timeSinceStart += elapsedTime;
        
        //Update the previousMilliseconds
        previousMilliseconds = System.currentTimeMillis();
        
        //Increment time since snake last moved
        timeSinceSnakeMoved += elapsedTime;
        timeBetweenExtraFoodRemaining += elapsedTime;
        
        //Is it time to move the snake?????
        if (timeSinceStart > 1000 && timeSinceSnakeMoved > timeBetweenSnakeMoves)
        {
            timeSinceSnakeMoved = 0;
            for(int i = 0; i < snakes.size(); i++)
            {
                snakes.get(i).updateHead(nextSnakeMoveX.get(i), nextSnakeMoveY.get(i), foods);
                previousSnakeMoveX.set(i, nextSnakeMoveX.get(i));
                previousSnakeMoveY.set(i, nextSnakeMoveY.get(i));
            }
            
            for(int i = 0; i < snakes.size(); i++)
            {
                BodySegment snake = snakes.get(i);
                if (mapLoops)
                {
                    offMapCheck(snake);
                }
                if (!mapLoops && (snake.getX() < 0 || snake.getX() >= WIDTH 
                        || snake.getY() < 0 || snake.getY() >= HEIGHT))
                    snakeLost[i] = true;

                if(timeSinceStart > 2000)
                {
                    if (snake.getTail() != null && snake.getLength() > 4 && snake.getTail().contains(snake.getX(),snake.getY()))
                        snakeLost[i] = true;
                    for(int j = 0; j < snakes.size(); j++)
                    {
                        BodySegment otherSnake = snakes.get(j);
                        if (i != j && otherSnake.getTail() != null && otherSnake.getTail().contains(snake.getX(), snake.getY()))
                            snakeLost[i] = true;
                    }
                }
            }
            int numSnakesLost = 0;
            int snakeThatWon = -1;
            for(int i = 0; i < snakeLost.length; i++)
            {
                if (snakeLost[i])
                {
                    numSnakesLost++;
                    snakes.get(i).moveOffScreen();
                }
                else
                    snakeThatWon = i;
            }
            if (numSnakesLost == numberOfSnakes - 1)//One snake wins
            {
                System.out.println("Snake " + (snakeThatWon + 1) + " won!");
                scores[snakeThatWon] += 3;

            }
            else if (numSnakesLost == numberOfSnakes) //No snakes win
            {
                System.out.println("No Snakes Win :(");
            }
            if (numSnakesLost >= numberOfSnakes - 1)
            {
                for(int i = 0; i < numberOfSnakes; i++)
                {
                    scores[i] += snakes.get(i).getLength() - startingLength;
                    System.out.println("Snake " + (i + 1) + " has " + scores[i] + " points.");
                }
                resetGame();
            }

        }
        
        if (timeBetweenExtraFoodRemaining >= timeBetweenExtraFood)
        {
            timeBetweenExtraFoodRemaining = 0;
            if (superFoodMode)
            {
                Food newFood = new Food(foodImage, tilesWide, tilesHigh, tileSize, snakes);
                newFood.newLocation();
                foods.add(newFood);
            }
        }
        
        if (image == null) {
            image = createImage(this.getWidth(), this.getHeight());
            second = image.getGraphics();
        }
        
        
        
        second.setColor(getBackground());
        second.fillRect(0,0,getWidth(),getHeight());
        second.setColor(getForeground());
        paint(second);
        
        g.drawImage(image, 0, 0, this);
        
    }

    @Override
    public void paint(Graphics g) {
        for(BodySegment snake : snakes)
            snake.Paint(g, this);
        for (Food food : foods)
            food.Paint(g,this);
    }
    
    

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        for(int i = 0; i < numberOfSnakes; i++)
            checkSnakeKeys(e.getKeyCode(),i);
    }

    public void checkSnakeKeys(int k, int i)
    {
        if (k == snakeKeys[i][0]) //Up
        {
            if (previousSnakeMoveY.get(i) == tileSize)
                return;
            nextSnakeMoveX.set(i, 0);
            nextSnakeMoveY.set(i, -tileSize);
        }
        else if (k == snakeKeys[i][1]) //Left
        {
            if (previousSnakeMoveX.get(i) == tileSize)
                return;
            nextSnakeMoveX.set(i, -tileSize);
            nextSnakeMoveY.set(i, 0);
        }
        else if (k == snakeKeys[i][2]) //Down
        {
            if (previousSnakeMoveY.get(i) == -tileSize)
                return;
            nextSnakeMoveX.set(i, 0);
            nextSnakeMoveY.set(i, tileSize);
        }
        else if (k == snakeKeys[i][3]) //Right
        {
            if (previousSnakeMoveX.get(i) == -tileSize)
                return;
            nextSnakeMoveX.set(i, tileSize);
            nextSnakeMoveY.set(i, 0);
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                break;
            case KeyEvent.VK_LEFT:
                break;
            case KeyEvent.VK_RIGHT:
                break;
            case KeyEvent.VK_DOWN:
                break;
            case KeyEvent.VK_SPACE:
                break;
        }
    }

    private void offMapCheck(BodySegment snake) {
        if (snake.getX() < 0)
            snake.setX(WIDTH - tileSize);
        else if (snake.getX() >= WIDTH)
            snake.setX(0);
        else if (snake.getY() < 0)
            snake.setY(HEIGHT - tileSize);
        else if (snake.getY() >= HEIGHT)
            snake.setY(0);
    }

}
