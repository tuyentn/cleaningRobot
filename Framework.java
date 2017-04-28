package rpg;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Framework that controls the game (Game.java) that created it, update it and draw it on the screen.
 * 
 * @author RPG
 */

public class Framework extends Canvas {
    
    /**
     * Width of the frame.
     */
    public static int frameWidth;
    /**
     * Height of the frame.
     */
    public static int frameHeight;

    /**
     * Time of one second in nanoseconds.
     * 1 second = 1 000 000 000 nanoseconds
     */
    public static final long secInNanosec = 1000000000L;
    
    /**
     * Time of one millisecond in nanoseconds.
     * 1 millisecond = 1 000 000 nanoseconds
     */
    public static final long milisecInNanosec = 1000000L;
    
    /**
     * FPS - Frames per second
     * How many times per second the game should update?
     */
    private final int GAME_FPS = 45;
    /**
     * Pause between updates. It is in nanoseconds.
     */
    private final long GAME_UPDATE_PERIOD = secInNanosec / GAME_FPS;
    
    /**
     * Các trạng thái có thể của game
     */
    public static enum GameState{STARTING, VISUALIZING, GAME_CONTENT_LOADING ,MAIN_MENU,LOADGAME, OPTIONS,PAUSE, PLAYING, GAMEOVER, DESTROYED}
    /**
     * Trạng thái hiện tại của game
     */
    public static GameState gameState,preState;
    
    /**
     * Elapsed game time in nanoseconds.
     */
    private long gameTime;
    // It is used for calculating elapsed time.
    private long lastTime;
    
    private BufferedImage bg_introduce;
    
    private BufferedImage bg_menu;
    private BufferedImage btn_start;
    private BufferedImage btn_loadgame;
    private BufferedImage btn_options;
    private BufferedImage btn_exit;
    private BufferedImage task,pause,end;
    // The actual game
    private Game game;
    
    private File f;
    private int mode = 0;
    
    public Framework ()
    {
        super();
        
        gameState = GameState.VISUALIZING;
        
        //We start game in new thread.
        Thread gameThread = new Thread() {
            @Override
            public void run(){
                GameLoop();
            }
        };
        gameThread.start();
    }
    
    
   /**
     * Set variables and objects.
     * Khởi tạo các biến của game Menu
     */
    private void Initialize()
    {
        
    }
    
    
    
