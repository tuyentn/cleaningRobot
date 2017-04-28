/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpg;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author VPC
 */
class Map {
    public ArrayList<submap> submaps;      //Khai báo các submap
    private submap currentMap;   //Chọn lấy 1 submap để hiển thị trên màn hình
    public static ArrayList<BufferedImage> imgs;
    public static BufferedImage border,tempImg,daLau;
    public static BufferedImage chatlong,veto,bundat,buiban,sinhto,longdongvat,dovat,nammoc;
    public static BufferedImage backgr;
    public final static int TILE_WIDTH = 32;
    public final static int TILE_HEIGHT = 32;
    public final static int DALAU = 333;
    public final static int TUONG = 85;
    public final static int SACH_CHUALAU=0;
    public final static int VETBAN =334;
    public final static int CHOSACPIN = 499;
    
    public final static int GIOIHANDIDUOC = 80;
    public final static int GO_CHATLONG =1;
    public final static int GO_CHATLONG2=2;
    public final static int GO_VETO = 3;
    public final static int GO_BUNDAT =4;
    public final static int GO_BUIBAN=7;
    public final static int CHATLONG = 31;
    public final static int VETO = 33;
    public final static int BUNDAT = 34;
    public final static int BUIBAN = 37;
    public final static int SINHTO = 39;
    public final static int NAMMOC = 40;
    public final static int LONGDONGVAT = 54;
    public final static int DOVAT = 86;
    public int NUMBER_OF_SUBMAP = 2;
    public int id_current;
    
    
//    public Map(){
//        submaps = new ArrayList<>();
//        imgs = new ArrayList<>();
//        submaps.add(new submap());
//        loadImage();
//        for (int i=1;i<=NUMBER_OF_SUBMAP;i++){
//            submaps.add(new submap(i));
//        }
//        id_current = 1;
//        currentMap = submaps.get(id_current);          //Chọn map hiển thị
//    }
    public Map(int x){
         submaps = new ArrayList<>();
        imgs = new ArrayList<>();
        submaps.add(new submap());
        loadImage();
        if(x==1){
          NUMBER_OF_SUBMAP = 3;
          id_current = 3;
        }
        else{
          id_current = 1;
        }
        for (int i=1;i<=NUMBER_OF_SUBMAP;i++){
            submaps.add(new submap(i));
        }
        currentMap = submaps.get(id_current);      
    }
    public submap getCurrentMap()
    {
        return currentMap;
    }
    
    public void nextMap(){
        if(id_current<NUMBER_OF_SUBMAP){
            id_current++;
            currentMap = submaps.get(id_current);
            System.out.println("next "+id_current);
        }else{
            System.out.println("Đã qua hết tất cả map!");
        }
    }
    
    public void backMap(){
        if(id_current>1){
            id_current--;
            currentMap = submaps.get(id_current);
            System.out.println("back "+id_current);
        }
        else{
            System.out.println("Đang là map 1");
        }
    }
    
    //Load tài nguyên dùng chung cho tất cả các submap
    public void loadImage(){
        try
        {
            URL borderImgUrl = this.getClass().getResource("/rpg/resources/images/map/border.png");
            border = ImageIO.read(borderImgUrl);
            
             URL daLauImgUrl = this.getClass().getResource("/rpg/resources/images/map/daau.png");
            daLau = ImageIO.read(daLauImgUrl);
            URL vetoImgUrl = this.getClass().getResource("/rpg/resources/images/vetban/veto.png");
            veto = ImageIO.read(vetoImgUrl);
            URL ImgUrl = this.getClass().getResource("/rpg/resources/images/vetban/chatlong.png");
             chatlong= ImageIO.read(ImgUrl);
             URL bundatImgUrl = this.getClass().getResource("/rpg/resources/images/vetban/bundat.png");
             bundat= ImageIO.read(bundatImgUrl);
             URL buibanImgUrl = this.getClass().getResource("/rpg/resources/images/vetban/buiban.png");
             buiban= ImageIO.read(buibanImgUrl);
             URL sinhtoImgUrl = this.getClass().getResource("/rpg/resources/images/vetban/sinhto.png");
             sinhto= ImageIO.read(sinhtoImgUrl);
             URL longdongvatImgUrl = this.getClass().getResource("/rpg/resources/images/vetban/longdongvat.png");
             longdongvat= ImageIO.read(longdongvatImgUrl);
             URL nammocImgUrl = this.getClass().getResource("/rpg/resources/images/vetban/nammoc.png");
             nammoc= ImageIO.read(nammocImgUrl);
             URL dochoiImgUrl = this.getClass().getResource("/rpg/resources/images/dovat/dovat1.png");
             dovat= ImageIO.read(dochoiImgUrl);
            imgs.add(border);
            for (int i=1;i<=NUMBER_OF_SUBMAP;i++){
                URL tempImgUrl = this.getClass().getResource("/rpg/resources/images/map/map"+i+".png");
                tempImg = ImageIO.read(tempImgUrl);
                imgs.add(tempImg);
            }
            URL tempImgUrl = this.getClass().getResource("/rpg/resources/images/map/map"+3+".png");
                tempImg = ImageIO.read(tempImgUrl);
                imgs.add(tempImg);
   
          //  URL backgrImgUrl = this.getClass().getResource("/rpg/resources/images/map/bgr.png");
            //backgr = ImageIO.read(backgrImgUrl);
        }
        catch (IOException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    //Vẽ map hiển thị
    public void paint(Graphics2D g2d){
        g2d.drawImage(border, 0, 0,null);
        currentMap.paint(g2d);
    }
   
}
