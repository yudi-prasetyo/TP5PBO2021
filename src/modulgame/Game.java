/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Fauzan
 */
public class Game extends Canvas implements Runnable{
    Window window;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    
    
    private Thread thread;
    private boolean running = false;
    
    private Handler handler;
    
    private boolean multiplayer = false;
    private String difficulty;
    
    public enum STATE{
        Game,
        GameOver
    };
    
    public STATE gameState = STATE.Game;
    
    public Game(ArrayList<String> username, String difficulty){
        window = new Window(WIDTH, HEIGHT, "Modul praktikum 5", this);
        
        handler = new Handler();

        this.addKeyListener(new KeyInput(handler, this));
        this.difficulty = difficulty;
        
        if(gameState == STATE.Game){
            handler.addObject(new Enemy(randomNumber(WIDTH - 100),randomNumber(HEIGHT - 100), ID.Enemy, difficulty));
            handler.addObject(new Items(randomNumber(WIDTH - 100),randomNumber(HEIGHT - 100), ID.Item));
            handler.addObject(new Items(randomNumber(WIDTH - 100),randomNumber(HEIGHT - 100), ID.Item));
            handler.addObject(new Player1(randomNumber(WIDTH - 100),randomNumber(HEIGHT - 100), ID.Player1, username.get(0), difficulty));
            
            if (username.size() > 1) {
                handler.addObject(new Player2(randomNumber(WIDTH - 100),randomNumber(HEIGHT - 100), ID.Player2, username.get(1), difficulty));
                this.multiplayer = true;
            }
        }
    }

    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }
    
    public synchronized void stop(){
        try{
            thread.join();
            running = false;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        playSound("/opening.wav");
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        
        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            
            while(delta >= 1){
                tick();
                delta--;
            }
            if(running){
                render();
                frames++;
            }
            
            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                frames = 0;
                if(gameState == STATE.Game){
                    ArrayList<Player> playerObject = handler.getPlayersObject();
                    Enemy enemy = handler.getEnemyObject();
                    
                    if (multiplayer)
                        enemy.chase(playerObject.get(0), playerObject.get(1));
                    else
                        enemy.chase(playerObject.get(0));
    
                    int gameOver = 0;

                    for (int i = 0; i < playerObject.size(); i++) {
                        if (!playerObject.get(i).isDeath()) {
                            playerObject.get(i).reduceTime();
                        } else {
                            gameOver++;
                        }
                    }

                    if (gameOver == playerObject.size()) {
                        gameState = STATE.GameOver;
                        dbConnection dbcon = new dbConnection();
                        dbcon.saveData(playerObject.get(0));

                        if (this.multiplayer)
                            dbcon.saveData(playerObject.get(1));
                    }
                }
            }
        }
        stop();
    }
    
    private void tick(){
        handler.tick();
        if(gameState == STATE.Game){
            ArrayList<Player> playerObject = handler.getPlayersObject();
            
            if(playerObject != null){
                if (handler.object.size() - playerObject.size() > 1) {
                    for(int i=0;i< handler.object.size(); i++){
                        if(handler.object.get(i).getId() == ID.Item){
                            for (int j = 0; j < playerObject.size(); j++) {
                                if(!playerObject.get(j).isDeath() && checkCollision(playerObject.get(j), handler.object.get(i), 20)){
                                    playSound("/Eat.wav");
                                    handler.removeObject(handler.object.get(i));
                                    playerObject.get(j).addScore();
                                    playerObject.get(j).addTime();
                                    break;
                                }
                            }
                        } else if(handler.object.get(i).getId() == ID.Enemy) {
                            for (int j = 0; j < playerObject.size(); j++) {
                                if(!playerObject.get(j).isDeath() && checkCollision(playerObject.get(j), handler.object.get(i), 50)){
                                    playSound("/hit_enemy.wav");
                                    playerObject.get(j).death();
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    handler.addObject(new Items(randomNumber(WIDTH - 100), randomNumber(HEIGHT - 100), ID.Item));
                }
            }
        }
    }
    
    public static boolean checkCollision(GameObject player, GameObject otherObject, int objectSize){
        boolean result = false;
        
        int sizePlayer = 50;
        int sizeItem = objectSize;
        
        int playerLeft = player.x;
        int playerRight = player.x + sizePlayer;
        int playerTop = player.y;
        int playerBottom = player.y + sizePlayer;
        
        int itemLeft = otherObject.x;
        int itemRight = otherObject.x + sizeItem;
        int itemTop = otherObject.y;
        int itemBottom = otherObject.y + sizeItem;
        
        if((playerRight > itemLeft ) &&
        (playerLeft < itemRight) &&
        (itemBottom > playerTop) &&
        (itemTop < playerBottom)
        ){
            result = true;
        }
        
        return result;
    }
    
    private void render(){
        ArrayList<Player> playerObject = handler.getPlayersObject();

        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        
        Graphics g = bs.getDrawGraphics();
        
        g.setColor(Color.decode("#F1f3f3"));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        if(gameState ==  STATE.Game){
            
            handler.render(g);
            
            Font currentFont = g.getFont();
            Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4F);
            g.setFont(newFont);

            g.setColor(Color.BLACK);
            g.drawString("Score " +  playerObject.get(0).getUsername()+ ": " + Integer.toString(playerObject.get(0).getScore()), 20, 20);
            
            if (this.multiplayer) {
                g.setColor(Color.BLACK);
                g.drawString("Score " +  playerObject.get(1).getUsername()+ ": " + Integer.toString(playerObject.get(1).getScore()), 20, 40);
            }

            g.setColor(Color.BLACK);
            g.drawString("Time " +  playerObject.get(0).getUsername()+ ": " +Integer.toString(playerObject.get(0).getTime()), WIDTH-120, 20);
            
            if (this.multiplayer) {
                g.setColor(Color.BLACK);
                g.drawString("Time " +  playerObject.get(1).getUsername()+ ": " + Integer.toString(playerObject.get(1).getTime()), WIDTH-120, 40);
            }
        }else{
            Font currentFont = g.getFont();
            Font newFont = currentFont.deriveFont(currentFont.getSize() * 3F);
            g.setFont(newFont);

            g.setColor(Color.BLACK);
            g.drawString("GAME OVER", WIDTH/2 - 120, HEIGHT/2 - 30);

            currentFont = g.getFont();
            Font newScoreFont = currentFont.deriveFont(currentFont.getSize() * 0.5F);
            g.setFont(newScoreFont);

            g.setColor(Color.BLACK);
            g.drawString("Score " +  playerObject.get(0).getUsername()+ ": " +Integer.toString(playerObject.get(0).getScore()), WIDTH/2 - 50, HEIGHT/2 - 10);
            
            if (this.multiplayer) {
                g.setColor(Color.BLACK);
                g.drawString("Score " +  playerObject.get(1).getUsername()+ ": " +Integer.toString(playerObject.get(1).getScore()), WIDTH/2 - 50, HEIGHT/2 + 10);
            }
            
            g.setColor(Color.BLACK);
            g.drawString("Press Space to Continue", WIDTH/2 - 100, HEIGHT/2 + 30);
        }        

        g.dispose();
        bs.show();
    }
    
    public static int clamp(int var, int min, int max){
        if(var >= max){
            return var = max;
        }else if(var <= min){
            return var = min;
        }else{
            return var;
        }
    }
    
    public void close(){
        window.CloseWindow();
    }
    
    public void playSound(String filename){
        try {
            // Open an audio input stream.
            URL url = this.getClass().getResource(filename);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
           e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        } catch (LineUnavailableException e) {
           e.printStackTrace();
        }
    
    }
    
    public int randomNumber(int upperBound) {
        return (int) ((Math.random() * upperBound) + 1);
    }
}
