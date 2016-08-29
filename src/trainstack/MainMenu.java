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
import alfredo.sound.Sound;
import alfredo.util.Resources;
import alfredo.util.ToolBox;

/**
 *
 * @author TheMonsterOfTheDeep
 */
public class MainMenu extends Scene {

    Image bg;
    
    Image playButton, helpButton;
    Rectangle playRect, helpRect;
    
    //Sound music;
    
    boolean playDown = false, helpDown = false;
    
    public MainMenu(Game parent) {
        super(parent);
        
        bg = Image.load("/resrc/menu.png");
        
        playButton = Image.load("/resrc/menu/playButton.png");
        helpButton = Image.load("/resrc/menu/helpButton.png");
        
        //music = Resources.loadSound("/resrc/menu/music.wav");
        
        playRect = new Rectangle(-3, 31, 29, 12);
        helpRect = new Rectangle(32, 31, 27, 12);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.draw(bg, -60, -45);
        if(playDown) {
            canvas.draw(playButton, playRect.x, playRect.y);
        }
        if(helpDown) {
            canvas.draw(helpButton, helpRect.x, helpRect.y);
        }
    }

    @Override
    public void loop() {
        int prevState = playDown ? 1 : helpDown ? 2 : 0;
        playDown = playRect.contains(Mouse.getMouseX(), Mouse.getMouseY());
        helpDown = helpRect.contains(Mouse.getMouseX(), Mouse.getMouseY());
        int state = playDown ? 1 : helpDown ? 2 : 0;
        if(state != prevState) {
            TrainStack.select.play();
        }
        
        if(Mouse.isLMBDown()) {
            if(playDown) {
                //parent.setScene(TrainStack.game);
                TrainStack.push.play();
                TrainStack.levelSelect();
            }
            if(helpDown) {
                TrainStack.push.play();
                TrainStack.help();
            }
        }
    }
    
}
