/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author VPC
 */


package rpg;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpg.Rules.Rule;

public class Robot {
    public double x,y;
    public double speed;
    public Entity.Direction direction;
    public int[] sensor = new int[4];
    public Location[] locationSen = new Location[4];
    public int[] uuTien = new int[4];
    public int[][] vitual_map = new int[50][50];
    public ArrayList<Location> stack;
    public int countdownImmortal, countAtRest, countTime;
    public static final int TIMECLEAN = 1000;
    public static enum RobotState{CLEANING, MOVINGASTACK, MOVINGTO, FINDING, SACPIN,NEXTMAP, END,BAOHONG};
    public RobotState robotState, nextState;
    public BufferedImage character,message;
    public Image[][] Character = new Image[4][3];//new
    public BufferedImage character_spritesheet, sensorArea,sensoredTile;//new
    public int width, height;
    public int Width, Height;//new
    public int index_img;
    public int MaxEnergy,MaxStrong;
    public int energy,strong;
    public boolean baohong;
    public boolean diDeTroVe= false;
    public int count_baohong;
    public Location tileMove;
    public Location a_current;
    public ArrayList<Location> Astar_path;
    public AstarAlgorithm Astar;
    public Rules rules;
    public Rule useRule;
    public int mode;
    public int decrease_energy;
     public int decrease_strong;
     public int chonglan;
    public Robot(int m, int n){
         this.mode = 0;
         decrease_energy = 20;
         decrease_strong = 1;
         speed = 4;
         stack = new ArrayList<>();
        this.Load_image();
        character = (BufferedImage) Character[0][1];
        this.direction = Entity.Direction.RIGHT;
        y = m * Map.TILE_HEIGHT;
        x = n* Map.TILE_WIDTH;
        this.robotState = RobotState.FINDING;
          this.countTime = 0;
          this.MaxEnergy = 28500;
          this.MaxStrong = 8000;
          this.energy = 28500;
          this.strong = 8000;
          for (int i=0;i<50;i++){
               for (int j=0;j<50;j++){
                    this.vitual_map[i][j]=-1;
               }
          }
          this.vitual_map[2][1]=499;
          baohong =false;
         rules = new Rules();
         chonglan=0;
    }
    
