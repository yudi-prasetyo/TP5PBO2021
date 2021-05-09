/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author yudip
 */
public class Enemy extends GameObject {

    private int difficulty;

    public Enemy(int x, int y, ID id, String difficulty) {
        super(x, y, id);
        switch (difficulty) {
            case "Easy":
                this.difficulty = 4;
                break;
            case "Normal":
                this.difficulty = 5;
                break;
            case "Hard":
                this.difficulty = 6;
                break;
            default:
                break;
        }
    }

    // Enemy chase one player by player's coordinate
    public void chase(Player player) {
        if (player.getX() > this.x) {
            this.vel_x = this.difficulty;
            if (player.getY() > this.y) {
                this.vel_y = this.difficulty;
            } else {
                this.vel_y = -this.difficulty;
            }
        } else {
            this.vel_x = -this.difficulty;
            if (player.getY() > this.y) {
                this.vel_y = this.difficulty;
            } else {
                this.vel_y = -this.difficulty;
            }
        }
    }

    // Enemy chasing two players and choose the nearest player
    public void chase(Player player1, Player player2) {
        chase(getNearestPlayer(player1, player2));
    }

    // Get the nearest player from enemy
    public Player getNearestPlayer(Player player1, Player player2) {
        if (playerDistanceX(player1) > playerDistanceX(player2)) {
            if (playerDistanceY(player1) > playerDistanceY(player2)) {
                return player2;
            } else {
                return player1;
            }
        } else {
            if (playerDistanceY(player2) > playerDistanceY(player1)) {
                return player1;
            } else {
                return player2;
            }
        }
    }
    
    // Get player distance x from enemy 
    public int playerDistanceX(Player player) {
        return Math.abs(player.getX() - this.x);
    }
    
    // Get player distance y from enemy
    public int playerDistanceY(Player player) {
        return Math.abs(player.getY() - this.y);
    }

    @Override
    public void tick() {
        x += vel_x;
        y += vel_y;

        x = Game.clamp(x, 0, Game.WIDTH - 60);
        y = Game.clamp(y, 0, Game.HEIGHT - 80);

    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, 50, 50);
    }
}