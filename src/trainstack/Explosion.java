/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainstack;

import alfredo.geom.Point;
import alfredo.paint.Animation;
import alfredo.paint.Image;

/**
 *
 * @author TheMonsterOfTheDeep
 */
public class Explosion {
    static Animation explosion;
    
    static void init() {
        explosion = Animation.load("/resrc/explode.png", 4);
    }
    
    int frame = 4;
    public Point position;
    
    public Explosion() {
    }
    
    void step() {
        if(frame < 4) {
            ++frame;
        }
    }
    
    boolean draw() {
        return frame != 4;
    }
    
    void setup(Point position) {
        this.position = position;
        this.frame = 0;
    }
    
    Image getFrame() {
        return explosion.getFrame(frame);
    }
}
