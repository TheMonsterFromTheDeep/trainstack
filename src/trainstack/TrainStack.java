/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainstack;

import alfredo.Game;
import alfredo.paint.Image;
import alfredo.scene.Scene;
import alfredo.sound.Sound;
import alfredo.util.Resources;
import alfredo.util.ToolBox;

/**
 *
 * @author TheMonsterOfTheDeep
 */
public class TrainStack extends Game {

    public static int TRACK_MENU = 0;
    public static int TRACK_NEXTLEVEL = 1;
    public static int TRACK_GAME = 2;
    
    public static GameScene game;
    public static MainMenu menu;
    public static LevelSelect levelSelect;
    public static NextLevel nextLevel;
    
    static Pause pause;
    
    static Help help;
    
    static Unlock unlock;
    static TrackUnlock trackUnlock;
    
    static Transition transition;
    static Scene current;
    
    private static TrainStack ts;
    
    private static int track;
    private static Sound[] tracks;
    
    public static Sound select, push;

    
    
    public TrainStack() {
        super("TrainStack", 120, 90, 6);
        //this.setDelay(100);
        setIcon(Image.load("/resrc/icon.png"));
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ts = new TrainStack();
        ts.init();
        
        menu = new MainMenu(ts);
        levelSelect = new LevelSelect(ts);
        game = new GameScene(ts);
        nextLevel = new NextLevel(ts);
        unlock = new Unlock(ts);
        trackUnlock = new TrackUnlock(ts);
        help = new Help(ts);
        pause = new Pause(ts);
        
        ts.setScene(menu);
        ToolBox.addDefaultFullscreen();
        
        tracks = new Sound[] {
            Resources.loadSound("/resrc/menu/music.wav"),
            Resources.loadSound("/resrc/nextlevel/music.wav"),
            Resources.loadSound("/resrc/music.wav")
        };
        
        select = Resources.loadSound("/resrc/snd/select.wav");
        push = Resources.loadSound("/resrc/snd/push.wav");
        
        transition = new Transition(ts);
        
        current = menu;
        
        track = 0;
        tracks[0].loop();
        ts.run();
    }
    
    public static void menu() {
        setTrack(TRACK_MENU);
        menu.playDown = menu.helpDown = false;
        transition.transition(current, menu);
        ts.setScene(transition);
        current = menu;
        //ts.setScene(menu);
    }
    
    public static void pause() {
        setTrack(TRACK_MENU);
        pause.position = 0;
        pause.state = 0;
        transition.transition(current, pause);
        ts.setScene(transition);
        current = pause;
        //ts.setScene(menu);
    }
    
     public static void help() {
        setTrack(TRACK_MENU);
        help.slide = 0;
        transition.transition(current, help);
        ts.setScene(transition);
        current = help;
        //ts.setScene(menu);
    }
    
    public static void game() {
        setTrack(TRACK_GAME);
        game.playing = false;
        game.ticksSinceStart = 0;
        transition.transition(current, game);
        ts.setScene(transition);
        current = game;
        //ts.setScene(game);
    }
    
    public static void levelSelect() {
        setTrack(TRACK_MENU);
        levelSelect.state = -2;
        levelSelect.position = 0;
        levelSelect.selectedX = levelSelect.selectedY = -1;
        transition.transition(current, levelSelect);
        ts.setScene(transition);
        current = levelSelect;
        //ts.setScene(levelSelect);
    }
    
    public static void nextLevel() {
        setTrack(TRACK_NEXTLEVEL);
        nextLevel.position = 0;
        nextLevel.state = 0;
        transition.transition(current, nextLevel);
        ts.setScene(transition);
        current = nextLevel;
        //ts.setScene(nextLevel);
    }
    
    public static void setTrack(int number) {
        if(track != number) {
            if(track >= 0 && track < tracks.length) {
                tracks[track].stop();
            }
            track = number;
            if(number >= 0 && number < tracks.length) {
                tracks[number].loop();
            }
        }
    }
    
    static void unlock(String name, Image trainCar) {
        setTrack(TRACK_NEXTLEVEL);
        unlock.position = 0;
        unlock.unlock(name, trainCar);
        transition.transition(current, unlock);
        ts.setScene(transition);
        current = unlock;
    }
    
    static void unlockTrack(String name) {
        setTrack(TRACK_NEXTLEVEL);
        trackUnlock.unlock(name);
        transition.transition(current, trackUnlock);
        ts.setScene(transition);
        current = trackUnlock;
    }
}
