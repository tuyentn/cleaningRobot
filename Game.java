package rpg;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.awt.Rectangle;
import rpg.Player.Dimension2D;

/**
 * Actual game.
 *
 * @author www.gametutorial.net
 */
public class Game {

    // Map chính của game
    private Map map;
    //Nhân vật
    public Robot robot;
    public int mode = 0;
    public Entity.Direction[] directions = {Entity.Direction.UP, Entity.Direction.RIGHT, Entity.Direction.DOWN, Entity.Direction.LEFT};
    
    public Game() {
        Framework.gameState = Framework.GameState.GAME_CONTENT_LOADING;

        Thread threadForInitGame = new Thread() {
            @Override
            public void run() {
                // Sets variables and objects for the game.
                Initialize(0);
                // Load game files (images, sounds, ...)
                LoadContent();

                Framework.gameState = Framework.GameState.PLAYING;
            }
        };
        threadForInitGame.start();
    }

    public Game(int x) {
        Framework.gameState = Framework.GameState.GAME_CONTENT_LOADING;

        Thread threadForInitGame = new Thread() {
            @Override
            public void run() {
                //Load previous game state.
                Initialize(x);
                // Load game files (images, sounds, ...)
                LoadContent();

                Framework.gameState = Framework.GameState.PLAYING;
            }
        };
        threadForInitGame.start();
    }

    /**
     * Set variables and objects for the game.
     */
//    private void Initialize() {
//        map = new Map();
//        robot = new Robot(map.getCurrentMap().STARTy,map.getCurrentMap().STARTx);
//    }
    private void Initialize(int x) {
        map = new Map(x);
        this.mode = x;
        robot = new Robot(map.getCurrentMap().STARTy,map.getCurrentMap().STARTx);
        robot.decrease_energy = 0;
        robot.decrease_strong = 0;
        robot.speed = 8;
    }
    /**
     * Load game files - images, sounds, ...
     */
    private void LoadContent() {

    }

    private void loadGame(File f) {

    }


    public void RestartGame() {

    }

 
    public void UpdateGame(long gameTime, Point mousePosition) {
//         System.out.println(robot.robotState.toString());
         switch(robot.robotState){
              case FINDING: 
                   robot_sensor();
                   findDirection();
                   break;
              case MOVINGTO:
//                   System.out.println("("+robot.robot_location().m+", "+robot.robot_location().n+")");
//                   System.out.println("("+robot.tileMove.m+", "+robot.tileMove.n+")");
                   if(!this.sinhvatAttack()){
                         robot.moveTo();
                         if(map.getCurrentMap().data[robot.tileMove.m][robot.tileMove.n]!=Map.CHOSACPIN){
                              map.getCurrentMap().data[robot.robot_location().m][robot.robot_location().n] = Map.DALAU;
                         }
                   }     
                     break;
              case CLEANING:
                   if(map.getCurrentMap().data[robot.robot_location().m][robot.robot_location().n]==Map.CHOSACPIN){
                         robot.robotState = Robot.RobotState.FINDING;
                   }else{
                         clean();
                   }
                   break;
              
              case MOVINGASTACK:
//                   if(map.getCurrentMap().data[robot.tileMove.m][robot.tileMove.n]==Map.DALAU){
//                        robot.robotState = Robot.RobotState.FINDING;
//                   }
                   if(!this.sinhvatAttack()){
                         robot.movingAstar();
                   }
                   break;
              case SACPIN:
                   if(robot.energy<=robot.MaxEnergy-40){
                         robot.energy+=40;
                   }else{
                        robot.robotState = Robot.RobotState.FINDING;
                   }
                   break;
              case BAOHONG:
                   
                   robot.baohong();
                   break;
              case NEXTMAP:
                    map.nextMap();     
                    robot.reset(20,38);             
                    break;  
              
              case END:
                   Framework.gameState = Framework.GameState.GAMEOVER;break;
         }
         
        
//        playerAction(gameTime);
        map.getCurrentMap().monster_move_onMap();

    }
    private void check_battery(){
         
         if(robot.energy<robot.MaxEnergy*0.3){
              robot.tileMove = map.getCurrentMap().SacPin;
              robot.diDeTroVe = true;
              robot.nextState = Robot.RobotState.SACPIN;
               robot.findAstar();
              robot.robotState = Robot.RobotState.MOVINGASTACK;
         }else{
                     robot.robotState = Robot.RobotState.FINDING;
         }
//         if(robot.robot_location().isMatch(map.getCurrentMap().SacPin)){
//              
//         }
         
    }
    
