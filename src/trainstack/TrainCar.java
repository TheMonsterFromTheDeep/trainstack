/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainstack;

import alfredo.geom.Rectangle;
import alfredo.paint.Image;

/**
 *
 * @author TheMonsterOfTheDeep
 */
public class TrainCar {
    public Image appearance;
    public Image outline;
    public Rectangle bounds;
    
    public boolean hasHealth = false;
    public boolean burnt = false;
    public boolean boost = false;
    
    public int type;
    
    public int backwardsOffset = 0;
    public boolean onTrack = false;
    
    public TrainCar(Image appearance, Image outline, int type) {
        this.appearance = appearance;
        this.outline = outline;
        this.type = type;
        this.bounds = new Rectangle(0, 0, appearance.image.getWidth(), appearance.image.getHeight());
    }
}
