/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;

import java.awt.Graphics;

/**
 *
 * @author Fauzan
 */
public abstract class Player extends GameObject{
    
    private String username;
    private int score = 0;
    private int time;
    private int duration;
    
    public Player(int x, int y, ID id, String username, String difficulty){
        super(x, y, id);
        this.username = username;
        
        switch (difficulty) {
            case "Easy":
                this.time = this.duration = 20;
                break;
            case "Normal":
                this.time = this.duration = 10;
                break;
            case "Hard":
                this.time = this.duration = 5;
                break;
            default:
                break;
        }
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public int getScore() {
        return this.score;
    }
    
    public void addScore() {
        int addedScore = (int) ((Math.random() * 20));
        this.score += addedScore;
    }
    
    public int getTime() {
        return this.time;
    }
    
    public int getDuration() {
        return this.duration;
    }
    
    public int getTotalScore() {
        return this.score + this.duration;
    }
    
    public void reduceTime() {
        this.time -= 1;
    }
    
    public void addTime() {
        int addedTime = (int) ((Math.random() * 20));
        this.time += addedTime;
        this.duration += addedTime;
    }
    
    public boolean isDeath() {
        if (this.time > 0)
            return false;
        return true;
    }
    
    public void death() {
        this.duration -= this.time;
        this.time = 0;
        this.x = 9999;
        this.y = 9999;
    }

    @Override
    public void tick() {
        x += vel_x;
        y += vel_y;
        
        if (!isDeath()) {
            x = Game.clamp(x, 0, Game.WIDTH - 60);
            y = Game.clamp(y, 0, Game.HEIGHT - 80);
        }
    }
    
    @Override
    public abstract void render(Graphics g);
}
