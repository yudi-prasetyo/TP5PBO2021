/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author Fauzan
 */
public class Handler {
    LinkedList<GameObject> object = new LinkedList<GameObject>();
    private boolean multiplayer = false;
    
    public void tick(){
        for(int i=0;i<object.size(); i++){
            GameObject tempObject = object.get(i);
            
            tempObject.tick();
        }
    }
    
    public void render(Graphics g){
        for(int i=0;i<object.size(); i++){
            if (object.get(i) instanceof Player) {
                Player tempPlayer = (Player) object.get(i);
                if (!tempPlayer.isDeath())
                    tempPlayer.render(g);
            } else {
                GameObject tempObject = object.get(i);
                tempObject.render(g);
            }
        }
    }
    
    public void addObject(GameObject object){
        this.object.add(object);
    }
    
    public void removeObject(GameObject object){
        this.object.remove(object);
    }
    
    // Get all players object from handler
    public ArrayList<Player> getPlayersObject() {
        ArrayList<Player> playersArray = new ArrayList<Player>();
        for (int i = 0; i < this.object.size(); i++) {
            if (this.object.get(i) instanceof Player) {
                playersArray.add((Player) this.object.get(i));
            }
        }
        
        if (!this.multiplayer && playersArray.size() > 1)
            this.multiplayer = true;
        
        return playersArray;
    }
    
    public Enemy getEnemyObject() {
        
        for (int i = 0; i < this.object.size(); i++) {
            if (this.object.get(i) instanceof Enemy) {
                return (Enemy) this.object.get(i);
            }
        }
        
        return null;
    }
}
