import javax.swing.ImageIcon;
import java.awt.Graphics;

public class Projectile extends PhysicsObject{

    protected static int pWidth; 
    protected static int pHeight;
    private ImageIcon image;
    private int type;
    private double radius = 128;
    protected int expR = 64;

    private final double timePerFrame = 0.2;
    private double timer = timePerFrame;
    
    private double lifeTime = 0.0;
    private double explWaitTime = 1.6;
    private boolean exploded = false;
    private boolean canBounce;
    
    private int damage;
    private double kb = 1.0;
    
    private final ImageIcon[] explosion = {new ImageIcon("images/explosion0.png"), new ImageIcon("images/explosion1.png"),
                                        new ImageIcon("images/explosion2.png"), new ImageIcon("images/explosion3.png"),
                                        new ImageIcon("images/explosion4.png"), new ImageIcon("images/explosion5.png"),
                                        new ImageIcon("images/explosion6.png"), new ImageIcon("images/explosion7.png"),}; // TODO

    public Projectile(int x, int y, double angle, double power, int type){
        super(x, y, 10*(power+0.1)*Math.cos(angle), -10*(power+0.1)*Math.sin(angle), pWidth, pHeight);
        minVel = 0.0;
        this.type = type;
        canBounce = false;
        if(type == 1){ // projectile
            pWidth = 10;
            pHeight = 10;
            image = new ImageIcon("images/projectile0.png");
            gravity = 0;
            radius = 16;
            expR = 10;
            damage = 10;
        } else if(type == 2){ // grenade
            pWidth = 16;
            pHeight = 20;
            canBounce = true;
            image = new ImageIcon("images/grenade7.png");
            damage = 30;
            radius = 160;
            kb = 3.0;
        } else if (type == 3){ // bazookith
            pWidth = 20;
            pHeight = 20;
            gravity = -0.10;
            image = new ImageIcon("images/projectile1.png");
            damage = 50;
            kb = 3.0;
        }
        exploded = false;
        // System.out.println("x: "+this.x+", y: "+this.y);
    }

    public void draw(Graphics g) {
        // System.out.println("x: "+x+", y: "+y);
        // System.out.println(exploded);
        if (exploded) {
            if (explWaitTime <= 0.01) {
                return;
            } else {
                explWaitTime -=0.05;
                g.drawImage(explosion[7-(int)(explWaitTime/0.2)].getImage(),(int)x-(int)(radius/2.0), 780-(int)y-(int)(radius/2.0), (int)(radius), (int)(radius), null);
                return;
            }
        }
        // System.out.println("drew");
        g.drawImage(image.getImage(), (int)x-(pWidth/2), 780-(int)y-(pHeight/2), pWidth, pHeight, null);
        
        timer -= 0.01;
        if (timer <=0) {
            timer = timePerFrame;
        }
    }

    public void move(boolean[][] map, Player[] players) {
        // System.out.println("yv: "+yVel);
        // System.out.println("x: "+x+", y: "+y);
        if (exploded)
            return;
        
        if (type == 3)
            gravity = -0.1 - Math.random()/10.0; 
        // System.out.println("x: "+x+", y: "+y);
        if(type == 1){ // proj
            super.move(map);
        }
        else if(type == 2){ // grenade
            super.move(map);
        }
        else{ // bazooktih
            super.move(map);
        }
        // System.out.println(lifeTime);
        lifeTime += 0.01;
        // System.out.println("t: "+type+", lt: "+lifeTime);
        // if (type == 1 && lifeTime > 1.0){
        //     gravity = -0.8;
        // }
        
        //todo
        if (type != 2) {
            for (int i = 0; i < players.length; i++) {
                if (players[i].isHit(this)) {
                    explode(map, players);
                    // double V = kb * (Math.sqrt(Math.pow(players[i].getX()-x,2) + Math.pow(players[i].getY()-y,2))*(double)expR);
                    double V = kb;
                    double angle = Math.atan2(players[i].getY() - y, players[i].getX() - x);
                    players[i].getHit(damage, Math.cos(angle)*V, Math.sin(angle)*V);
                }
            }
        }
        
        if (!canBounce && bounces>=1) {
            explode(map, players);
            // System.out.println("x: "+x+", y: "+y);
        } else if (lifeTime >= 2.5 && type == 2) {
            explode(map, players);
            // for (int i = 0; i < players.length; i++) {
            //     // double V = kb * (Math.min(Math.sqrt(Math.pow(players[i].getX()-x,2) + Math.pow(players[i].getY()-y,2))-32,0)*(double)expR);
            //     double V = kb;
            //     double angle = Math.atan2(players[i].getY() - y, players[i].getX() - x);
            //     players[i].getHit(damage, Math.cos(angle)*V, Math.sin(angle)*V);
            // }
            // System.out.println("x: "+x+", y: "+y);
        } else if (x < 0 || x > 1500 || y < 0 || y > 780) {
            explode(map, players);
            // System.out.println("x: "+x+", y: "+y);
        }
            
        if (type == 1)
            gravity = -1 *Math.pow(Math.max(lifeTime-0.7,0), 1.5);
        // System.out.println(gravity);
        // System.out.println("x: "+x+", y: "+y);
    }

    public void explode(boolean[][] map, Player[] players){
        // System.out.println("x: "+x+", y: "+y);
        explWaitTime = 1.6;
        exploded = true;
        
        for (int mx = 0; mx < 150; mx++) {
            for (int my = 0; my < 78; my++) {
                if (map[my][mx] && Math.sqrt(Math.pow(mx-(x/10),2) + Math.pow(my-(y/10),2)) < (radius/16.0)) {
                    // System.out.println("expl math");
                    map[my][mx] = false;
                }
            }
        }
        
        for (int i = 0; i < players.length; i++) {
            if (players[i].inRadius(this)) {
                // double V = kb * (Math.min(Math.sqrt(Math.pow(players[i].getX()-x,2) + Math.pow(players[i].getY()-y,2))-0,0)*(double)expR);
                double V = kb;
                // System.out.println("v: "+V+", dist: "+(Math.sqrt(Math.pow(players[i].getX()-x,2) + Math.pow(players[i].getY()-y,2))));
                double angle = Math.atan2(players[i].getY() - y, players[i].getX() - x);
                players[i].getHit(damage/(type == 2 ? 1 : 2), Math.cos(angle)*V, Math.sin(angle)*V);
            }
        }
        
        // if(type == 2){
        //     if(r<30){
        //         g.setColor(new Color(255, 255, blue));
        //         g.fillOval((int)x-r, (int)y-r, 2*r, 2*r);
        //         blue-=10;
        //     } else{r = radius; blue = 255;}
        // }
        // else if(type == 3){
        //     if(r<60){
        //         g.setColor(new Color(255, 255, blue));
        //         g.fillOval((int)x-r, (int)y-r, 2*r, 2*r);
        //         blue -= 10;
        //     } else{r = radius; blue = 255;} 
        // }

        // timer -= 0.01;
        // if (timer <=0) {
        //     timer = timePerFrame;
        //     r += r*0.2;
        // }
        // System.out.println("x: "+x+", y: "+y);
    }
    
    public boolean isDead() {
        return exploded && explWaitTime <= 0;
    }
    
    public int getExpR(){
        return expR;
    }

}