    public void Load_image() {
        try {
            //load img of character
            URL sensoredUrl = this.getClass().getResource("/rpg/resources/images/map/sensored.png");
            sensoredTile = ImageIO.read(sensoredUrl);
            URL messImgUrl = this.getClass().getResource("/rpg/resources/images/map/message.png");
            message = ImageIO.read(messImgUrl);
            
            URL sensorAreaUrl = this.getClass().getResource("/rpg/resources/character/sensorArea.png");
            sensorArea = ImageIO.read(sensorAreaUrl);

            URL character_sheetUrl = this.getClass().getResource("/rpg/resources/images/character/doraemon.png");
            character_spritesheet = ImageIO.read(character_sheetUrl);
           
            this.Width = character_spritesheet.getWidth() / 3;
            this.Height = character_spritesheet.getHeight() /4;

            //load img of character
            int tx = 0, ty = 0;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    Character[i][j] = character_spritesheet.getSubimage(tx, ty, this.Width, this.Height);
                    tx += this.Width;
                }
                tx = 0;
                ty += this.Height;
            }

            
        } catch (IOException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public void load_useRule(Location loc){
         if(this.vitual_map[loc.m][loc.n] >=1 && this.vitual_map[loc.m][loc.n]<=Map.GIOIHANDIDUOC){
               this.useRule = this.rules.set_rules.get(this.vitual_map[loc.m][loc.n]);
         }else{
              this.useRule = this.rules.set_rules.get(0);
         }
    }
    public boolean clean(Location loc){
         
         if(countTime <= TIMECLEAN-15){   //Chưa lau xong
               this.energy -=4;
               this.strong -=1;
               countTime+=15;
               
               return false;           
         }else{
              countTime = 0;
               this.stack.remove(loc);
               return true;
         }
    }
     public void baohong(){
          if(count_baohong>0){
               count_baohong-=2;
          }else{
               this.strong = this.MaxStrong;
               this.robotState = RobotState.FINDING;
          }
     }
    public void reset(int start_m,int start_n){
         this.direction = Entity.Direction.DOWN;
         y = start_m * Map.TILE_HEIGHT;
         x = start_n* Map.TILE_WIDTH;
          this.robotState = RobotState.FINDING;
         for (int i=0;i<50;i++){
               for (int j=0;j<50;j++){
                    this.vitual_map[i][j]=-1;
               }
          }
    }
    public void pushStack(Location item){
        this.stack.add(item);
    }
    public Location popStack(){
//         Location curr = this.robot_location();
//         Location closest = this.stack.get(this.stack.size()-1);
//         double nearest = curr.distance(closest);
//         ArrayList<Integer> indexs = new ArrayList<>();
//         indexs.add(this.stack.size()-1);
//         for(int i=this.stack.size()-1;i>=0;i--){
//              if(curr.distance(stack.get(i))<nearest){
//                   closest = stack.get(i);
//                   nearest = curr.distance(stack.get(i));
//                   indexs.add(i);
//              }
//         }
//         for(Integer index: indexs){
//              if(stack.get(index).isMatch(closest)){
//                   stack.remove(stack.get(index));
//              }
//         }
//         return closest;
    Location temp = this.stack.get(stack.size()-1);
        this.stack.remove(stack.size()-1);
        while(stack.contains(temp)){
            stack.remove(temp);
        }
        return temp;
    }
    public int sizeStack(){
         return this.stack.size();
    }
    public void moveOne(Location destination){
         double desX = (double) destination.n * Map.TILE_WIDTH;
         double desY = (double) destination.m * Map.TILE_HEIGHT;
         if( ((this.x >= desX-speed/2)&&(this.x <= desX+speed/2)) && ((this.y >= desY-speed/2)&&(this.y <= desY+speed/2)) ){
              this.robotState = Robot.RobotState.MOVINGASTACK;
         }else {
               if( this.x > (desX+speed/2)){
                    this.x -= this.speed;
                    this.direction = Entity.Direction.LEFT;
               }else if (this.x < (desX-speed/2)){
                    this.x += this.speed;
                    this.direction = Entity.Direction.RIGHT;
               }
               if (this.y > (desY+speed/2)){
                    this.y -= this.speed;
                    this.direction = Entity.Direction.UP;
               }else if(this.y < (desY-speed/2)){
                    this.y += this.speed;
                    this.direction = Entity.Direction.DOWN;
               }
               this.energy--;
         }
    }
    public void moveTo(){
         double desX = (double) tileMove.n * Map.TILE_WIDTH;
         double desY = (double) tileMove.m * Map.TILE_HEIGHT;
         
          if( ((this.x >= desX-speed/2)&&(this.x <= desX+speed/2)) && ((this.y >= desY-speed/2)&&(this.y <= desY+speed/2)) ){
              if(this.stack.contains(this.robot_location())){
                   System.out.println("REMOVE TILEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                   this.stack.remove(this.robot_location());
                   System.out.println("Stack = "+this.stack.size());
              }
               this.robotState = nextState;
         }else {
               if( this.x > (desX+speed/2)){
                    this.x -= this.speed;
                    this.direction = Entity.Direction.LEFT;
               }else if (this.x < (desX-speed/2)){
                    this.x += this.speed;
                    this.direction = Entity.Direction.RIGHT;
               }
               if (this.y > (desY+speed/2)){
                    this.y -= this.speed;
                    this.direction = Entity.Direction.UP;
               }else if(this.y < (desY-speed/2)){
                    this.y += this.speed;
                    this.direction = Entity.Direction.DOWN;
               }
               this.energy--;
         }
          
    }
    
    public void findAstar(){
         Astar = new AstarAlgorithm(robot_location(),tileMove,vitual_map);
         Astar_path = Astar.Looking_path();
         System.out.println("Astar.size = "+Astar_path.size());
         for(Location loc : Astar_path){
              System.out.print("("+loc.m+","+loc.n+")=> ");
         }
         a_current = Astar_path.get(Astar_path.size()-1);
    }
    public void movingAstar(){
         if(robot_location().isMatch(tileMove)){
              this.diDeTroVe = false;
              this.robotState = this.nextState;
         }else{
              if(robot_location().isMatch(a_current)){         //Vị trí hiện tại trùng với ô cần đến
                   if(vitual_map[a_current.m][a_current.n]==Map.DALAU){
                         chonglan++;
                    }
                  Astar_path.remove(Astar_path.size()-1);       // Xóa ô cần đến khỏi path
                  a_current = Astar_path.get(Astar_path.size()-1);  // Gán lại ô cần đến là phần tử tiếp theo
              }else{
                   moveOne(a_current);                    //Di chuyển đến vị trí cần đến
              }
         }
    }
    public Location robot_location(){
         return new Location((int)this.y/Map.TILE_HEIGHT, (int)this.x/Map.TILE_WIDTH);
    }
    public void draw_direction() {

            if (countTime * speed % 540 == 0) {
                if (index_img > 0) {
                    index_img--;
                } else if (index_img == 0) {
                    index_img = 2;
                }
            }
        
        if (direction == Entity.Direction.LEFT) {
            character = (BufferedImage) Character[1][index_img];
        }
        if (direction == Entity.Direction.RIGHT) {
            character = (BufferedImage) Character[2][index_img];
        }
        if (direction == Entity.Direction.UP) {
            character = (BufferedImage) Character[3][index_img];
        }
        if (direction == Entity.Direction.DOWN) {
            character = (BufferedImage) Character[0][index_img];
        }

    }
     void paint(Graphics2D g2d) {
           
            g2d.drawImage(sensorArea, (int) (x+2*32),(int) (y+20) , null);        //Vung` cam nhan
            
            //Tile da~ cam nhan duoc <- vitual_map[][]
            for(int i=0;i<50;i++){
                 for (int j=0;j<50;j++){
                      if(this.vitual_map[i][j]!=-1){
                           g2d.drawImage(sensoredTile, (int)(j*32+96), (int)(i*32+54), null);
                      }
                 }
            }
             g2d.drawImage(character, (int) (x+3*32-8), (int) (y+38), this.Width, this.Height, null);
//            drawHPbar(g2d);
               
        draw_direction();

    }
      void drawHPbar(Graphics2D g2d) {
        g2d.setColor(Color.green);
        g2d.drawRect((int) x+3*32 - 17, (int) y-4, 54, 7);
        g2d.setColor(Color.yellow);
        g2d.fillRect((int) x+3*32 - 16, (int) y-3, (int) 53 * energy / MaxEnergy, 6);
        g2d.setColor(Color.blue);
        g2d.drawRect((int) x+3*32 - 6, (int) y+32 - 28, 35, 4);
        g2d.fillRect((int) x+3*32 - 5, (int) y+32 - 27, (int) 34 * this.strong / this.MaxStrong, 3);
        
          if(robotState == RobotState.CLEANING){
                     load_useRule(this.robot_location());
                     g2d.setColor(Color.RED);
                     g2d.drawRect((int) x-301, (int) y+246, 800, 15);
                     g2d.setColor(Color.ORANGE);
                     g2d.fillRect((int) x-300, (int) y+247, (int) 800 * countTime / TIMECLEAN, 14);
                     g2d.drawImage(message, (int) x-416, (int) y+270, null);
                     g2d.setColor(Color.WHITE);
                     g2d.setFont(new  Font("SansSerif", Font.PLAIN, 25));
                     g2d.drawString("Loại vết bẩn: ", (int) x-380 ,(int) y+305);
                     g2d.drawString("Mã luật: ", (int) x+150 ,(int) y+328);
                     g2d.drawString("Giải quyết: ", (int) x-380 ,(int) y+353);
                     g2d.drawString("Mã hành động: ", (int) x+150 ,(int) y+375);
                     g2d.drawString("Địa hình: ", (int) x-380 ,(int) y+399);
                     g2d.drawString("Trọng số: ", (int) x+150 ,(int) y+399);
                     //Tham số luật
                     g2d.setColor(Color.ORANGE);
                     g2d.drawString(useRule.suKien, (int) x-235 ,(int) y+305);
                     g2d.drawString(String.valueOf(useRule.maLuat), (int) x+250 ,(int) y+328);
                     g2d.drawString(useRule.giaiQuyet, (int) x-255 ,(int) y+353);
                     for(int i=0;i<useRule.maHD.length;i++){
                          g2d.drawString(String.valueOf(useRule.maHD[i])+", ", (int) x+325+i*55 ,(int) y+375);
                     }
                     g2d.drawString(useRule.diaHinh, (int) x-280 ,(int) y+399);
                      g2d.drawString(""+useRule.trongSo, (int) x+300 ,(int) y+399);
                }else if(robotState == RobotState.BAOHONG){
                     g2d.setFont(new  Font("SansSerif", Font.PLAIN, 35));
                     g2d.setColor(Color.ORANGE);
                      g2d.drawImage(message, (int) x-416, (int) y+270, null);
                      if(this.count_baohong>850){
                          g2d.drawString("Bị hỏng!! Đang gọi trung tâm sửa chữa...", (int) x-380 ,(int) y+345);
                      }else if(count_baohong>720){
                           g2d.drawString("Bị hỏng!! Đang gọi trung tâm sửa chữa...", (int) x-380 ,(int) y+345);
                           g2d.drawString("- Alo!", (int) x-100 ,(int) y+385);
                      }else if(count_baohong>650){
                           g2d.drawString("- Tôi bị hỏng rồi, làm sao bây giờ???", (int) x-380 ,(int) y+345);
                      }else if(count_baohong>500){
                           g2d.drawString("- Tôi bị hỏng rồi, làm sao bây giờ???", (int) x-380 ,(int) y+345);
                           g2d.drawString("- Đừng hoảng hốt, hãy làm theo các bước @#*&$@!*$....", (int) x-380 ,(int) y+385);
                      }else if(count_baohong>300){
                           g2d.drawString("Đang tự sửa chữa ...", (int) x-380 ,(int) y+345);
                      }else{
                           g2d.drawString("- Cảm ơn tôi khỏe lại rồi!", (int) x-380 ,(int) y+345);
                      }
                }else if(robotState == RobotState.MOVINGASTACK && this.diDeTroVe){
                     g2d.setColor(Color.ORANGE);
                     g2d.setFont(new  Font("SansSerif", Font.PLAIN, 35));
                      g2d.drawImage(message, (int) x-416, (int) y+270, null);
                      g2d.drawString("Hết năng lượng. Tìm đường về sạc...", (int) x-380 ,(int) y+345);
                }
    }
}
