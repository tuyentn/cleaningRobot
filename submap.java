/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpg;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author VPC
 */
public class submap {

    /*
    Quy ước đặt tên các biến:
    Vùng không đi  được các biến nhỏ hơn 10
    Vùng đi được có giá trị từ 10 - 20
    Vùng đi bị vẽ lại khi player đi vào có giá trị 20 -30
     */
    public int width;
    public int height;
    public static final int BLOCK = 0;//Quy dinh vung khong duoc phep di vao
    public static final int CLEAR = 10;// Quy dinh vung di duoc
    public static final int REDRAW = 21;// QUy dinh vung cay di vao phai ve lai
    public static final int CHUYENMAP = 15;//Quy dinh vung di vao co the chuyen map
    public static final int GAIDOC = 12;//DI vao duoc nhung toc di cham va bi tru mau
    
    private int index;
    public static final int START = 30;
    public int STARTx;// Toa do xua hien cua nhan vat toa do x
    public int STARTy;// Toa do xuat hien y cua nhan vat
    // Vung player xuat hien
    public static final int END = 35;
//    public int index_submap;
    // Vung chuyen map, Khi player vao vung nay se chuyen map 
    File map,map_monster,monster,monster_1,monster_2;
    Scanner scan;

    String[][] stringTileNumbers = new String[100][100];
    int[][] data = new int[100][100];
    BufferedImage spriteSheet,spriteSheet_1,spriteSheet_2;
    Image[][] monsterSprites = new Image[4][3];
    Image[][] monsterSprites_1 = new Image[4][3];
    Image[][] monsterSprites_2 = new Image[4][3];
    Image[][] monsterSprites_3 = new Image[4][3];
    public Monster conSoi,tempMonster,xaHoiDen,girl;
    public ArrayList<Location> dovats = new ArrayList<>();
    public ArrayList<Monster> arrayMonster;
    public int numberOfMonster;
    public Location SacPin, canhCua;
    //Khởi tạo có tham số là 1 file .txt
    public submap(int index_submap) {
        this.index = index_submap;
        arrayMonster = new ArrayList<>();
        numberOfMonster = 0;
        map = new File("src/rpg/resources/mapdata/map"+this.index+".txt");
        map_monster = new File("src/rpg/resources/mapdata/map"+this.index+"_monster.txt");
        loadMap();
        loadMapMonster();
        width = 0;
        height = 0;
        canhCua = new Location(20,0);
    }
    public submap(int index_submap,int x) {
        this.index = index_submap;
        arrayMonster = new ArrayList<>();
        numberOfMonster = 0;
        map = new File("src/rpg/resources/mapdata/map"+this.index+".txt");
        map_monster = new File("src/rpg/resources/mapdata/map"+this.index+"_monster.txt");
        loadMap();
        loadMapMonster();
        width = 0;
        height = 0;
        canhCua = new Location(20,0);
    }

    //Khởi tạo mặc định với map demo
    public submap() {
        map = new File("src/rpg/resources/mapdata/map1.txt");    
    }

