/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainstack;

import alfredo.Game;
import alfredo.geom.Point;
import alfredo.geom.Rectangle;
import alfredo.input.Keys;
import alfredo.input.Mouse;
import alfredo.paint.Animation;
import alfredo.paint.Canvas;
import alfredo.paint.Image;
import alfredo.scene.Scene;
import alfredo.sound.Sound;
import alfredo.util.Resources;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

/**
 *
 * @author TheMonsterOfTheDeep
 */
public class GameScene extends Scene {
    //<editor-fold defaultstate="collapsed" desc="Tile/Car constants">
    static final int TILE_TRACK = 0;
    static final int TILE_UPTRACK = 1;
    static final int TILE_DOWNTRACK = 2;
    static final int TILE_BEGINTRACK = 3;
    static final int TILE_CONTINUEUPTRACK = 4;
    static final int TILE_CONTINUEDOWNTRACK = 5;
    static final int TILE_FINISHTRACK = 6;
    static final int TILE_SAWTRACK = 7;
    static final int TILE_WEAKTRACK = 8;
    static final int TILE_FIRE = 9;
    static final int TILE_SPIKE = 10;
    static final int TILE_SPEED = 11;
    static final int TILE_COUNT = 12;
    
    static final int CAR_ENGINE = 0;
    static final int CAR_COAL = 1;
    static final int CAR_LOG = 2;
    static final int CAR_FUEL = 3;
    static final int CAR_BOOST = 4;
    static final int CAR_HEALTH = 5;
    static final int CAR_DRAINED = 6;
    static final int CAR_ANCHOR = 7;
    static final int CAR_ANTIBOOST = 8;
    static final int CAR_ANTIHEALTH = 9;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Color constants">
    final Color CLIFF_COLOR = new Color(0xbf9a79);
    final Color LAND_COLOR = new Color(0xd2b397);
    final Color CLIFF_TOP_COLOR = new Color(0xa98666);
    final Color LAND_SIDE_COLOR = new Color(0xc9a584);
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Tile/map data">
    final Image[] tiles;
    final int[] tileHeights;
    
    int[] map;
    int[] heightmap;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="decor data">
    final Image[] decorImages;
    Decor[] decor;
    //</editor-fold>
    
    final Image[] trainCars;
    final Image[] trainCarOutlines;
    TrainCar[] train;
    final ArrayList<TrainCar> builtTrain;
    final ArrayList<TrainCar> loadedTrain;
    TrainCar active;
    Point dragPoint;
    boolean alive;
    
    float trainPosition;
    float trainSpeed;
    float trainAcc;
    float maxSpeed;
    
    final Image lightTrack, lightStraight;
    
    float scroll = 0;
    
    int[][] levels;
    
    boolean playing = false;
    boolean buttonHighlighted = false;
    int buttonCooldown = 0;
    
    final Image stopButton;
    final Image stopButtonPressed;
    final Image playButton;
    final Image playButtonPressed;
    
    final Rectangle switchStateButton;
    
    SawTrack sawTrack;
    SpikeTrack spikeTrack;
    Tutorial tutorial;
    
    Sound trainUp, trainDown;
    Sound explode;
    
    public int lastLoaded = 0;
    int progress = -1;
    
    int ticksSinceStart = 0;
    
    final Explosion[] explosions;
    int currentExplosion = 0;
    
    Animation fire;
    Animation speed;
    
