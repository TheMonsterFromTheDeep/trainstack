/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainstack;

import alfredo.paint.Image;

/**
 *
 * @author TheMonsterOfTheDeep
 */
public class Tutorial {
    public static Tutorial nullTutorial = new Tutorial();
    
    Action[] actions;
    Image[] display;
    
    int current = 0;
    
    public Action getAction() {
        return current >= actions.length ? null : actions[current];
    }
    
    public boolean hasAction() {
        return current < actions.length;
    }
    
    public void next() {
        ++current;
    }
    
    public Image getImage() {
        return display[current];
    }
    
    private Tutorial() {
        actions = new Action[0];
        current = 1;
    }
    
    public Tutorial(int level, Action... actions) {
        this.actions = actions;
        display = new Image[actions.length];
        for(int i = 0; i < display.length; ++i) {
            display[i] = Image.load("/resrc/tut/" + level + "_" + i + ".png");
        }
    }
    
    public Tutorial(Image[] display, Action... actions) {
        this.actions = actions;
        this.display = display;
    }
}
