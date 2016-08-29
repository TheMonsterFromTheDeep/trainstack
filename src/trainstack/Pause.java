/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainstack;

import alfredo.Game;
import alfredo.geom.Rectangle;
import alfredo.input.Keys;
import alfredo.input.Mouse;
import alfredo.paint.Canvas;
import alfredo.paint.Image;
import alfredo.scene.Scene;
import java.awt.event.KeyEvent;

/**
 *
 * @author TheMonsterOfTheDeep
 */
public class Pause extends Scene {
    Image bg;
    
    Rectangle quitRectangle;
    Rectangle continueRectangle;
    
    Image quit;
    Image cont;
    
    Image train;
    
    int position;
    int state;

    public Pause(Game parent) {
        super(parent);
        
        quitRectangle = new Rectangle(-54, -21, 39, 15);
        continueRectangle = new Rectangle(-13, -21, 63, 15);
        
        bg = Image.load("/resrc/pause/bg.png");
        quit = Image.load("/resrc/pause/quit.png");
        cont = Image.load("/resrc/pause/continue.png");
        
        train = Image.load("/resrc/pause/train.png");
    }
    
    @Override
    public void draw(Canvas canvas) {
        canvas.draw(bg, -60, -45);
        
        canvas.draw(train, position - 192, -21);
        
        if(position == 138) {
            if(quitRectangle.contains(Mouse.getPosition())) {
                canvas.draw(quit, quitRectangle.x, quitRectangle.y);
            }
            
            if(continueRectangle.contains(Mouse.getPosition())) {
                canvas.draw(cont, continueRectangle.x, continueRectangle.y);
            }
        }
    }

    @Override
    public void loop() {
        if(position < 138) {
            position += 8;
        }
        else {
            int prevState = state;
            if(quitRectangle.contains(Mouse.getPosition())) {
                state = 1;
                if(Mouse.isLMBDown()) {
                    TrainStack.push.play();
                    TrainStack.menu();
                }
            }
            if(continueRectangle.contains(Mouse.getPosition())) {
                state = 2;
                if(Mouse.isLMBDown()) {
                    TrainStack.push.play();
                    TrainStack.game();
                }
            }
            if(state != prevState) {
                TrainStack.select.play();
            }
        }
        if(position > 138) {
            position = 138;
        }
        if(Keys.pressed(KeyEvent.VK_ESCAPE)) {
            TrainStack.game();
        }
    }
}
