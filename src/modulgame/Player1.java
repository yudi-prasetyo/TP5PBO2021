package modulgame;

import java.awt.Color;
import java.awt.Graphics;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yudip
 */
public class Player1 extends Player {
    
    public Player1(int x, int y, ID id, String username, String difficulty){
        super(x, y, id, username, difficulty);
    }
    
    public void render(Graphics g) {
        g.setColor(Color.decode("#3f6082"));
        g.fillRect(x, y, 50, 50);
    }
}