     private void robot_sensor() {
          int n = (int) robot.x/Map.TILE_WIDTH;
          int m = (int) robot.y/Map.TILE_HEIGHT;
          
          robot.locationSen[0] = new Location(m-1,n);
          robot.locationSen[1] = new Location(m,n+1);
          robot.locationSen[2] = new Location(m+1,n);
          robot.locationSen[3] = new Location(m,n-1);
          
          robot.sensor[0] = map.getCurrentMap().data[m-1][n];
          robot.sensor[1] = map.getCurrentMap().data[m][n+1];
          robot.sensor[2] = map.getCurrentMap().data[m+1][n];
          robot.sensor[3] = map.getCurrentMap().data[m][n-1];
          
          robot.vitual_map[m-1][n] = robot.sensor[0];
          robot.vitual_map[m][n+1] = robot.sensor[1];
          robot.vitual_map[m+1][n] = robot.sensor[2];
          robot.vitual_map[m][n-1] = robot.sensor[3];
     }
    

    public void clean(){
         //Nếu ô đang đứng là sạch thì k cần cleanning, chỉ cần lau ướt
         if(robot.vitual_map[robot.robot_location().m][robot.robot_location().n]==Map.SACH_CHUALAU){
              if(map.getCurrentMap().data[robot.robot_location().m][robot.robot_location().n] != Map.CHOSACPIN){
                    map.getCurrentMap().data[robot.robot_location().m][robot.robot_location().n] = Map.DALAU;
              }
              check_battery();
              if(robot.strong<0){
                    robot.count_baohong = 1300;
                          robot.robotState = Robot.RobotState.BAOHONG;
                     }
         }else{
               if(robot.clean(robot.robot_location())){
                    if(map.getCurrentMap().data[robot.robot_location().m][robot.robot_location().n] != Map.CHOSACPIN){
                         map.getCurrentMap().data[robot.robot_location().m][robot.robot_location().n] = Map.DALAU;
                    }
                     check_battery();
                     if(robot.strong<0){
                           robot.count_baohong = 1200;
                          robot.robotState = Robot.RobotState.BAOHONG;
                     }
               }
         }
    }
    
//    public void monster_move()//logic move's monster
//    {
//        for (int i = 0; i < map.getCurrentMap().numberOfMonster; i++) {
//            double v = map.getCurrentMap().arrayMonster.get(i).getSpeed_base();
//            double nx = map.getCurrentMap().arrayMonster.get(i).getX();
//            double ny = map.getCurrentMap().arrayMonster.get(i).getY();
//            if (map.getCurrentMap().arrayMonster.get(i).getDirection() == Entity.Direction.RIGHT) {
//                nx = nx + v;
//            }
//            if (map.getCurrentMap().arrayMonster.get(i).getDirection() == Entity.Direction.LEFT) {
//                nx = nx - v;
//            }
//            if (map.getCurrentMap().arrayMonster.get(i).getDirection() == Entity.Direction.UP) {
//                ny = ny - v;
//            }
//            if (map.getCurrentMap().arrayMonster.get(i).getDirection() == Entity.Direction.DOWN) {
//                ny = ny + v;
//            }
//
//            if (valid_location(nx, ny) == submap.BLOCK) {
//                return;
//            } else {
//                map.getCurrentMap().arrayMonster.get(i).move(nx, ny);
//            }
//
//        }
//    }

