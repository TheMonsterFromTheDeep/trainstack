/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainstack;

import alfredo.Game;
import alfredo.paint.Canvas;
import alfredo.paint.Image;
import alfredo.scene.Scene;

/**
 *
 * @author TheMonsterOfTheDeep
 */
public class Transition extends Scene {

    Canvas c1, c2;
    Scene s1, s2;
    Image i1, i2;
    int position;
    
    public Transition(Game parent) {
        super(parent);
        c1 = new Canvas(120, 90);
        c2 = new Canvas(120, 90);
        
        i1 = new Image();
        i2 = new Image();
    }
    
    public void transition(Scene s1, Scene s2) {
        this.s1 = s1;
        this.s2 = s2;
        
        s1.draw(c1);
        s2.draw(c2);
        
        i1.image = c1.getRender();
        i2.image = c2.getRender();
        position = -45;
    }

    @Override
    public void draw(Canvas canvas) {
        
        if(s2 == TrainStack.game || s2 == TrainStack.menu) {
            canvas.draw(i1, -60, -position - 90);
            canvas.draw(i2, -60, -position - 180);
        }
        else {
            canvas.draw(i1, -60, position);
            canvas.draw(i2, -60, position + 90);
        }
    }

    @Override
    public void loop() {
        position -= 5;
        //System.out.println("Pos: " + position);
        if(position == -135) {
            parent.setScene(s2);
        }
        //s1.loop();
        //s2.loop();
    }
    
}