    public GameScene(Game parent) {
        super(parent);
        
        tiles = new Image[TILE_COUNT];
        tiles[0] = Image.load("/resrc/tile/track.png");
        tiles[1] = Image.load("/resrc/tile/uptrack.png");
        tiles[2] = Image.load("/resrc/tile/downtrack.png");
        tiles[3] = Image.load("/resrc/tile/begintrack.png");
        tiles[4] = Image.load("/resrc/tile/continueuptrack.png");
        tiles[5] = Image.load("/resrc/tile/continuedowntrack.png");
        tiles[6] = Image.load("/resrc/tile/finishtrack.png");
        
        sawTrack = new SawTrack();
        tiles[7] = sawTrack.getFrame();
        
        tiles[8] = Image.load("/resrc/tile/weaktrack.png");
        
        fire = Animation.load("/resrc/tile/fire.png",3);
        fire.loop(0, 2);
        tiles[9] = fire.getCurrentFrame();
        
        spikeTrack = new SpikeTrack();
        tiles[10] = spikeTrack.getFrame();
        
        speed = Animation.load("/resrc/tile/speedtrack.png",3);
        speed.loop(0, 2);
        tiles[11] = speed.getCurrentFrame();
        
        tileHeights = new int[TILE_COUNT];
        tileHeights[TILE_TRACK] = 0;
        tileHeights[TILE_UPTRACK] = 3;
        tileHeights[TILE_DOWNTRACK] = -3;
        tileHeights[TILE_BEGINTRACK] = 0;
        tileHeights[TILE_CONTINUEUPTRACK] = 6;
        tileHeights[TILE_CONTINUEDOWNTRACK] = -6;
        tileHeights[TILE_FINISHTRACK] = 0;
        tileHeights[TILE_SAWTRACK] = 0;
        tileHeights[TILE_WEAKTRACK] = 0;
        
        stopButton = Image.load("/resrc/stopButton.png");
        stopButtonPressed = Image.load("/resrc/stopButtonPressed.png");
        playButton = Image.load("/resrc/playButton.png");
        playButtonPressed = Image.load("/resrc/playButtonPressed.png");
        
        switchStateButton = new Rectangle(-59, 32, 12, 12);
        
        decorImages = new Image[] {
            Image.load("/resrc/decor/cactus1.png"),
            Image.load("/resrc/decor/cactus2.png"),
            Image.load("/resrc/decor/cactus3.png"),
            Image.load("/resrc/decor/rock1.png"),
            Image.load("/resrc/decor/rock2.png"),
            Image.load("/resrc/decor/tumble1.png"),
            Image.load("/resrc/decor/tumble2.png"),
        };
        
        trainCars = new Image[] {
            Image.load("/resrc/cars/engine.png"),
            Image.load("/resrc/cars/coalcar.png"),
            Image.load("/resrc/cars/logger.png"),
            Image.load("/resrc/cars/fuelcar.png"),
            Image.load("/resrc/cars/booster.png"),
            Image.load("/resrc/cars/health.png"),
            Image.load("/resrc/cars/drained.png"),
            Image.load("/resrc/cars/anchor.png"),
            Image.load("/resrc/cars/antibooster.png"),
            Image.load("/resrc/cars/antihealth.png"),
        };
        
        trainCarOutlines = new Image[] {
            Image.load("/resrc/outline/engine.png"),
            Image.load("/resrc/outline/coalcar.png"),
            Image.load("/resrc/outline/logger.png"),
            Image.load("/resrc/outline/fuelcar.png"),
            Image.load("/resrc/outline/booster.png"),
            Image.load("/resrc/outline/health.png"),
            Image.load("/resrc/cars/drained.png"),
            Image.load("/resrc/outline/anchor.png"),
            Image.load("/resrc/outline/antibooster.png"),
            Image.load("/resrc/outline/antihealth.png"),
        };
        
        lightTrack = Image.load("/resrc/lightTrack.png");
        lightStraight = Image.load("/resrc/lightStraight.png");
        
        //train[1].backwardsOffset = train[1].appearance.image.getWidth();
        //trainPosition = 30;
        //train[0].bounds.x = 59 - train[0].appearance.image.getWidth();
        //train[0].bounds.y = -44;
        //train[1].bounds.x = 59 - train[1].appearance.image.getWidth();
        //train[1].bounds.y = -43 + train[0].bounds.height;
        
        levels = new int[1][];
        
        levels[0] = new int[] {
            TILE_BEGINTRACK, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, TILE_FINISHTRACK
        };
        
        //loadMap(new int[] {
        //    3, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 4, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 5, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 6
        //});
        
        builtTrain = new ArrayList();
        loadedTrain = new ArrayList();
        
        trainUp = Resources.loadSound("/resrc/snd/trainup.wav");
        trainDown = Resources.loadSound("/resrc/snd/traindown.wav");
        
        explosions = new Explosion[6];
        for(int i = 0; i < explosions.length; ++i) {
            explosions[i] = new Explosion();
        }
        Explosion.init();
        
        explode = Resources.loadSound("/resrc/snd/explode.wav");
        
        File testSave = new File("ld36-trainstack-save.txt");
            if(testSave.exists()) {
            FileReader inputFile;
            try {
                inputFile = new FileReader("ld36-trainstack-save.txt");
                BufferedReader input = new BufferedReader(inputFile);

                if("LD36:TRAINSTACK:SAVE".equals(input.readLine())) {
                    progress = Integer.parseInt(input.readLine());
                    if(progress < -1) { progress = -1; } //Confirm that progress is good number
                    if(progress > 14) { progress = 14; }
                }
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
            }
        }
    }
    
    int getLevel(char token) {
        switch(token) {
            case '$':
                return TILE_BEGINTRACK;
            case '@':
                return TILE_FINISHTRACK;
            case 'S':
                return TILE_TRACK;
            case 'u':
                return TILE_UPTRACK;
            case 'U':
                return TILE_CONTINUEUPTRACK;
            case 'd':
                return TILE_DOWNTRACK;
            case 'D':
                return TILE_CONTINUEDOWNTRACK;
            case '*':
                return TILE_SAWTRACK;
            case 'W':
                return TILE_WEAKTRACK;
            case 'F':
                return TILE_FIRE;
            case '^':
                return TILE_SPIKE;
            case '>':
                return TILE_SPEED;
        }
        return -1;
    }
    
    int getCar(char token) {
        switch(token) {
            case 'E':
                return CAR_ENGINE;
            case 'C':
                return CAR_COAL;
            case 'L':
                return CAR_LOG;
            case 'F':
                return CAR_FUEL;
            case 'B':
                return CAR_BOOST;
            case '+':
                return CAR_HEALTH;
            case 'A':
                return CAR_ANCHOR;
            case 'b':
                return CAR_ANTIBOOST;
            case '=':
                return CAR_ANTIHEALTH;
        }
        return -1;
    }
    
