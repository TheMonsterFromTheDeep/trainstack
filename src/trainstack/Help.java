/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainstack;

import alfredo.Game;
import alfredo.geom.Rectangle;
import alfredo.input.Mouse;
import alfredo.paint.Canvas;
import alfredo.paint.Image;
import alfredo.scene.Scene;

/**
 *
 * @author TheMonsterOfTheDeep
 */
public class Help extends Scene {

    boolean pressed = false;
    int countdown = 0;
    int slide;
    
    Rectangle nextRectangle;
    
    Image bg;
    Image next;
    Image[] slides;
    
    public Help(Game parent) {
        super(parent);
        
        nextRectangle = new Rectangle(24, 32, 35, 12);
        
        bg = Image.load("/resrc/unlock/emptybg.png");
        next = Image.load("/resrc/unlock/next.png");
        slides = new Image[7]; //hardcoding ftw
        //i could put that comment a lot of places
        for(int i = 0; i < slides.length; ++i) {
            slides[i] = Image.load("/resrc/help/slide" + i + ".png");
        }
    }
    
    @Override
    public void onActivate() { //I wish I had remembed this method before
        slide = 0;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.draw(bg, -60, -45);
        
        if(nextRectangle.contains(Mouse.getPosition())) {
            canvas.draw(next, nextRectangle.x, nextRectangle.y);
        }
        
        canvas.draw(slides[slide], -59, -44);
    }

    @Override
    public void loop() {
        boolean lastPressed = pressed;
        if(nextRectangle.contains(Mouse.getPosition())) {
            pressed = true;
            if(lastPressed != pressed) { TrainStack.select.play(); }
            if(Mouse.isLMBDown() && countdown == 0) {
                TrainStack.push.play();
                if(slide < slides.length - 1) {
                    ++slide;
                    countdown = 10;
                }
                else {
                    TrainStack.menu();
                }
            }
        }
        if(countdown > 0) { --countdown; }
    }
    
}
