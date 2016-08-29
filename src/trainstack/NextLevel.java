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
public class NextLevel extends Scene {

    Image bg;
    
    Image train;
    Image selector;
    
    Rectangle prevRectangle;
    Rectangle nextRectangle;
    
    Rectangle replayRectangle;
    Image replaySelector;
    
    Image selectButton;
    Rectangle selectRectangle;
    
    Image gray;
    
    int position;
    int state = 0;
    
    public NextLevel(Game parent) {
        super(parent);
        
        bg = Image.load("/resrc/nextlevel/bg.png");
        train = Image.load("/resrc/nextlevel/train.png");
        selector = Image.load("/resrc/nextlevel/selector.png");
        replaySelector = Image.load("/resrc/nextlevel/retryselector.png");
        gray = Image.load("/resrc/nextlevel/gray.png");
        
        prevRectangle = new Rectangle(-54, -21, 39, 15);
        nextRectangle = new Rectangle(-13, -21, 39, 15);
        replayRectangle = new Rectangle(28, -21, 15, 15);
        
        selectButton = Image.load("/resrc/nextlevel/select.png");
        selectRectangle = new Rectangle(-24, 17, 47, 12);
    }
    
    @Override
    public void draw(Canvas canvas) {
        canvas.draw(bg, -60, -45);
        canvas.draw(train, position - 185, -21);
        
        if(position == 131) {
            if(TrainStack.game.lastLoaded == 0) {
                canvas.draw(gray, prevRectangle.x, prevRectangle.y);
            }
            else if(prevRectangle.contains(Mouse.getPosition())) {
                canvas.draw(selector, prevRectangle.x, prevRectangle.y);
            }

            if(TrainStack.game.lastLoaded == 14) {
                canvas.draw(gray, nextRectangle.x, nextRectangle.y);
            }
            else if(nextRectangle.contains(Mouse.getPosition())) {
                canvas.draw(selector, nextRectangle.x, nextRectangle.y);
            }

            if(replayRectangle.contains(Mouse.getPosition())) {
                canvas.draw(replaySelector, replayRectangle.x, replayRectangle.y);
            }
            
            if(selectRectangle.contains(Mouse.getPosition())) {
                canvas.draw(selectButton, selectRectangle.x, selectRectangle.y);
            }
        }
    }

    @Override
    public void loop() {
        if(position < 131) {
            position += 8;
        }
        else {
            int prevState = state;
            if(TrainStack.game.lastLoaded != 0 && prevRectangle.contains(Mouse.getPosition())) {
                state = 1;
                if(Mouse.isLMBDown()) {
                    TrainStack.push.play();
                    TrainStack.game.loadLevel(TrainStack.game.lastLoaded - 1);
                    TrainStack.game();
                }
            }
            else if(TrainStack.game.lastLoaded != 14 && nextRectangle.contains(Mouse.getPosition())) {
                state = 2;
                if(Mouse.isLMBDown()) {
                    TrainStack.push.play();
                    TrainStack.game.loadLevel(TrainStack.game.lastLoaded + 1);
                    TrainStack.game();
                }
            }
            else if(replayRectangle.contains(Mouse.getPosition())) {
                state = 3;
                if(Mouse.isLMBDown()) {
                    TrainStack.push.play();
                    TrainStack.game.loadLevel(TrainStack.game.lastLoaded);
                    TrainStack.game();
                }
            }
            else if(selectRectangle.contains(Mouse.getPosition())) {
                state = 4;
                if(Mouse.isLMBDown()) {
                    TrainStack.push.play();
                    TrainStack.levelSelect();
                }
            }
            if(state != prevState) {
                TrainStack.select.play();
            }
        }
        if(position > 131) {
            position = 131;
        }
    }
    
}
