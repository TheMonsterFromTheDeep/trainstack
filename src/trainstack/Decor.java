/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainstack;

import alfredo.geom.Point;
import alfredo.paint.Image;

/**
 *
 * @author TheMonsterOfTheDeep
 */
public class Decor {
    public Point position;
    public Image appearance;
    
    public Decor(Point position, Image appearance) {
        this.position = position;
        this.appearance = appearance;
    }
}
