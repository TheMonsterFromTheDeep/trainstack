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
public class Unlock extends Scene {

    Image bg;
    Image next;
    
    Image topTitle;
    
    Rectangle nextRectangle;
    boolean pressed;
    
    Image title;
    Image desc;
    Image car;
    
    Image engine;
    
    int position;

    public Unlock(Game parent) {
        super(parent);
        
        nextRectangle = new Rectangle(24, 32, 35, 12);
        
        bg = Image.load("/resrc/unlock/bg.png");
        next = Image.load("/resrc/unlock/next.png");
        topTitle = Image.load("/resrc/unlock/title.png");
        engine = Image.load("/resrc/cars/engine.png");
    }
    
    public void unlock(String name, Image car) {
        title = Image.load("/resrc/unlock/" + name + "title.png");
        desc = Image.load("/resrc/unlock/" + name + "desc.png");
        this.car = car;
        
        position = 0;
    }
    
    @Override
    public void draw(Canvas canvas) {
        canvas.draw(bg, -60, -45);
        
        if(nextRectangle.contains(Mouse.getPosition())) {
            canvas.draw(next, nextRectangle.x, nextRectangle.y);
        }
        
        canvas.draw(topTitle, -topTitle.image.getWidth() / 2, -44);
        canvas.draw(title, -title.image.getWidth() / 2, -36);
        canvas.draw(desc, -59, 3);
        
        canvas.draw(engine, position - 45 - engine.image.getWidth(), -1 - engine.image.getHeight());
        canvas.draw(car, position - 45 - (engine.image.getWidth() + car.image.getWidth()), -1 - car.image.getHeight());
    }

    @Override
    public void loop() {
        if(position < 45 + (engine.image.getWidth() + car.image.getWidth()) / 2) {
            position += 5;
        }
        else {
            boolean lastPressed = pressed;
            if(nextRectangle.contains(Mouse.getPosition())) {
                pressed = true;
                if(lastPressed != pressed) { TrainStack.select.play(); }
                if(Mouse.isLMBDown()) {
                    TrainStack.push.play();
                    TrainStack.nextLevel();
                }
            }
            
        }
        if(position > 45 + (engine.image.getWidth() + car.image.getWidth()) / 2) {
            position = 45 + (engine.image.getWidth() + car.image.getWidth()) / 2;
        }
    }
    
}
