import java.awt.Graphics;
import java.awt.Color;
import javax.swing.ImageIcon;

public class Player extends PhysicsObject{
    
    private boolean direction = true; // true = facing right
    
    private final int startHealth = 150;
    private final double timePerFrame = 0.5;
    private double timer = timePerFrame;
    
    private final double moveSpeed = 1.7;
    private final double jumpVel = 6.7;
    private boolean jumping;
    
    private boolean hurt = false;
    private boolean hurtFrame = false;
        
    private double health;
    private ImageIcon[] sprites = {new ImageIcon("images/maggot0.png"), new ImageIcon("images/maggot1.png"),
    new ImageIcon("images/maggot2.png"), new ImageIcon("images/maggot3.png"), new ImageIcon("images/maggot4.png"),
    new ImageIcon("images/maggot5.png"), new ImageIcon("images/maggot6.png")};
    private ImageIcon[] fsprites = {new ImageIcon("flip/maggot0.png"), new ImageIcon("flip/maggot1.png"),
    new ImageIcon("flip/maggot2.png"), new ImageIcon("flip/maggot3.png"), new ImageIcon("flip/maggot4.png"),
    new ImageIcon("flip/maggot5.png"), new ImageIcon("flip/maggot6.png")};
    private static final int pWidth = 49;
    private static final int pHeight = 58;

    private Color color;
    
    private int currImg = 0;
    
    private Weapon weapon;
    
    public Player(int x, int y, Color color) {
        super(x, y, pWidth, pHeight);
        health = startHealth;
        weapon = new Weapon(x, y, 1);
        currImg = 0;
        this.color = color;
    }
    
    public void move(boolean[][] map) {
        super.move(map);
        if (yVel < 3)
            jumping = false;
        weapon.move((int)x, (int)y);
        if (x > 1500 || x < 0 || y < 0)
            health = 0;
        
        if (hurt && yVel == 0 && xVel == 0) {
            hurt = false;
        }
    }
    
    public void projMove(boolean[][] map, Player[] players) {
        weapon.projMove(map, players);
    }
    
    public void projDraw(Graphics g) {
        weapon.projDraw(g);
    }
    
    public void moveLR(int dir) {
        // if (Math.abs(xVel) < moveSpeed)
        if (!jumping)
            this.xVel = moveSpeed*dir;
    }
    
    public void shoot(double power, double angle) {
        int sx = (int)x + (int)(Math.cos(angle) * (pHeight+1));
        int sy = (int)y + (int)(Math.sin(angle + Math.PI) * (pHeight+1));
        weapon.shoot(power, angle, sx, sy);
    }
    
    public void draw(Graphics g) {
        if (!isAlive()) {
            g.drawImage((direction ? sprites[4] : fsprites[4]).getImage(), (int)x-(pWidth/2), 780-(int)y-(pHeight/2), pWidth, pHeight, null);
            return;
        }
        if (hurt)
            g.drawImage((direction ? sprites[hurtFrame ? 2 : 3] : fsprites[hurtFrame ? 2 : 3]).getImage(), (int)x-(pWidth/2), 780-(int)y-(pHeight/2), pWidth, pHeight, null);
        else
            g.drawImage((direction ? sprites[currImg] : fsprites[currImg]).getImage(), (int)x-(pWidth/2), 780-(int)y-(pHeight/2), pWidth, pHeight, null);
        g.setColor(color);
        int[] xPoints = {(int)x-10, (int)x+10, (int)x};
        int[] yPoints = {(int)y*-1 + 720, (int)y*-1 + 720, (int)y*-1 + 732};
        g.fillPolygon(xPoints, yPoints, 3);
        
        g.setColor(Color.GRAY.brighter());
		g.fillRect((int)x-19, 704-(int)y, 36, 13);
		g.setColor(color);
		g.drawString("H:"+(int)health, (int)x-17, 715-(int)y);
        
        weapon.direction = direction;
        weapon.draw(g);
        
        if (hurt) {
            timer -= 0.01;
            if (timer <=0) {
                hurtFrame = !hurtFrame;
                timer = timePerFrame;
            }
        }
    }
    
    public boolean isHit(Projectile projectile) {
        
        //check if pojectile directly hits player
        double plaTop = y + (double)height/2;
        double plaBot = y - (double)height/2;
        double plaRight = x + (double)pWidth/2;
        double plaLeft = x - (double)pWidth/2;
        
        double pTop = projectile.getY() + (double)projectile.getHeight()/2;
        double pBot = projectile.getY() - (double)projectile.getHeight()/2;
        double pRight = projectile.getX() - (double)projectile.getWidth()/2;
        double pLeft = projectile.getX() - (double)projectile.getWidth()/2;
        
        //   (                 returns true if the projectile passes an edge of the player y-wise                ) && (returns true if the projectile is anywhere in the player x-wise)
        if ( !( ((plaTop <= pTop) == (plaBot <= pTop)) && ((plaTop >= pBot) == (plaBot >= pBot)) ) && ( (plaRight > pLeft) == (plaLeft < pRight))) {
            return true;
        }
        //   (                 returns true if the projectile passes an edge of the player x-wise                ) && (returns true if the projectile is anywhere in the player y-wise)
        if ( !( ((plaRight >= pLeft) == (plaLeft >= pLeft)) && ((plaRight >= pRight) == (plaLeft >= pRight)) ) && ( (plaBot > pTop) == (plaTop < pBot))) {
            return true;
        }
        return false;
    }
    
    public boolean inRadius(Projectile projectile) {
        // check if it hit general radius
        if (projectile.getExpR()/2 +pHeight-1> Math.sqrt(Math.pow(x-(double)projectile.getX(),2) + Math.pow(y-(double)projectile.getY(),2))) {
            return true;
        }
        return false;
    }
    
    public void getHit(int damage, double xV, double yV) {
        health -= damage;
        // System.out.println("xv: "+xV+", yv: "+yV);
        xVel += xV;
        yVel += yV;
        bounces = -1;
    }
    
    public boolean isAlive() {
        return (int)health > 0;
    }
    
    public int getHealth() {
        // System.out.println("LIVE CHECK LIVE CHECK");
        return (int)health;
    }
    
    public void setWeapon(int w) {
        weapon.setType(w);
    }
    
    public int getX() {
        return (int)x;
    }
    public int getY() {
        return (int)y;
    }
    public void moveY(int ny) {
        y += ny;
    }
    public void setDir(boolean dir) {
        direction = dir;
    }
    public void jump() {
        jumping = true;
        yVel = jumpVel;
        if (xVel == 0)
            yVel *= 1.2;
        else
            xVel *= 1.8;
        bounces = 0;
    }
    
    public boolean falling() {
        return Math.abs(yVel) > minVel;
    }
    
    public boolean isMoving() {
        return xVel > minVel || yVel > minVel || weapon.projMoving() && jumping == false;
    }
    
    public void newTurn() {
        weapon.newTurn();
    }
}