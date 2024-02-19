import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

public class SnakeGame extends JFrame implements KeyListener {
    private static final long serialVersionUID = 1L;

    private static final int GRID_SIZE = 20;
    private static final int TILE_SIZE = 20;

    private int[][] grid;
    private int snakeLength;
    private int[] snakeX, snakeY;
    private int direction;
    private boolean gameOver;

    public SnakeGame() {
        setTitle("Snake Game");
        setSize(GRID_SIZE * TILE_SIZE, GRID_SIZE * TILE_SIZE);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        grid = new int[GRID_SIZE][GRID_SIZE];
        snakeX = new int[GRID_SIZE * GRID_SIZE];
        snakeY = new int[GRID_SIZE * GRID_SIZE];

        direction = KeyEvent.VK_RIGHT; // start moving to the right
        addKeyListener(this);
        setFocusable(true);
        initializeGame();
    }

    private void initializeGame() {
        snakeLength = 3; // initial length of the snake
        for (int i = 0; i < snakeLength; i++) {
            snakeX[i] = snakeLength - i - 1;
            snakeY[i] = 0;
        }

        spawnFood();
        updateGrid();
        repaint();
        gameOver = false; // Set gameOver to false here
    }

    private void spawnFood() {
        int foodX, foodY;
        do {
            foodX = (int) (Math.random() * GRID_SIZE);
            foodY = (int) (Math.random() * GRID_SIZE);
        } while (grid[foodY][foodX] != 0);

        grid[foodY][foodX] = -1; // -1 represents the food
    }

    private void updateGrid() {
        // Clear the grid
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                grid[y][x] = 0;
            }
        }

        // Draw the snake
        for (int i = 0; i < snakeLength; i++) {
            grid[snakeY[i]][snakeX[i]] = i + 1;
        }

        // Draw the food
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                if (grid[y][x] == -1) {
                    grid[y][x] = -1; // food
                }
            }
        }
    }

    private void move() {
        if (!gameOver) {
            // Move the body
            for (int i = snakeLength - 1; i > 0; i--) {
                snakeX[i] = snakeX[i - 1];
                snakeY[i] = snakeY[i - 1];
            }

            // Move the head based on the current direction
            switch (direction) {
                case KeyEvent.VK_UP:
                    snakeY[0]--;
                    break;
                case KeyEvent.VK_DOWN:
                    snakeY[0]++;
                    break;
                case KeyEvent.VK_LEFT:
                    snakeX[0]--;
                    break;
                case KeyEvent.VK_RIGHT:
                    snakeX[0]++;
                    break;
            }

            // Check for collisions
            if (snakeX[0] < 0 || snakeX[0] >= GRID_SIZE || snakeY[0] < 0 || snakeY[0] >= GRID_SIZE) {
                gameOver = true;
            } else if (grid[snakeY[0]][snakeX[0]] > 0) {
                gameOver = true;
            } else if (grid[snakeY[0]][snakeX[0]] == -1) {
                // Snake eats the food
                snakeLength++;
                spawnFood();
            }

            // Update the grid
            updateGrid();
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if ((key == KeyEvent.VK_LEFT && direction != KeyEvent.VK_RIGHT) ||
            (key == KeyEvent.VK_RIGHT && direction != KeyEvent.VK_LEFT) ||
            (key == KeyEvent.VK_UP && direction != KeyEvent.VK_DOWN) ||
            (key == KeyEvent.VK_DOWN && direction != KeyEvent.VK_UP)) {
            direction = key;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                if (grid[y][x] > 0) {
                    g.setColor(Color.GREEN); // snake body
                } else if (grid[y][x] == -1) {
                    g.setColor(Color.RED); // food
                } else {
                    g.setColor(Color.BLACK); // empty cell
                }

                g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                g.setColor(Color.WHITE);
                g.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over", GRID_SIZE * TILE_SIZE / 4, GRID_SIZE * TILE_SIZE / 2);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SnakeGame snakeGame = new SnakeGame();
            snakeGame.setVisible(true);

            while (true) {
                if (snakeGame.gameOver) {
                    break;
                }

                snakeGame.move();
                try {
                    Thread.sleep(100); // control the speed of the snake
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
