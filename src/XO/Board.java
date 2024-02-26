package XO;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Board extends JPanel {
    private static final int N = 3;
    private static final int M = 3;

    public static final int ST_DRAW = 0;
    public static final int ST_WIN = 1;
    public static final int ST_NORMAL = 2;

    private EndGameListener endGameListener;
    private Image imgX;
    private Image imgO;
    private Cell[][] matrix = new Cell[N][M];
    private String currentPlayer = Cell.EMPTY_VALUE;

    public Board(String player){
        this();
        this.currentPlayer = player;
    }

    public Board(){
        this.initMatrix();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                int x = e.getX();
                int y = e.getY();

                if(currentPlayer.equals(Cell.EMPTY_VALUE)){
                    return;
                }

                soundClick();
                for(int i = 0 ; i < N; i++){
                    for(int j = 0 ; j < M; j++){
                        Cell cell = matrix[i][j];

                        int cXStart = cell.getX();
                        int cYStart = cell.getY();

                        int cXEnd = cXStart + cell.getW();
                        int cYEnd = cYStart + cell.getH();

                        if(x >= cXStart && x <= cXEnd && y >= cYStart && y <= cYEnd){
                            if(cell.getValue().equals(Cell.EMPTY_VALUE)){
                                cell.setValue(currentPlayer);
                                repaint();
                                checkResult(currentPlayer);
                                currentPlayer = currentPlayer.equals(Cell.O_VALUE) ? Cell.X_VALUE : Cell.O_VALUE; // Change player
                                if (currentPlayer.equals(Cell.X_VALUE)) {
                                    makeAIMove(); // AI makes a move if it's the AI's turn
                                }
                            }
                        }
                    }
                }
            }
        });
        try{
            imgX = ImageIO.read(getClass().getResource("x.png"));
            imgO = ImageIO.read(getClass().getResource("o.png"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private synchronized void soundClick(){
    	Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("clicker.wav"));
                    clip.open(audioInputStream);
                    clip.start();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void initMatrix(){
    	for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                Cell cell = new Cell();
                matrix[i][j] = cell;
            }
        }
    }

 
    private void checkResult(String player) {
        int result = checkWin(player);
        if (endGameListener != null) {
            endGameListener.end(player, result);
        }
    }
    
    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setEndGameListener(EndGameListener endGameListener) {
        this.endGameListener = endGameListener;
    }

    

    public void reset(){
        this.initMatrix();
        this.setCurrentPlayer(Cell.EMPTY_VALUE);
        repaint();
    }

    public int checkWin(String player){
        if(this.matrix[0][0].getValue().equals(player) && this.matrix[1][1].getValue().equals(player) && this.matrix[2][2].getValue().equals(player)){
            return ST_WIN;
        }

        if(this.matrix[0][2].getValue().equals(player) && this.matrix[1][1].getValue().equals(player) && this.matrix[2][0].getValue().equals(player)){
            return ST_WIN;
        }

        for(int i = 0; i < N; i++){
            if(this.matrix[i][0].getValue().equals(player) && this.matrix[i][1].getValue().equals(player) && this.matrix[i][2].getValue().equals(player)){
                return ST_WIN;
            }
        }

        for(int i = 0; i < M; i++){
            if(this.matrix[0][i].getValue().equals(player) && this.matrix[1][i].getValue().equals(player) && this.matrix[2][i].getValue().equals(player)){
                return ST_WIN;
            }
        }

        if(this.isFull()){
            return ST_DRAW;
        }

        return ST_NORMAL;
    }

    private boolean isFull(){
        int number = N * M;
        int k = 0;
        for(int i = 0 ; i < N; i++){
            for(int j = 0 ; j < M; j++){
                Cell cell = matrix[i][j];
                if(!cell.getValue().equals(Cell.EMPTY_VALUE)){
                    k++;
                }
            }
        }
        return k == number;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth() / 3;
        int h = getHeight() / 3;

        Graphics2D graphic2d = (Graphics2D) g;

        int k = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                int x = j * w;
                int y = i * h;

                Cell cell = matrix[i][j];
                cell.setX(x);
                cell.setY(y);
                cell.setW(w);
                cell.setH(h);

                Color color = k % 2 == 0 ? Color.PINK : Color.GRAY;
                graphic2d.setColor(color);
                graphic2d.fillRect(x, y, w, h);

                if (cell.getValue().equals(Cell.X_VALUE)) {
                    graphic2d.drawImage(imgX, x, y, w, h, this);
                } else if (cell.getValue().equals(Cell.O_VALUE)) {
                    graphic2d.drawImage(imgO, x, y, w, h, this);
                }

                k++;
            }
        }
    }

    public void makeAIMove() {
        if (isFull() || checkWin(Cell.X_VALUE) != ST_NORMAL) {
            return;
        }

        char[] boardState = convertBoardStateToArray();
        int bestMove = miniMax(boardState, 'O');
        int row = bestMove / N;
        int col = bestMove % N;
        Cell cell = matrix[row][col];
        if (cell.getValue().equals(Cell.EMPTY_VALUE)) { // Check if the cell is empty
            cell.setValue(currentPlayer);
            repaint();
            checkResult(currentPlayer);
            currentPlayer = currentPlayer.equals(Cell.O_VALUE) ? Cell.X_VALUE : Cell.O_VALUE;
        }
    }


    private char[] convertBoardStateToArray() {
        char[] boardState = new char[N * M];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                Cell cell = matrix[i][j];
                String value = cell.getValue();
                char charValue = value.isEmpty() ? '-' : value.charAt(0);
                boardState[i * N + j] = charValue;
            }
        }
        return boardState;
    }


    public int miniMax(char[] node, char playerSymbol) {
        int gameResult = checkWin(node);
        if (gameResult != ST_NORMAL) {
            return score(gameResult);
        }
        if (playerSymbol == 'O') {
            int bestScore = Integer.MIN_VALUE;
            int bestMove = -1;
            for (int i = 0; i < node.length; i++) {
                if (node[i] == '-') {
                    node[i] = playerSymbol;
                    int score = miniMax(node, 'X');
                    node[i] = '-';
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = i;
                    }
                }
            }
            return bestMove;
        } else {
            int bestScore = Integer.MAX_VALUE;
            int bestMove = -1;
            for (int i = 0; i < node.length; i++) {
                if (node[i] == '-') {
                    node[i] = playerSymbol;
                    int score = miniMax(node, 'O');
                    node[i] = '-';
                    if (score < bestScore) {
                        bestScore = score;
                        bestMove = i;
                    }
                }
            }
            return bestMove;
        }
    }

    private int checkWin(char[] node) {
        if (node[0] == node[4] && node[4] == node[8] && node[0] != '-') {
            return ST_WIN;
        }
        if (node[2] == node[4] && node[4] == node[6] && node[2] != '-') {
            return ST_WIN;
        }
        for (int i = 0; i < N; i++) {
            if (node[i * N] == node[i * N + 1] && node[i * N + 1] == node[i * N + 2] && node[i * N] != '-') {
                return ST_WIN;
            }
            if (node[i] == node[i + N] && node[i + N] == node[i + 2 * N] && node[i] != '-') {
                return ST_WIN;
            }
        }
        for (int i = 0; i < node.length; i++) {
            if (node[i] == '-') {
                return ST_NORMAL;
            }
        }
        return ST_DRAW;
    }

    private int score(int gameResult) {
        switch (gameResult) {
            case ST_WIN:
                return 10;
            case ST_DRAW:
                return 0;
            default:
                return -10;
        }
    }
    
    public synchronized void soundStart() {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("StartS.wav"));
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
