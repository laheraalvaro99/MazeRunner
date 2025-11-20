package com.example.mazerunner.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class GameView extends View {

    private Paint wallPaint;
    private Paint goalPaint;
    private Paint playerPaint;

    private int[][] maze;
    private float cellSize;

    private float offsetX = 0;
    private float offsetY = 0;

    private float playerX = 1;
    private float playerY = 1;

    private boolean levelCompleted = false;

    // -------------------- SCORE SYSTEM --------------------
    private int score = 1000;
    private int timer = 0;
    private boolean timerRunning = false;

    public interface OnScoreUpdateListener {
        void onScoreUpdate(int currentScore);
    }
    private OnScoreUpdateListener scoreUpdateListener;
    public void setOnScoreUpdateListener(OnScoreUpdateListener listener) {
        this.scoreUpdateListener = listener;
    }

    private Paint gemPaint;

    private int[][] gems; // Mapa de gemas (1 = hay gema)
    private long lastTimeCheck = System.currentTimeMillis();

    // Listener para enviar score final
    public interface OnScoreListener {
        void onLevelFinished(int finalScore);
    }
    private OnScoreListener scoreListener;

    public void setOnScoreListener(OnScoreListener listener) {
        this.scoreListener = listener;
    }


    // Listener de meta alcanzada
    public interface OnGoalReachedListener {
        void onGoalReached();
    }

    private OnGoalReachedListener goalListener;

    public void setOnGoalReachedListener(OnGoalReachedListener listener) {
        this.goalListener = listener;
    }

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);

        goalPaint = new Paint();
        goalPaint.setColor(Color.GREEN);

        playerPaint = new Paint();
        playerPaint.setColor(Color.RED);

        // Generamos un laberinto aleatorio de 21x21 (tamaño recomendable)
        maze = generateMaze(21, 21);

        // Colocamos meta en la esquina inferior derecha
        maze[maze.length - 2][maze[0].length - 2] = 9;
        // Crear mapa de gemas
        gems = new int[maze.length][maze[0].length];

        // Pintura para gemas
        gemPaint = new Paint();
        gemPaint.setColor(Color.CYAN);

        // Generar gemas en cada celda caminable
        for (int y = 0; y < maze.length; y++) {
            for (int x = 0; x < maze[0].length; x++) {
                if (maze[y][x] == 0) {
                    gems[y][x] = 1; // Gema colocada
                }
            }
        }

    }

    // --------------------------
    //     GENERADOR LABERINTO
    // --------------------------

    private int[][] generateMaze(int rows, int cols) {
        int[][] maze = new int[rows][cols];

        // Inicializar todo como paredes
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                maze[y][x] = 1;
            }
        }

        Random random = new Random();

        // Empezamos en celda impar
        carve(1, 1, maze, rows, cols, random);

        return maze;
    }

    private void carve(int x, int y, int[][] maze, int rows, int cols, Random random) {
        int[] dx = {0, 0, -2, 2};
        int[] dy = {-2, 2, 0, 0};

        Integer[] dirs = {0, 1, 2, 3};
        Collections.shuffle(Arrays.asList(dirs), random);

        maze[y][x] = 0;

        for (int dir : dirs) {
            int nx = x + dx[dir];
            int ny = y + dy[dir];

            if (ny > 0 && ny < rows - 1 && nx > 0 && nx < cols - 1) {
                if (maze[ny][nx] == 1) {

                    // Abrimos la pared intermedia
                    maze[y + dy[dir] / 2][x + dx[dir] / 2] = 0;

                    carve(nx, ny, maze, rows, cols, random);
                }
            }
        }
    }

    // --------------------------
    //         DIBUJADO
    // --------------------------

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int rows = maze.length;
        int cols = maze[0].length;

        cellSize = Math.min(getWidth() / (float) cols, getHeight() / (float) rows);

        offsetX = (getWidth() - cols * cellSize) / 2f;
        offsetY = (getHeight() - rows * cellSize) / 2f;

        // Dibujar celdas
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {

                float left = offsetX + x * cellSize;
                float top = offsetY + y * cellSize;
                float right = left + cellSize;
                float bottom = top + cellSize;

                if (maze[y][x] == 1) {
                    canvas.drawRect(left, top, right, bottom, wallPaint);
                }

                if (maze[y][x] == 9) {
                    canvas.drawRect(left, top, right, bottom, goalPaint);
                }
            }
        }

        // Dibujar gemas
        for (int y = 0; y < gems.length; y++) {
            for (int x = 0; x < gems[0].length; x++) {

                if (gems[y][x] == 1) {
                    float cx = offsetX + x * cellSize + cellSize / 2;
                    float cy = offsetY + y * cellSize + cellSize / 2;

                    canvas.drawCircle(cx, cy, cellSize / 5, gemPaint);
                }
            }
        }


        // Dibujar jugador (círculo)
        canvas.drawCircle(
                offsetX + playerX * cellSize + cellSize / 2,
                offsetY + playerY * cellSize + cellSize / 2,
                cellSize / 3,
                playerPaint
        );
    }

    // --------------------------
    //   MOVIMIENTO Y COLISIONES
    // --------------------------

    public void updateBallPosition(float ax, float ay) {
        float speed = 0.04f;

        float dx = ax * speed;
        float dy = ay * speed;

        movePlayer(dx, dy);
    }

    public void movePlayer(float dx, float dy) {

        // ---------------- TIMER ----------------
        if (!timerRunning) {
            timerRunning = true;
            lastTimeCheck = System.currentTimeMillis();
        } else {
            long now = System.currentTimeMillis();
            if (now - lastTimeCheck >= 1000) {
                timer++;
                score -= 10; // -10 puntos por segundo
                lastTimeCheck = now;
            }
        }




        float newX = playerX + dx;
        float newY = playerY + dy;

        // ---------------- GEMAS ----------------
        int ix = Math.round(playerX);
        int iy = Math.round(playerY);

        if (ix >= 0 && iy >= 0 && iy < gems.length && ix < gems[0].length) {
            if (gems[iy][ix] == 1) {
                gems[iy][ix] = 0;
                score += 100;
            }
        }

        if (scoreUpdateListener != null) {
            scoreUpdateListener.onScoreUpdate(score);
        }


        // Colisión horizontal
        if (!isWall(newX, playerY)) {
            playerX = newX;
        }

        // Colisión vertical
        if (!isWall(playerX, newY)) {
            playerY = newY;
        }

        // Detectar meta
        if (!levelCompleted && isGoal(playerX, playerY)) {
            levelCompleted = true;
            timerRunning = false;

            if (scoreListener != null) {
                scoreListener.onLevelFinished(score);
            }

            if (goalListener != null) {
                goalListener.onGoalReached();
            }
        }



        invalidate();
    }

    private boolean isWall(float x, float y) {
        float radius = cellSize / 3f;

        float px = offsetX + x * cellSize + cellSize / 2;
        float py = offsetY + y * cellSize + cellSize / 2;

        float left = px - radius;
        float right = px + radius;
        float top = py - radius;
        float bottom = py + radius;

        int cellLeft = (int) ((left - offsetX) / cellSize);
        int cellRight = (int) ((right - offsetX) / cellSize);
        int cellTop = (int) ((top - offsetY) / cellSize);
        int cellBottom = (int) ((bottom - offsetY) / cellSize);

        if (cellLeft < 0 || cellTop < 0 || cellBottom >= maze.length || cellRight >= maze[0].length) {
            return true;
        }

        return maze[cellTop][cellLeft] == 1 ||
                maze[cellTop][cellRight] == 1 ||
                maze[cellBottom][cellLeft] == 1 ||
                maze[cellBottom][cellRight] == 1;
    }

    private boolean isGoal(float x, float y) {
        int ix = Math.round(x);
        int iy = Math.round(y);

        if (ix < 0 || iy < 0 || iy >= maze.length || ix >= maze[0].length) {
            return false;
        }

        return maze[iy][ix] == 9;
    }

    public void resetLevel() {
        levelCompleted = false;
        playerX = 1;
        playerY = 1;

        score = 1000;
        timer = 0;
        timerRunning = false;

        maze = generateMaze(21, 21);
        maze[maze.length - 2][maze[0].length - 2] = 9;

        // Regenerar gemas
        gems = new int[maze.length][maze[0].length];
        for (int y = 0; y < maze.length; y++) {
            for (int x = 0; x < maze[0].length; x++) {
                if (maze[y][x] == 0) {
                    gems[y][x] = 1;
                }
            }
        }

        invalidate();
    }

    public int getScore() {
        return score;
    }


}