    private void preStarting(){
        try{
            URL bg_introImgUrl = this.getClass().getResource("/rpg/resources/images/bgintro.jpg/");
            bg_introduce = ImageIO.read(bg_introImgUrl);
        }catch (IOException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Load files - images, sounds, ...
     * Load ảnh của menu, nhạc menu và nhạc in game
     */
    private void LoadContent()
    {
        try
           {
            
            
            URL menuImgUrl = this.getClass().getResource("/rpg/resources/images/menu/bg_menu.jpg");
            bg_menu = ImageIO.read(menuImgUrl);
            
            URL startImgUrl = this.getClass().getResource("/rpg/resources/images/menu/btn_start.png");
            btn_start = ImageIO.read(startImgUrl);
            
            URL loadgameImgUrl = this.getClass().getResource("/rpg/resources/images/menu/btn_loadgame.png");
            btn_loadgame = ImageIO.read(loadgameImgUrl);
            
            URL optionsImgUrl = this.getClass().getResource("/rpg/resources/images/menu/btn_options.png");
            btn_options = ImageIO.read(optionsImgUrl);
            
            URL exitImgUrl = this.getClass().getResource("/rpg/resources/images/menu/btn_exit.png");
            btn_exit = ImageIO.read(exitImgUrl);
            
            
            
            URL pauseImgUrl = this.getClass().getResource("/rpg/resources/images/pause.png");
            pause = ImageIO.read(pauseImgUrl);
            
            URL taskImgUrl = this.getClass().getResource("/rpg/resources/images/task.png");
            task = ImageIO.read(taskImgUrl);
            
            URL endUrl = this.getClass().getResource("/rpg/resources/images/end.png");
            end = ImageIO.read(endUrl);
            
        }
        catch (IOException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * In specific intervals of time (GAME_UPDATE_PERIOD) the game/logic is updated and then the game is drawn on the screen.
     */
    private void GameLoop()
    {
        // This two variables are used in VISUALIZING state of the game. We used them to wait some time so that we get correct frame/window resolution.
        long visualizingTime = 0, lastVisualizingTime = System.nanoTime();
        
        // This variables are used for calculating the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
        long beginTime, timeTaken, timeLeft;
        
        while(true)
        {
            beginTime = System.nanoTime();
            
            switch (gameState)
            {
                case PLAYING:
                    gameTime += System.nanoTime() - lastTime;
                    
                    game.UpdateGame(gameTime, mousePosition());
                    
                    lastTime = System.nanoTime();
                break;
                case GAMEOVER:
                    //...
                break;
                case PAUSE:
                    pause();
                break;
                case MAIN_MENU:
                    gameMenu();
                break;
                case LOADGAME:
                    loadGame();
                break;
                case OPTIONS:
                    //...
                break;
                case GAME_CONTENT_LOADING:
                    //...
                break;
                case STARTING:
                    // Sets variables and objects.
                    Initialize();
                    // Load files - images, sounds, ...
                    LoadContent();

                    // When all things that are called above finished, we change game status to main menu.
                    gameState = GameState.MAIN_MENU;
                break;
                case VISUALIZING:
                    
                    if(this.getWidth() > 1 && visualizingTime > secInNanosec)
                    {
                        frameWidth = this.getWidth();
                        frameHeight = this.getHeight();
                        //preStarting();
                        // Khởi tạo xong frame thì khởi tạo menu
                        gameState = GameState.STARTING;
                    }
                    else
                    {
                        visualizingTime += System.nanoTime() - lastVisualizingTime;
                        lastVisualizingTime = System.nanoTime();
                    }
                break;
            }
            
            // Cập nhật lại màn hình .
            repaint();
            
            // Tính toán thời gian giữa mỗi lần update .
            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / milisecInNanosec; // In milliseconds
            // If the time is less than 10 milliseconds, then we will put thread to sleep for 10 millisecond so that some other thread can do some work.
            if (timeLeft < 10) 
                timeLeft = 10; //set a minimum
            try {
                 // Delay luồng .
                 Thread.sleep(timeLeft);
            } catch (InterruptedException ex) { }
        }
    }
    
    /**
     * Draw the game to the screen. It is called through repaint() method in GameLoop() method.
     */
    @Override
    public void Draw(Graphics2D g2d)
    {

        switch (gameState)
        {
            case PLAYING:
                game.Draw(g2d, mousePosition());
                if (Canvas.keyboardKeyState(KeyEvent.VK_Q)){
                    g2d.drawImage(task, game.xPlayer()-(task.getWidth())/2 , game.yPlayer()-(task.getHeight())/2, null);
                }
            break;
            case GAMEOVER:
                game.Draw(g2d, mousePosition());
                g2d.drawImage(end, game.xPlayer()-350,game.yPlayer() -100, null);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new  Font("SansSerif", Font.PLAIN, 50));
                g2d.drawString("Đã lau xong !! Bao phủ: 100%", game.xPlayer() -300 , game.yPlayer() -20);
                g2d.setColor(Color.GREEN);
                g2d.drawString("Độ chồng lấn: "+game.robot.chonglan + " ô / "+1200*(2- mode) +" ô", game.xPlayer() -300, game.yPlayer() +100);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Bấm ESC quay lại menu", game.xPlayer() -300, game.yPlayer() +240);
            break;
            case PAUSE:
                game.Draw(g2d, mousePosition());
                
                g2d.drawImage(pause,game.xPlayer()-(pause.getWidth())/2 + 115 , game.yPlayer()-(pause.getHeight())/2+57,null);
            break;
            case GAME_CONTENT_LOADING:
            case MAIN_MENU:
                g2d.drawImage(bg_menu, 0, 0,frameWidth,frameHeight,null);
                g2d.drawImage(btn_start, frameWidth/2-10, frameHeight/3, null);
                g2d.drawImage(btn_loadgame, frameWidth/2 -17, frameHeight/3 + 95, null);
                g2d.drawImage(btn_options, frameWidth/2 -13, frameHeight/3 + 190, null);
                g2d.drawImage(btn_exit, frameWidth/2 + 5, frameHeight/3 + 285, null);               
            break;
            case OPTIONS:
                //Sound UI
            break;
            
            case STARTING:
                g2d.drawImage(bg_introduce, 0, 0, null);
            break;
            
        }
    }
    
    
    /**
     * Starts new game.
     */
    private void newGame()
    {
        // We set gameTime to zero and lastTime to current time for later calculations.
        
        gameTime = 0;
        lastTime = System.nanoTime();
        mode = 0;
        game = new Game();
    }
    /*
    * Load Game State from save.txt
    */
    private void loadGame(){
        /* read file save.txt
        */
        gameTime = 0;
        lastTime = System.nanoTime();
        mode = 1;
        game = new Game(mode);
    }
    
    
    /**
     *  Restart game - reset game time and call RestartGame() method of game object so that reset some variables.
     */
    private void restartGame()
    {
        // We set gameTime to zero and lastTime to current time for later calculations.
        gameTime = 0;
        lastTime = System.nanoTime();
        
        game.RestartGame();
        
        // We change game status so that the game can start.
        gameState = GameState.PLAYING;
    }
    
    private void gameMenu(){
        if(Canvas.mouseButtonState(MouseEvent.BUTTON1)){
            
            if(new Rectangle(frameWidth/2 -10, frameHeight/3,btn_start.getWidth(),btn_start.getHeight()).contains(mousePosition()))
                newGame();
        
            if(new Rectangle(frameWidth/2 -17, frameHeight/3 + 95,btn_loadgame.getWidth(),btn_loadgame.getHeight()).contains(mousePosition()))
                gameState = GameState.LOADGAME;
            
            if(new Rectangle(frameWidth/2 -13, frameHeight/3 + 190,btn_options.getWidth(),btn_options.getHeight()).contains(mousePosition()))
                gameState = GameState.OPTIONS;
            
            if(new Rectangle(frameWidth/2 + 5, frameHeight/3 + 285,btn_exit.getWidth(),btn_exit.getHeight()).contains(mousePosition()))
                System.exit(0);
            
        }
    }
    
    /**
     * Returns the position of the mouse pointer in game frame/window.
     * If mouse position is null than this method return 0,0 coordinate.
     * 
     * @return Point of mouse coordinates.
     */
    private Point mousePosition()
    {
        try
        {
            Point mp = this.getMousePosition();
            
            if(mp != null)
                return this.getMousePosition();
            else
                return new Point(0, 0);
        }
        catch (Exception e)
        {
            return new Point(0, 0);
        }
    }
    private Point mousePositionFollowPlayer(){
        Point temp = mousePosition();
        temp.x += (game.xPlayer()-512);
        temp.y += (game.yPlayer()-384);
        return temp;
    }
    
    private void pause() {
        if (Canvas.mouseButtonState(MouseEvent.BUTTON1)) {
            if (new Rectangle(game.xPlayer()-(pause.getWidth())/2+220, game.yPlayer()-(pause.getHeight())/2+40, 180, 48).contains(mousePositionFollowPlayer())) {
                System.out.println("("+mousePositionFollowPlayer().x+", "+mousePositionFollowPlayer().y+")");
                gameState = GameState.PLAYING;    
            }

            if (new Rectangle(game.xPlayer()-(pause.getWidth())/2+220,  game.yPlayer()-(pause.getHeight())/2+130, 180, 48).contains(mousePositionFollowPlayer())) {
                savegame();
            }
            if (new Rectangle(game.xPlayer()-(pause.getWidth())/2+220, game.yPlayer()-(pause.getHeight())/2+227, 180, 48).contains(mousePositionFollowPlayer())) {
                gameState = GameState.OPTIONS;
            }
            if (new Rectangle(game.xPlayer()-(pause.getWidth())/2+220, game.yPlayer()-(pause.getHeight())/2+320, 180, 48).contains(mousePositionFollowPlayer())) {
//                Game.INGAME.stop();
                gameState = GameState.MAIN_MENU;
                try {
                    //Provides the necessary delay and also yields control so that other thread can do work.
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }
        }
        preState = GameState.PAUSE;
    }
    
    public void savegame(){
        
    }
    
    /**
     * This method is called when keyboard key is released.
     * 
     * @param e KeyEvent
     */
    @Override
    public void keyReleasedFramework(KeyEvent e)
    {
        switch (gameState)
        {
            case GAMEOVER:
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    gameState = GameState.MAIN_MENU;
                else if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER)
                    restartGame();
            break;
            case PLAYING:
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    gameState = GameState.PAUSE;
            break;
            
            case MAIN_MENU:
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    System.exit(0);
            break;
            
            case OPTIONS:
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    gameState = preState;
            break;
            case PAUSE:
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    gameState = GameState.PLAYING;
                break;
            
        }
    }
    
    /**
     * This method is called when mouse button is clicked.
     * 
     * @param e MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        
    }
}