    public void findDirection()// logic move's character
    {
         boolean found_valid_tile;
         for (int i=0;i<4;i++){
              if (robot.direction == directions[i]){   //hướng hien tai cua robot
                   found_valid_tile = false;
                   for(int j=i+3;j<=i+5;j++){            //Xét thứ tự ưu tiên từ thấp đến cao
                        if (robot.sensor[j%4]!=Map.TUONG && robot.sensor[j%4]!=Map.DALAU){     //Đi được, nhưng chưa đi qua
                             if(!found_valid_tile){
                                  robot.tileMove = robot.locationSen[j%4];
                                   found_valid_tile = true;
                             }else{
                                   robot.pushStack(robot.locationSen[j%4]);      // Đưa vào Stack
      //                             System.out.println("Stack + ("+robot.locationSen[j%4].m+", "+robot.locationSen[j%4].n+") ="+robot.sensor[j%4]);
                                   System.out.println("Stack = "+robot.stack.size());
                             }
                        }
                   }
                   if (found_valid_tile){     //Nếu trong 3 ô có 1 ô có thể đi được thì di chuyển tới ô đó 
                        //Nếu là vết bẩn, do choi thì di chuyển tới để lau
                        if((robot.vitual_map[robot.tileMove.m][robot.tileMove.n]>=1 && robot.vitual_map[robot.tileMove.m][robot.tileMove.n]<=Map.GIOIHANDIDUOC)
                                || (robot.vitual_map[robot.tileMove.m][robot.tileMove.n]==Map.DOVAT)){
                              robot.nextState = Robot.RobotState.CLEANING;
                        }else{
                             robot.nextState = Robot.RobotState.FINDING;
                        }
                        robot.robotState = Robot.RobotState.MOVINGTO;break;
                   }else{                       //Nếu không đi được, dùng A* tìm đường
                        if(robot.sizeStack()==0){          //Hêt stack kết thúc
                             if(map.id_current ==1){
                                       robot.vitual_map[20][0] = Map.DALAU;
                                        robot.tileMove = new Location(20,0);
                                        robot.nextState = Robot.RobotState.NEXTMAP;
                                        robot.findAstar();
                                        robot.robotState = Robot.RobotState.MOVINGASTACK;break;
                             }else{
                                   robot.robotState = Robot.RobotState.END;break;
                             }
                        }else{              
                             robot.tileMove = robot.popStack();
                             
                             while(map.getCurrentMap().data[robot.tileMove.m][robot.tileMove.n]==Map.DALAU && robot.stack.size()>0){
                                  robot.tileMove = robot.popStack();
                             }
                             robot.nextState = Robot.RobotState.CLEANING;
                             robot.findAstar();
                             robot.robotState = Robot.RobotState.MOVINGASTACK;break;
                        }
                   }
              }
         }
         
       
    }

    //Hanh dong cua quai vat
    public boolean sinhvatAttack() {
        for (int i = 0; i < map.getCurrentMap().arrayMonster.size(); i++) {
            Monster temp = map.getCurrentMap().arrayMonster.get(i);

                Rectangle shapeMonter = new Rectangle((int) temp.getX(), (int) temp.getY(), temp.getWidth(), temp.getHeight());
                if (shapeMonter.contains(new Point((int) robot.x, (int) robot.y))
                        || shapeMonter.contains(new Point((int) robot.x + robot.width - 20, (int) robot.y))
                        || shapeMonter.contains(new Point((int) robot.x, (int) robot.y + robot.height - 20))
                        || shapeMonter.contains(new Point((int) robot.x + robot.width - 20, (int) robot.y + robot.height - 20))) {
                     if(robot.strong<2){
                           robot.count_baohong = 1300;
                          robot.robotState = Robot.RobotState.BAOHONG;
                     }else{
                         robot.strong -=10;
                     }
                     return true;
            }
        }
        return false;
    }




    public int xPlayer() {
        return (int) robot.x;
    }

    public int yPlayer() {
        return (int) robot.y;
    }

    public void Draw(Graphics2D g2d, Point mousePosition) {
        g2d.translate(-robot.x + 416, -robot.y + 324);
        map.paint(g2d);
        robot.paint(g2d);
        if(this.mode ==0){
          for(Monster mons: map.getCurrentMap().arrayMonster){
               mons.paint(g2d);
          }
        robot.drawHPbar(g2d);
        }
    }

    

}