    void loadLevel(int level) {
        ArrayList<Integer> data = new ArrayList();
        ArrayList<TrainCar> cars = new ArrayList();
        String content = "";
        try {
            InputStream in = GameScene.class.getClass().getResourceAsStream("/resrc/level/level" + level + ".lvl");
            BufferedReader input = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = input.readLine()) != null) {
                content += line;
            }
        }
        catch(IOException e) {
            System.err.println("Could not load level!");
        }
        
        int i;
        
        for(i = 0; i < content.length(); ++i) {
            if(getLevel(content.charAt(i)) != -1) {
                data.add(getLevel(content.charAt(i)));
            }
            else if(content.charAt(i) >= '0' && content.charAt(i) <= '9') {
                int times = 0;
                while(i < content.length()) {
                    if(!(content.charAt(i) >= '0' && content.charAt(i) <= '9')) {
                        break;
                    }
                    times *= 10;
                    times += content.charAt(i) - '0';
                    ++i;
                }
                int token = getLevel(content.charAt(i));
                if(token != -1) {
                    for(int j = 0; j < times; ++j) {
                        data.add(token);
                    }
                }
            }
            else if(content.charAt(i) == ';') { break; }
        }
        for(; i < content.length(); ++i) {
            int car = getCar(content.charAt(i));
            if(car != -1) {
                cars.add(new TrainCar(trainCars[car], trainCarOutlines[car], car));
            }
        }
        
        loadMap(data.stream().mapToInt(a -> a).toArray(), level > 2);
        
        train = cars.toArray(new TrainCar[0]);
        
        trainPosition = 6;
        doList(-1);
        
        scroll = 30;
        
        tutorial = Tutorial.nullTutorial;
        
        builtTrain.clear();
        loadedTrain.clear();
        
        switch(level) {
            case 0:
                tutorial = new Tutorial(0, Action.ADD_ENGINE, Action.MODE_SWITCH, Action.MODE_SWITCH, Action.ADD_COALCAR, Action.MODE_SWITCH);
                break;
            case 1:
                Image frame1 = Image.load("/resrc/tut/1_0.png");
                Image blank = Image.load(null);
                Image frame2 = Image.load("/resrc/tut/1_1.png");
                tutorial = new Tutorial(new Image[] { frame1, frame1, frame1, blank, frame2 }, Action.ADD_ENGINE, Action.ADD_COALCAR, Action.MODE_SWITCH, Action.MODE_SWITCH, Action.ADD_COALCAR);
                break;
            case 2:
                tutorial = new Tutorial(2, Action.SCROLL_RIGHT, null);
                break;
        }
        
        lastLoaded = level;
    }
    
    void loadMap(int[] data, boolean useDecor) {
        map = data;
        heightmap = new int[data.length];
        int height = 0;
        for(int i = 0; i < heightmap.length; ++i) {
            if(tileHeights[map[i]] > 0) {
                height -= tileHeights[map[i]];
            }
            heightmap[i] = height;
            if(tileHeights[map[i]] < 0) {
                height -= tileHeights[map[i]];
            }
            
            //canvas.fillRect(pos, -45, 12, 45 - height, LAND_COLOR);
            //canvas.fillRect(pos, height + 15, 12, 45, CLIFF_COLOR);
            //if(tileHeights[map[i]] > 0) {
            //    height -= tileHeights[map[j]];
            //}
            //canvas.draw(tiles[map[j]], pos, height);
            //if(tileHeights[map[j]] < 0) {
            //    height -= tileHeights[map[j]];
            //}
            //pos += 12;
        }
        if(useDecor) {
            Random random = new Random(1337);
            ArrayList<Decor> decorList = new ArrayList();
            for(int i = 0; i < map.length * 10; ++i) {
                int decor = random.nextInt(decorImages.length);
                Point loc = new Point(random.nextInt(map.length * 12 + 120) - 60, random.nextInt(91) - 45);
                int tile = (int)(loc.x / 12);

                boolean valid = false;

                if(tile < 0) {
                    if(loc.y + decorImages[decor].image.getHeight() < 0) {
                        valid = true;
                    }
                }
                else if (tile >= heightmap.length) {
                    if(loc.y + decorImages[decor].image.getHeight() < heightmap[heightmap.length - 1]) {
                        valid = true;
                    }
                }
                else {
                    if(loc.y + decorImages[decor].image.getHeight() < heightmap[tile]) {
                        valid = true;
                    }
                }

                if(valid) {
                    decorList.add(new Decor(loc, decorImages[decor]));
                }
            }
            decorList.sort(new Comparator<Decor> () {
                @Override
                public int compare(Decor o1, Decor o2) {
                    return o1.position.x > o2.position.x ? 1 : 
                            o1.position.x < o2.position.x ? -1 : 0;
                }
            });
            ArrayList<Decor> bads = new ArrayList();
            for(int i = 0; i < decorList.size() - 1; ++i) {
                float dx = decorList.get(i + 1).position.x - decorList.get(i).position.x;
                float dy = decorList.get(i + 1).position.y - decorList.get(i).position.y;
                if(dx * dx + dy * dy < 15 * 15) {
                    bads.add(decorList.get(i));
                }
            }
            for(Decor d : bads) {
                decorList.remove(d);
            }
            decor = decorList.toArray(new Decor[0]);
        }
        else {
            decor = new Decor[0];
        }
    }
    
    int getHeight(int index) {
        if(index < 0) { return 0; }
        if(index >= heightmap.length) { return heightmap[heightmap.length - 1]; }
        return heightmap[index];
    }
    
    void drawEditor(Canvas canvas) {
        if(!train[0].onTrack) {
            if(active == train[0]) {
                if(Math.abs(active.bounds.x - scroll + 60 - trainPosition) < 18 && Math.abs(active.bounds.y) < 18) {
                    if(trainPosition < 12) {
                        canvas.draw(lightTrack, scroll - 60, 0);
                    }
                    else {
                        canvas.draw(lightStraight, ((int)trainPosition / 12) * 12 + scroll - 60, 0);
                    }
                }
            }
        }

        for(int i = 0; i < train.length; ++i) {
            float x = train[i].bounds.x, y = train[i].bounds.y;
            double angle = 0;
            if(train[i].onTrack) {
                x = trainPosition + scroll - 60 - train[i].backwardsOffset;
                int index = (int)((trainPosition - train[i].backwardsOffset) / 12);
                int height = 0;
                //if(index >= 0 && index < heightmap.length) {
                    //height = heightmap[index + 1];
                //}
                angle = Math.toDegrees(Math.atan2(getHeight(index + 1) - getHeight(index), 12));
                double dist = ((trainPosition - train[i].backwardsOffset) % 12) / 12;
                height = (int)(getHeight(index) * (1 - dist) + getHeight(index + 1) * dist);

                y = 12 + height - train[i].bounds.height;
                //System.out.println("Angle [" + i + "]: " + angle);
                //canvas.draw(train[i].appearance, x, 12 + height - train[i].bounds.height, angle);
            }
            if(playing) {
                if(train[i].onTrack) {
                    canvas.draw(train[i].appearance, x, y, angle);
                }
            }
            else {
                boolean valid = true;
                if(tutorial.getAction() != null) {
                    if(null != tutorial.getAction()) switch (tutorial.getAction()) {
                        case ADD_ENGINE:
                            valid = train[i].type == CAR_ENGINE;
                            break;
                        case ADD_COALCAR:
                            valid = train[i].type == CAR_COAL;
                            break;
                        default:
                            valid = false;
                            break;
                    }
                }
                if(train[i].bounds.contains(Mouse.getPosition()) && (active == null || train[i] == active) && valid) {
                    canvas.draw(train[i].outline, x, y, angle);
                }
                else {
                    canvas.draw(train[i].appearance, x, y, angle);
                }
            }
        }

        if(train[0].onTrack && active != null) {
            if(Math.abs(active.bounds.y) < 18) {
                int activePos = (int)(active.bounds.x - scroll + 60);
                int accumulator = (int)(trainPosition);
                int min = Math.abs(accumulator - activePos);
                int minPos = 0;
                for(int i = 0; i < builtTrain.size(); ++i) {
                    accumulator = (int)(trainPosition-builtTrain.get(i).backwardsOffset);
                    if(Math.abs(accumulator - activePos) < min) {
                        min = Math.abs(accumulator - activePos);
                        minPos = i;
                    }
                }
                canvas.fillRect(builtTrain.get(minPos).bounds.x, builtTrain.get(minPos).bounds.y + builtTrain.get(minPos).bounds.height - 6, 1, 3, Color.WHITE);
            }
        }

        if(active != null) {
            canvas.draw(active.outline, active.bounds.x, active.bounds.y);
        }
    }
    
    @Override
    public void draw(Canvas canvas) {
        canvas.fill(Color.WHITE);
        
        int offset = (int)(-scroll / 12);
        float pos = -60 + scroll%12;
        int finalheight = 0;
        
        if(scroll > 0) {
            pos += 12 * -offset;
            int width = (int)Math.ceil(12 * -offset + scroll % 12);
            canvas.fillRect(-60, -45, width, 46, LAND_COLOR); //1 more than in general rendering to compensate for image provided dots
            canvas.fillRect(-60, 14, width, 31, CLIFF_COLOR);
            canvas.fillRect(-60, 1, width, 6, LAND_SIDE_COLOR);
            canvas.fillRect(-60, 7, width, 7, CLIFF_TOP_COLOR);
        }
        
        for(int i = offset; i <= offset + 10; ++i) {
            int j = i;
            if(j >= 0 && j < map.length) {
                canvas.fillRect(pos, -45, 12, 45 + heightmap[j], LAND_COLOR);
                canvas.fillRect(pos, heightmap[j] + 15, 12, 45 - (heightmap[j] + 15), CLIFF_COLOR);
                //if(tileHeights[map[j]] > 0) {
                    //height -= tileHeights[map[j]];
                //}
                canvas.draw(tiles[map[j]], pos, heightmap[j]);
                //if(tileHeights[map[j]] < 0) {
                    //height -= tileHeights[map[j]];
                //}
                finalheight = heightmap[j];
                pos += 12;
                
            }
        }
        if(pos < 60) {
            int width = (int)Math.ceil(60 - pos);
            canvas.fillRect(pos, -45, width, 46 + finalheight, LAND_COLOR);
            canvas.fillRect(pos, finalheight + 14, width, 45 - (finalheight + 14), CLIFF_COLOR);
            canvas.fillRect(pos, finalheight + 1, width, 7, LAND_SIDE_COLOR);
            canvas.fillRect(pos, finalheight + 7, width, 7, CLIFF_TOP_COLOR);
        }
        for(int i = 0; i < decor.length; ++i) {
            canvas.draw(decor[i].appearance, decor[i].position.x + scroll - 60, decor[i].position.y);
        }
        
        canvas.draw(playing ? 
                buttonHighlighted ? stopButtonPressed : stopButton : 
                buttonHighlighted ? playButtonPressed : playButton, 
                switchStateButton.x, switchStateButton.y);
        
        if(!playing) {
            synchronized(builtTrain) {
                drawEditor(canvas);
            }
        }
        else {
            synchronized(loadedTrain) {
                for(TrainCar t : loadedTrain) {
                    float x = trainPosition + scroll - 60 - t.backwardsOffset;
                    int index = (int)((trainPosition - t.backwardsOffset) / 12);
                    double angle = Math.toDegrees(Math.atan2(getHeight(index + 1) - getHeight(index), 12));
                    double dist = ((trainPosition - t.backwardsOffset) % 12) / 12;
                    int height = (int)(getHeight(index) * (1 - dist) + getHeight(index + 1) * dist);

                    canvas.draw(t.appearance, x, 12 + height - t.bounds.height, angle);
                    
                    t.bounds.x = trainPosition - t.backwardsOffset;
                    t.bounds.y = 12 + height - t.bounds.height;
                }
                
                for(Explosion e : explosions) {
                    if(e.draw()) {
                        canvas.draw(e.getFrame(), e.position.x + scroll - 60, e.position.y);
                    }
                }
            }
        }
        //for(int i = -60; i < 60; i += 12) {
       //     canvas.draw(tiles[i > 40 ? 1 : 0], i,  - tileHeights[i > 40 ? 1 : 0]);
        ///    canvas.fillRect(i, -45, 12, 45 - tileHeights[i > 40 ? 1 : 0], LAND_COLOR);
        //    canvas.fillRect(i, 15, 12, 45, CLIFF_COLOR);
        //}
        if(tutorial.hasAction()) {
            canvas.draw(tutorial.getImage(), -60, -45);
        }
    }
    
    void doList(int exclude) {
        int y = -44;
        for(int i = 0; i < train.length; ++i) {
            if(!train[i].onTrack && i != exclude) {
                train[i].bounds.x = 59 - train[i].bounds.width;
                train[i].bounds.y = y;
                y += train[i].bounds.height + 1;
            }
        }
    }
    
    void doTrain() {
        trainPosition = 6 + doTrainInPlace(builtTrain);
    }

    int doTrainInPlace(ArrayList<TrainCar> list) {
        /*trainPosition = 6;
        int offset = 0;
        for(int i = 1; i < train.length; ++i) {
            if(train[i].onTrack) {
                train[i].backwardsOffset = (int)(offset + train[i].bounds.width);
                trainPosition += train[i].bounds.width;
                offset += train[i].bounds.width;
            }
        }*/
        trainAcc = 0;
        maxSpeed = 0;
        
        int offset = 0;
        for(int i = 0; i < list.size(); ++i) {
            TrainCar t = list.get(i);
            if(i > 0) {
                t.backwardsOffset = (int)(offset + t.bounds.width);
                //trainPosition += t.bounds.width;
                offset += t.bounds.width;
            }
            
            t.hasHealth = false;
            if(i > 0) {
                if(list.get(i - 1).type == CAR_HEALTH) {
                    t.hasHealth = true;
                }
            }
            if(i < list.size() - 1) {
                if(list.get(i + 1).type == CAR_HEALTH) {
                    t.hasHealth = true;
                }
            }
            
            if(t.type == CAR_COAL) {
                double acc = t.burnt ? 0.05 : 0.1;
                double spd = t.burnt ? 2 : 2.5;
                trainAcc += acc;
                maxSpeed += spd;
                if(i < list.size() - 1) {
                    if(list.get(i + 1).type == CAR_BOOST){
                        trainAcc += acc;
                        maxSpeed += spd;
                    }
                    else if(list.get(i + 1).type == CAR_ANTIBOOST){
                        trainAcc -= acc / 2;
                        maxSpeed -= spd / 2;
                    }
                }
                if(i > 0) {
                    if(list.get(i - 1).type == CAR_BOOST){
                        trainAcc += acc;
                        maxSpeed += spd;
                    }
                    else if(list.get(i - 1).type == CAR_ANTIBOOST){
                        trainAcc -= acc / 2;
                        maxSpeed -= spd / 2;
                    }
                }
            }
            
            if(t.type == CAR_FUEL) {
                float amount = ticksSinceStart / 1000f;
                
                if(amount < 0.1f) { amount = 0.1f - amount; }
                else { amount = 0; }
                if(t.burnt) { amount *= 1.5; }
                trainAcc += amount;
                maxSpeed += amount * 30;
                if(i < list.size() - 1) {
                    if(list.get(i + 1).type == CAR_BOOST){
                        trainAcc += amount;
                        maxSpeed += amount * 30;
                    }
                    else if(list.get(i + 1).type == CAR_ANTIBOOST){
                        trainAcc -= amount / 2;
                        maxSpeed -= amount * 15;
                    }
                }
                if(i > 0) {
                    if(list.get(i - 1).type == CAR_BOOST){
                        trainAcc += amount;
                        maxSpeed += amount * 30;
                    }
                    else if(list.get(i - 1).type == CAR_ANTIBOOST){
                        trainAcc -= amount / 2;
                        maxSpeed -= amount * 15;
                    }
                }
            }
            
            if(t.type == CAR_ANCHOR) {
                trainAcc -= 0.05;
                maxSpeed -= 0.1;
            }
            
            if(t.type == CAR_LOG) {
                trainAcc -= 0.01;
                maxSpeed -= 0.1;
            }
            
            if(t.boost) {
                trainAcc += 0.01;
                maxSpeed += 0.5;
            }
        }
        
        if(trainAcc < 0) { trainAcc = 0; }
        if(maxSpeed < 0) { maxSpeed = 0; }
        
        if(!list.contains(train[0])) {
            trainAcc = 0;
            maxSpeed = 0;
        }
        
        return offset;
    }
    
    /*void doTrainInsert(int position) {
        trainPosition = 6;
        int offset = 0;
        for(int i = 1; i < train.length; ++i) {
            if(i == position) {
                active.backwardsOffset = (int)(offset + active.bounds.width);
                trainPosition += active.bounds.width;
                offset += train[i].bounds.width;
            }
            else if(train[i].onTrack && train[i] != active) {
                train[i].backwardsOffset = (int)(offset + train[i].bounds.width);
                trainPosition += train[i].bounds.width;
                offset += train[i].bounds.width;
            }
        }
    }*/
    
    void switchMode() {
        playing = !playing;
        doTrain();
        scroll = -trainPosition + 60;
        trainSpeed = 0;
        buttonCooldown = 10;
        
        if(playing) {
            synchronized(loadedTrain) {
                loadedTrain.clear();
                loadedTrain.addAll(builtTrain);
            }
        }
        
        ticksSinceStart = 0;
        
        if(!playing) {
            synchronized(builtTrain) {
                for(TrainCar t : builtTrain) {
                    if(t.type == CAR_DRAINED) {
                        t.type = CAR_HEALTH;
                        t.appearance = trainCars[CAR_HEALTH];
                    }
                    t.burnt = false;
                    t.boost = false;
                }
            }
        }
        
        sawTrack.reset();
        spikeTrack.reset();
        alive = true;
        
        currentExplosion = 0;
    }
    
    void save() {
        PrintWriter writer;
        File testSave = new File("ld36-trainstack-save.txt");
        if(testSave.exists()) { //Check to make sure file is valid before writing to it
            FileReader inputFile;
            try {
                inputFile = new FileReader("ld36-trainstack-save.txt");
                BufferedReader input = new BufferedReader(inputFile);

                if(!"LD36:TRAINSTACK:SAVE".equals(input.readLine())) {
                    return; //This is a bad save file; do NOT overwrite it
                }
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
            }
        }
        try {
            writer = new PrintWriter("ld36-trainstack-save.txt", "UTF-8");
            writer.println("LD36:TRAINSTACK:SAVE");
            writer.println(progress);
            writer.close();
        } 
        catch (FileNotFoundException | UnsupportedEncodingException ex) {
        }
    }
    
    void loopPlaying() {
         if(Keys.pressed(KeyEvent.VK_LEFT)) {
            scroll += 5;
        }
        else if(Keys.pressed(KeyEvent.VK_RIGHT)) {
            scroll -= 5;
        }
         
        scroll -= trainSpeed;
        
        if(scroll > 60) { scroll = 60; }
        if(scroll < map.length * -12 + 60) { scroll = map.length * -12 + 60; }
        int engine = (int)(trainPosition / 12);
        if(map[engine + 1] != TILE_FINISHTRACK) {
        //if(map[(int)((trainPosition + train[0].appearance.image.getWidth()) / 12) - 1] != TILE_FINISHTRACK) {
            if(trainSpeed < maxSpeed) {
                trainSpeed += trainAcc;
            }
            
            if(!loadedTrain.isEmpty()) {
                TrainCar last = loadedTrain.get(loadedTrain.size() - 1);
                int lastTile = map[(int)((trainPosition - last.backwardsOffset) / 12)];
                if(lastTile == TILE_BEGINTRACK && (trainSpeed < 0 || trainAcc < 0)) {
                    trainSpeed = 0;
                    trainAcc = 0;
                }
            }

            switch(map[engine]) {
                case TILE_UPTRACK:
                    trainSpeed -= 0.1;
                    break;
                case TILE_CONTINUEUPTRACK:
                    trainSpeed -= 0.2;
                    break;
                case TILE_DOWNTRACK:
                    trainSpeed += 0.05;
                    trainAcc += 0.1;
                    break;
                case TILE_CONTINUEDOWNTRACK:
                    trainSpeed += 0.2;
                    trainAcc += 0.09;
                    break;
                default:
                    if(trainSpeed > maxSpeed) {
                        trainSpeed -= 0.2;
                        if(Math.abs(trainSpeed - maxSpeed) < 0.2) {
                            trainSpeed = maxSpeed;
                        }
                    }
                    if(trainAcc == 0) {
                        if(trainSpeed > 0) {
                            trainSpeed -= 0.2;
                            if(Math.abs(trainSpeed) < 0.2) {
                                trainSpeed = 0;
                            }
                        }
                    }
            }  
            
            //0, safe; 1, dead
            int[] status = new int[loadedTrain.size()];
            int current = 0;
            
            for (TrainCar t : loadedTrain) {
                int tile = map[(int)((trainPosition - t.backwardsOffset) / 12) + 1];
                t.boost = false;
                switch(tile) {
                    case TILE_SAWTRACK:
                        if(sawTrack.active() && !t.hasHealth && t.type != CAR_HEALTH && t.type != CAR_DRAINED && t.type != CAR_ANTIHEALTH) {
                            //it.remove();
                            status[current] = 1;

                        }
                        if(t.type == CAR_HEALTH) {
                            t.type = CAR_DRAINED;
                            t.appearance = trainCars[CAR_DRAINED];
                        }
                        if(t.type == CAR_ANTIHEALTH) {
                            status[current] = 1;
                            if(current > 0) {
                                status[current - 1] = 1;
                            }
                            if(current < status.length - 1) {
                                status[current + 1] = 1;
                            }
                        }
                        break;
                    case TILE_SPIKE:
                        //code duplication makes this annyoing
                        if(spikeTrack.active() && !t.hasHealth && t.type != CAR_HEALTH && t.type != CAR_DRAINED && t.type != CAR_ANTIHEALTH) {
                            //it.remove();
                            status[current] = 1;
                        }
                        if(t.type == CAR_HEALTH) {
                            t.type = CAR_DRAINED;
                            t.appearance = trainCars[CAR_DRAINED];
                        }
                        if(t.type == CAR_ANTIHEALTH) {
                            status[current] = 1;
                            if(current > 0) {
                                status[current - 1] = 1;
                            }
                            if(current < status.length - 1) {
                                status[current + 1] = 1;
                            }
                        }
                        break;
                    case TILE_WEAKTRACK:
                        if(trainAcc > 0.15) {
                            trainAcc = 0;
                            trainSpeed = 0;
                            maxSpeed = 0;
                        }
                        break;
                    case TILE_FIRE:
                        t.burnt = true;
                        break;
                    case TILE_SPEED:
                        t.boost = true;
                        break;
                }
                ++current;
            }
             
             
             
            current = 0;
            for(Iterator<TrainCar> it = loadedTrain.iterator(); it.hasNext();) {
                TrainCar t = it.next();
                if(status[current] == 1) {
                    it.remove();
                    if(t == train[0]) {
                        trainAcc = 0;
                        trainSpeed = 0;
                        maxSpeed = 0;
                        alive = false;
                    }

                    explosions[currentExplosion].setup(new Point(t.bounds.x, t.bounds.y));
                    ++currentExplosion;
                    explode.play();
                }
                ++current;
            }
            doTrainInPlace(loadedTrain);

            trainPosition += trainSpeed;
        }
        else if(alive) {
            //winSound.play();
            //playing = false;
            if(progress < lastLoaded) {
                switch(lastLoaded) {
                    case 1:
                        TrainStack.unlock("logger",trainCars[CAR_LOG]);
                        break;
                    case 2:
                        TrainStack.unlock("health",trainCars[CAR_HEALTH]);
                        break;
                    case 3:
                        TrainStack.unlock("boost",trainCars[CAR_BOOST]);
                        break;
                    case 4:
                        TrainStack.unlock("fuel",trainCars[CAR_FUEL]);
                        break;
                    case 5:
                        TrainStack.unlockTrack("broken");
                        break;
                    case 6:
                        TrainStack.unlock("anchor",trainCars[CAR_ANCHOR]);
                        break;
                    case 7:
                        TrainStack.unlockTrack("fire");
                        break;
                    case 8:
                        TrainStack.unlockTrack("spike");
                        break;
                    case 9:
                        TrainStack.unlock("antiboost",trainCars[CAR_ANTIBOOST]);
                        break;
                    case 10:
                        TrainStack.unlock("antihealth",trainCars[CAR_ANTIHEALTH]);
                        break;
                    case 11:
                        TrainStack.unlockTrack("speed");
                        break;
                    default:
                        TrainStack.nextLevel();
                }
                progress = lastLoaded;
                save();
            }
            else {
                TrainStack.nextLevel();
            }
        }
        if(ticksSinceStart % 2 == 0) {
            for(Explosion e : explosions) {
                e.step();
            }
        }
        ++ticksSinceStart;
    }
    
    void loopEditor() {
        if(Keys.pressed(KeyEvent.VK_LEFT)) {
            if(tutorial.getAction() == null) {
                scroll += 5;
            }
        }
        else if(Keys.pressed(KeyEvent.VK_RIGHT)) {
            if(tutorial.getAction() == null || tutorial.getAction() == Action.SCROLL_RIGHT) {
                scroll -= 5;
                if(tutorial.getAction() == Action.SCROLL_RIGHT) { tutorial.next(); }
            }
        }
        if(scroll > 60) { scroll = 60; }
        if(scroll < map.length * -12 + 60) { scroll = map.length * -12 + 60; }

        if(active == null) {
            if(Mouse.isLMBDown()) {
                for(int i = 0; i < train.length; ++i) {
                    if(train[i].bounds.contains(Mouse.getPosition())) {
                        boolean valid = true;
                        if(tutorial.getAction() != null) {
                            if(null != tutorial.getAction()) switch (tutorial.getAction()) {
                                case ADD_ENGINE:
                                    valid = train[i].type == CAR_ENGINE;
                                    break;
                                case ADD_COALCAR:
                                    valid = train[i].type == CAR_COAL;
                                    break;
                                default:
                                    valid = false;
                                    break;
                            }
                        }
                        if(valid) {
                            trainUp.play();
                            train[i].onTrack = false; //Disconnect from track
                            builtTrain.remove(train[i]);
                            active = train[i];
                            dragPoint = new Point(train[i].bounds.x - Mouse.getMouseX(), train[i].bounds.y - Mouse.getMouseY());
                            doList(i);
                            break;
                        }
                    }
                }
            }
        }
        else {
            if(!Mouse.isLMBDown()) {
                if(!train[0].onTrack) {
                    if(active == train[0]) {
                        if(Math.abs(active.bounds.x - scroll + 60 - trainPosition) < 18 && Math.abs(active.bounds.y) < 18 &&
                                (tutorial.getAction() == null || tutorial.getAction() == Action.ADD_ENGINE)) {
                            trainDown.play();
                            train[0].onTrack = true;
                            builtTrain.add(0, train[0]);
                            if(tutorial.getAction() == Action.ADD_ENGINE) {
                                tutorial.next();
                            }
                        }
                        else {
                            trainUp.play();
                            for(int i = 0; i < train.length; ++i) {
                                train[i].onTrack = false; //Disconnect all trains from track
                                builtTrain.clear();
                            }
                            trainPosition = 6;
                        }
                    }
                }
                else {
                    boolean acceptable = true;
                    if(tutorial.getAction() == Action.ADD_COALCAR) {
                        acceptable = active.type == CAR_COAL;
                    }
                    else if(tutorial.getAction() != null) {
                        acceptable = false;
                    }
                    if(Math.abs(active.bounds.y) < 18 && acceptable) {
                        int activePos = (int)(active.bounds.x - scroll + 60);
                        int accumulator = (int)(trainPosition);
                        int min = Math.abs(accumulator - activePos);
                        int minPos = 0;
                        for(int i = 0; i < builtTrain.size(); ++i) {
                            accumulator = (int)(trainPosition-builtTrain.get(i).backwardsOffset);
                            if(Math.abs(accumulator - activePos) < min) {
                                min = Math.abs(accumulator - activePos);
                                minPos = i;
                            }
                        }
                        trainDown.play();
                        active.onTrack = true;
                                //doTrainInsert(i);
                        builtTrain.add(minPos + 1, active);
                        if(tutorial.getAction() == Action.ADD_COALCAR && active.type == CAR_COAL) {
                            tutorial.next();
                        }
                    }
                }
                doList(-1);
                doTrain();

                active = null;
            }
            else {
                active.bounds.x = Mouse.getMouseX() + dragPoint.x;
                active.bounds.y = Mouse.getMouseY() + dragPoint.y;
            }
        }

        for(TrainCar t : train) {
            if(t.onTrack) {
                t.bounds.x = trainPosition + scroll - 60 - t.backwardsOffset;
                t.bounds.y = 12 - t.bounds.height;
            }
        }
    }
    
    @Override
    public void loop() {
        if(playing) {
            synchronized(loadedTrain) {
                loopPlaying();
            }
        }
        else {
            synchronized(builtTrain) {
                loopEditor();
            }
        }
        
        if(tutorial.getAction() == null || tutorial.getAction() == Action.MODE_SWITCH) {
            if(switchStateButton.contains(Mouse.getPosition())) {
                buttonHighlighted = true;
                if(Mouse.isLMBDown() && buttonCooldown == 0) {
                    switchMode();
                    if(tutorial.getAction() == Action.MODE_SWITCH) {
                        tutorial.next();
                    }
                }
            }
            else {
                buttonHighlighted = false;
            }
        }
        else {
            buttonHighlighted = false;
        }
        if(buttonCooldown > 0) { --buttonCooldown; }
        
        sawTrack.step();
        tiles[TILE_SAWTRACK] = sawTrack.getFrame();
        spikeTrack.step();
        tiles[TILE_SPIKE] = spikeTrack.getFrame();
        
        fire.step();
        tiles[TILE_FIRE] = fire.getCurrentFrame();
        if(Game.getTick() % 2 == 0) {
            speed.step();
            tiles[TILE_SPEED] = speed.getCurrentFrame();
        }
        
        if(Keys.pressed(KeyEvent.VK_ESCAPE)) {
            if(playing) {
                switchMode();
                if(tutorial.getAction() == Action.MODE_SWITCH) {
                    tutorial.next();
                }
            }
            TrainStack.pause();
        }
    }
    
}
