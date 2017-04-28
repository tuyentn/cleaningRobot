/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author VPC
 */
class Monster extends Entity {

    private int width, height;
    private File image_path;
    private BufferedImage spriteSheet;
    private Image[][] monsterSprites;
    private Image image;
    private int i_image;
    private int countTime, countdownImmortal,countAtRest;
    private Random random;
    private int random_direction = 0;
    private double randomX, randomY;

    public Monster() {
        super.setX(0);
        super.setY(0);
        super.setDEF_base(0);
        super.setSpeed(1);
        super.setHP_base(0);
        super.setAttack_base(0);
        super.direction = Entity.Direction.DOWN;
    }
//Load anh

    public Monster(File path) {
        this.visible = true;
        monsterSprites = new Image[4][3];
        i_image = 0;
        countTime = 0;
        image_path = path;
        int x = 0, y = 0;
        try {
            spriteSheet = ImageIO.read(image_path);
        } catch (IOException ex) {
            Logger.getLogger(Monster.class.getName()).log(Level.SEVERE, "Khong load dc anh monster", ex);
        }
        this.width = spriteSheet.getWidth() / 3;
        this.height = spriteSheet.getHeight() / 4;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                monsterSprites[i][j] = spriteSheet.getSubimage(x, y, width, height);
                x += width;
            }
            x = 0;
            y += height;
        }
        random = new Random();
    }
//gan cac gia tri cho thuoc tinh

    public void setProperties(double x, double y, double speed, Entity.Direction dir) {
        this.setX(x);
        this.setY(y);
        this.speed_base = speed;
        this.direction = dir;
        this.randomX = x;
        this.randomY = y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
    


    public void move(double nx, double ny) {
        switch (direction) {
            case LEFT:
                image = monsterSprites[1][i_image];
                break;
            case RIGHT:
                image = monsterSprites[2][i_image];
                break;
            case UP:
                image = monsterSprites[3][i_image];
                break;
            case DOWN:
                image = monsterSprites[0][i_image];
                break;
        }
        
            this.x = nx;
            this.y = ny;
            if (countTime * speed_base % (15) == 0) {
                if (i_image > 0) {
                    i_image--;
                } else if (i_image == 0) {
                    i_image = 2;
                }
            }
       
        countTime++;
    }

    public void move_auto() {
        randomX = x;
        randomY = y;
        switch (direction) {
            case LEFT:
                image = monsterSprites[1][i_image];
                    randomX = (x - speed_base);
                break;
            case RIGHT:
                image = monsterSprites[2][i_image];
                    randomX = (x + speed_base);
                break;
            case UP:
                image = monsterSprites[3][i_image];
                    randomY = (y - speed_base*3/4);
                break;
            case DOWN:
                image = monsterSprites[0][i_image];
                    randomY = (y + speed_base*3/4);
                break;
        }

        if (countTime * speed_base % (100) == 0) {
            randomDirec();
        }
    }

    public void randomDirec() {
        random_direction = random.nextInt(4);
        switch (random_direction) {
            case 0:
                this.direction = Entity.Direction.LEFT;
                break;
            case 1:
                this.direction = Entity.Direction.RIGHT;
                break;
            case 2:
                this.direction = Entity.Direction.UP;
                break;
            case 3:
                this.direction = Entity.Direction.DOWN;
                break;

        }
    }

    public double getRanX() {
        return this.randomX;
    }

    public double getRanY() {
        return this.randomY;
    }

    void paint(Graphics2D g2d) {
            g2d.drawImage(image, (int) x+100, (int) y+52, null);
//            drawHPbar(g2d);
    }

    void drawHPbar(Graphics2D g2d) {
        g2d.setColor(Color.PINK);
        g2d.drawRect((int) x - 1, (int) y - 6, 28, 4);
    }

}
