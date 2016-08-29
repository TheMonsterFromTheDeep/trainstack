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
public class LevelSelect extends Scene {

    Image bg;
    Image topRow, middleRow, bottomRow;
    
    Image select;
    Rectangle selector;
    
    Image backButton;
    Rectangle backRectangle;
    
    Image gray;
    
    int selectedX, selectedY;
    
    int position;
    
    int state = -2;
    
    public LevelSelect(Game parent) {
        super(parent);
        
        bg = Image.load("/resrc/levelselect/bg.png");
        
        topRow = Image.load("/resrc/levelselect/1_5.png");
        middleRow = Image.load("/resrc/levelselect/6_10.png");
        bottomRow = Image.load("/resrc/levelselect/11_15.png");
        
        select = Image.load("/resrc/levelselect/select.png");
        
        backButton = Image.load("/resrc/levelselect/back.png");
        backRectangle = new Rectangle(-56, 29, 35, 12);
        
        gray = Image.load("/resrc/levelselect/gray.png");
        
        position = 0;
        
        selectedX = selectedY = -1;
        selector = new Rectangle(0, 0, 24, 25);
    }
    
    @Override
    public void draw(Canvas canvas) {
        canvas.draw(bg, -60, -45);
        
        canvas.draw(topRow, -206 + position, -44);
        canvas.draw(middleRow, 60 - position, -21);
        canvas.draw(bottomRow, -206 + position, 2);
        
        if(selectedX != -1 && selectedY != -1 && (selectedX + (selectedY * 5)) <= TrainStack.game.progress + 1) {
            canvas.draw(select, selectedX * 24 - 59, selectedY * 23 - 44);
        }
        
        if(position == 147) {
            for(int x = 0; x < 5; ++x) {
                for(int y = 0; y < 3; ++y) {
                    int level = (x + (y * 5));
                    if(level > TrainStack.game.progress + 1) {
                        canvas.draw(gray, x * 24 - 59, y * 23 - 44);
                    }
                }
            }
        }
        
        if(backRectangle.contains(Mouse.getPosition())) {
            canvas.draw(backButton, backRectangle.x, backRectangle.y);
        }
    }

    @Override
    public void loop() {
        if(position <= 146) {
            position += 8;
        }
        else {
            if(position > 147) {
                position = 147;
            }
            int prevState = state;
            selectedX = selectedY = -1;
            for(int x = 0; x < 5; ++x) {
                for(int y = 0; y < 3; ++y) {
                    if((x + (y * 5)) <= TrainStack.game.progress + 1) {
                        selector.x = x * 24 - 60;
                        selector.y = y * 23 - 45;
                        if(selector.contains(Mouse.getPosition())) {
                            selectedX = x;
                            selectedY = y;
                            state = selectedX + (selectedY * 5);
                            if(Mouse.isLMBDown()) {
                                TrainStack.push.play();
                                TrainStack.game.loadLevel(selectedX + (selectedY * 5));
                                TrainStack.game();
                            }
                        }
                    }
                }
            }
            if(backRectangle.contains(Mouse.getPosition())) {
                state = -1;
                if(Mouse.isLMBDown()) {
                    TrainStack.push.play();
                    TrainStack.menu();
                }
            }
            if(prevState != state) {
                TrainStack.select.play();
            }
        }
    }
    
}
