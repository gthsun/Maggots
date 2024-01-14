import java.awt.Graphics;
import javax.swing.ImageIcon;

public class Weapon{
    
    private static final int pWidth = 49;
    
    protected int x;
    protected int y; 
    private ImageIcon image;
    private int width;
    private int height;
    private int type;
    private int num;
    
    protected boolean direction = true; // right = true
    private boolean shot = false;

    ImageIcon[] banana = {new ImageIcon("images/banana0.png"), new ImageIcon("images/banana1.png"),
                          new ImageIcon("images/banana2.png"), new ImageIcon("images/banana3.png")};
    ImageIcon[] grenade = {new ImageIcon("images/grenade0.png"), new ImageIcon("images/grenade1.png"),
                           new ImageIcon("images/grenade2.png"), new ImageIcon("images/grenade3.png"),
                           new ImageIcon("images/grenade4.png"), new ImageIcon("images/grenade5.png"),
                           new ImageIcon("images/grenade6.png"), new ImageIcon("images/grenade7.png")};
    ImageIcon[] bazooka = {new ImageIcon("images/bazooka0.png"), new ImageIcon("images/bazooka1.png"),
                           new ImageIcon("images/bazooka2.png"), new ImageIcon("images/bazooka3.png"),
                           new ImageIcon("images/bazooka4.png"), new ImageIcon("images/bazooka5.png"),
                           new ImageIcon("images/bazooka6.png"), new ImageIcon("images/bazooka7.png")};
    ImageIcon[] fbanana = {new ImageIcon("flip/banana0.png"), new ImageIcon("flip/banana1.png"),
                           new ImageIcon("flip/banana2.png"), new ImageIcon("flip/banana3.png")};
    ImageIcon[] fbazooka = {new ImageIcon("flip/bazooka0.png"), new ImageIcon("flip/bazooka1.png"),
                            new ImageIcon("flip/bazooka2.png"), new ImageIcon("flip/bazooka3.png"),
                            new ImageIcon("flip/bazooka4.png"), new ImageIcon("flip/bazooka5.png"),
                            new ImageIcon("flip/bazooka6.png"), new ImageIcon("flip/bazooka7.png")};


    private final double timePerFrame = 0.05;
    private double timer = timePerFrame;
    
    private Projectile projectile;

    public Weapon(int x, int y, int type){
        this.type = type;
        if(type== 1){
            image = direction ? banana[0] : fbanana[0];
            width = 32;
            height = 32;
        } else if(type== 2){
            // image = direction ? grenade[0] : fgrenade[0];
            image = grenade[0];
            width = 32;
            height = 40;
        } else if(type== 3){
            image = direction ? bazooka[0] : fbazooka[0];
            width = 40;
            height = 20;
        }
        num = 0;
    }

    public void draw(Graphics g){
        // System.out.println(direction);
        if (!shot) {
            if(type == 1){
                image = direction ? banana[0] : fbanana[0];
                width = 32;
                height = 32;
            } else if(type == 2){
                // image = direction ? grenade[0] : fgrenade[0];
                image = grenade[0];
                width = 32;
                height = 40;
            } else if(type == 3){
                image = direction ? bazooka[0] : fbazooka[0];
                width = 40;
                height = 20;
            }
        } else {
            if (type == 2)
                return;
            // if (type == 1) {
            //     g.drawImage((direction ? banana[3] : fbanana[3]).getImage(), x - (!direction ? pWidth/2 : 0), 780-y, width, height, null);
            //     return;
            // }
            timer -= type == 1 ? 0.01 : 0.01;
            if(timer <= 0){
                ImageIcon[] arr;
                if(type == 1)
                    arr = direction ? banana : fbanana;
                else if(type == 2)
                    arr = grenade;
                else
                    arr = direction ? bazooka : fbazooka;
                timer = timePerFrame;
                if(num < arr.length - 1){
                    num++;
                    if (type == 1 && num == arr.length - 1)
                        timer += 1.5;
                } 
                else {
                    num = 0;
                    shot = false;
                }
                image = arr[num];
            }
        }
        
        
        g.drawImage(image.getImage(), x - (!direction ? pWidth/2 : 0), 780-y, width, height, null);
    }
    
    public void projDraw(Graphics g) {
        projectile.draw(g);
    }

    public void shoot(double power, double angle, int sx, int sy){
        // timer -= 0.01;
        // if(timer <= 0){
        //     ImageIcon[] arr;
        //     if(type == 1){arr = direction ? banana : fbanana;}
        //     // else if(type == 2){arr = direction ? grenade : fgrenade;}
        //     else if(type == 2){arr = grenade;}
        //     else{arr = direction ? bazooka : fbazooka;}
        //     timer = timePerFrame;
        //     if(num < arr.length - 1){num++;} 
        //     else{num = 0;}
        //     image = arr[num]; 
        // }
        projectile = new Projectile(sx, sy, angle, power, type);
        shot = true;
    }
    
    public void projMove(boolean[][] map, Player[] players) {
        projectile.move(map, players);
    }
    
    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public void newTurn() {
        shot = false;
    }
    
    public boolean projMoving() {
        return projectile != null && !projectile.isDead();
    }
    
}