    public void loadMap() {
        try {

            scan = new Scanner(map);
            String temp;
            temp = scan.next();
            this.width = Integer.parseInt(temp);
            temp = scan.next();
            this.height = Integer.parseInt(temp);
            temp = scan.next();
            this.STARTx = Integer.parseInt(temp);
            temp = scan.next();
            this.STARTy = Integer.parseInt(temp);
            int x = 0;
            int y = 0;
            System.out.println("width = "+width+" . Height = "+height);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    stringTileNumbers[i][j] = scan.next();
                }
            }
            System.out.println("DOne!");
            for (int i = 0; i < stringTileNumbers.length; i++) {

                for (int j = 0; j < stringTileNumbers[i].length; j++) {

                    if (stringTileNumbers[i][j] == null) {

                        data[i][j] = -1;
                    } else {
                         
                        data[i][j] = Integer.parseInt(stringTileNumbers[i][j]);
                        if(data[i][j]==Map.CHOSACPIN){
                             this.SacPin = new Location(i,j);
                        }
                        if(data[i][j] == Map.DOVAT){
                             this.dovats.add(new Location(i,j));
                        }
                        
                         System.out.print(data[i][j]);
                    }

                }
                 System.out.println("");
            }
        } catch (IOException ex) {

            ex.printStackTrace();

        }
    }
    public void loadMapMonster(){
        int m, n;
        int exp, ATK,  DEF,  HP;
        double speed;
        Entity.Direction dir;
        try{
            scan = new Scanner(map_monster);
            String temp;
            temp = scan.next();
            this.numberOfMonster = Integer.parseInt(temp);
            for (int i=0;i<this.numberOfMonster;i++){
                String url = scan.next();
                 System.out.println(url);
                tempMonster = new Monster(new File(url));
                m = Integer.parseInt(scan.next());
                n = Integer.parseInt(scan.next());
                speed = Double.parseDouble(scan.next());
                switch(Integer.parseInt(scan.next())){
                    case 1: dir = Entity.Direction.LEFT; break;
                    case 2: dir = Entity.Direction.UP; break;
                    case 3: dir = Entity.Direction.RIGHT; break;
                    case 4: dir = Entity.Direction.DOWN; break;
                    default: dir = Entity.Direction.DOWN; break;
                }
                tempMonster.setProperties(n*Map.TILE_WIDTH, m*Map.TILE_HEIGHT, speed,  dir);
                arrayMonster.add(tempMonster);
            }
        
        } catch (IOException ex) {

            ex.printStackTrace();

        }
    }
    
    public void monster_move_onMap(){
         for(Monster monster: this.arrayMonster){
               monster.move_auto();
   //            System.out.println("Ran =("+temp.getRanX()+", "+temp.getRanY()+")");
               if(valid_location(monster.getRanX(),monster.getRanY())){
                   monster.move(monster.getRanX(), monster.getRanY());
               }
               else{
                   monster.randomDirec();
               }
         }
    }
    

    
    public boolean valid_location(double nx, double ny) {
        if (data[(int) (ny / Map.TILE_HEIGHT)][(int) (nx / Map.TILE_WIDTH)] == Map.TUONG
                || data[(int) ((ny+32) / Map.TILE_HEIGHT )][(int) (nx / Map.TILE_WIDTH)] == Map.TUONG
                || data[(int) (ny / Map.TILE_HEIGHT)][(int) ((nx+32) / Map.TILE_WIDTH )] == Map.TUONG
                || data[(int) ((ny+32) / Map.TILE_HEIGHT)][(int) ((nx+32) / Map.TILE_WIDTH )] == Map.TUONG) {
            return false;
        } 
        return true;

    }
//    public void removeMonster(Monster monster){
//        int index_delete = arrayMonster.indexOf(monster);
//        arrayMonster.remove(index_delete);
//    }
    
    
    public void paint(Graphics2D g2d) {
         
        g2d.drawImage(Map.imgs.get(this.index), 4 * 24, 3 * 18, 1280, 960, null);
        for (int i = 0; i < stringTileNumbers.length; i++) {

                for (int j = 0; j < stringTileNumbers[i].length; j++) {
                     
                     switch(data[i][j]){
                          case Map.DALAU:   g2d.drawImage(Map.daLau, j*32+32*3+1, i*32+32*2 -9 , null);   break;
                          case Map.GO_CHATLONG: 
                          case Map.GO_CHATLONG2:
                          case Map.CHATLONG:  g2d.drawImage(Map.chatlong, j*32+32*3, i*32+32*2 -10 , null);    break;
                          case Map.GO_VETO:
                          case Map.VETO:   g2d.drawImage(Map.veto, j*32+32*3, i*32+32*2 -10 , null);   break;
                          case Map.GO_BUNDAT:
                          case Map.BUNDAT:   g2d.drawImage(Map.bundat, j*32+32*3, i*32+32*2 -10 , null);   break;
                          case Map.GO_BUIBAN:
                          case Map.BUIBAN:   g2d.drawImage(Map.buiban, j*32+32*3, i*32+32*2 -10 , null);   break;
                          case Map.SINHTO:   g2d.drawImage(Map.sinhto, j*32+32*3, i*32+32*2 -10 , null);   break;
                          case Map.LONGDONGVAT:    g2d.drawImage(Map.longdongvat, j*32+32*3, i*32+32*2 -10 , null);  break;
                          case Map.NAMMOC: g2d.drawImage(Map.nammoc, j*32+32*3, i*32+32*2 -10 , null);  break;
                          default: break;
                     }
                     
                }
        }
         for(Location loc: dovats){
              g2d.drawImage(Map.dovat,  loc.n*32+32*3+2, loc.m*32+32*2 -10 , null);
         }
       
    }
    

}
