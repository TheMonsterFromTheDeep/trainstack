/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainstack;

import alfredo.paint.Animation;
import alfredo.paint.Image;
import alfredo.sound.Sound;
import alfredo.util.Resources;

/**
 *
 * @author TheMonsterOfTheDeep
 */
public class SpikeTrack {
    Animation anim;
   // Sound fx;
    
    int[] frames = new int[] {
        0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 4, 3, 2, 1, 0, 0
    };
    
    //boolean step = false;
    int frame;
    
    public SpikeTrack() {
        anim = Animation.load("/resrc/tile/spiketrack.png", 5);
        //fx = Resources.loadSound("/resrc/snd/saw.wav");
    }
    
    public Image getFrame() {
        return anim.getFrame(frames[frame]); //frame frame frame frame frame frame
    }
    
    public void step() {
        //if(step) {
            ++frame;
            //if(frame == 9) { fx.play(); }
        //}
        if(frame >= frames.length) {
            frame = 0;
        }
    }
    
    public void reset() {
        frame = 0;
        //step = false;
    }
    
    public boolean active() {
        return frames[frame] == 4;
    }
}
