

public abstract class PhysicsObject {
    
    protected double gravity = -0.2;
    private final double airRes = 2.0/3.0;
    
    protected double minVel = 0.2;
    
    private final double bounceMultiplier = 1.8/3.0;
    
    protected double x;
    protected double xVel;
    protected double y;
    protected double yVel;
    
    protected int width; // everything is rectangles woooo
    protected int height;
    
    protected int bounces = 0;
    protected boolean sticky = false;
    
    public PhysicsObject(int x, int y, double xv, double yv, int width, int height) {
        this.x = x;
        this.y = y;
        this.xVel = xv;
        this.yVel = yv;
        this.width = width;
        this.height = height;
    }
    public PhysicsObject(int x, int y, int width, int height) {
        this(x, y, 0, -1, width, height);
    }
    
    public void move(boolean[][] map) {
        // System.out.println("x: "+x+", y:"+y+", xv: "+xVel+", yv: "+yVel);
        double nx = x + xVel;
        double ny = y + yVel;
        if (checkNX(nx, map)) {
            // System.out.println("X bounced!!");
            bounces++;
            nx = x;
            xVel *= -bounceMultiplier;
            xVel *= Math.pow(airRes, bounces);
        }
        if (checkNY(ny, map)) {
            // System.out.println("Y bounced!!");
            bounces++;
            ny = y;
            yVel *= -bounceMultiplier;
            xVel *= Math.pow(airRes, bounces);
        }
        // check x TRUE = BOUNCED
        //check y
        x = nx;
        y = ny;
        
        yVel += gravity;
        if (Math.abs(yVel) < minVel)
            yVel = 0;
        if (Math.abs(xVel) < minVel)
            xVel = 0;
        
    }
    
    private boolean checkNX(double nx, boolean[][] map) {
        if (xVel == 0)
            return false;
        boolean touching = false;
        if (xVel > 0) {
            for (int c = Math.max((int)((x+width/2)/10), 0); c < Math.min(Math.max((int)((nx+width/2)/10), Math.max((int)((x+width/2)/10), 0) + 1),149); c++ ) {
                for (int r = Math.max((int)((y-height/2)/10), 0); r < Math.min(Math.max((int)((y+height/2)/10), Math.max((int)((y-height/2)/10), 0)+1),77); r++ ) {
                    if (map[r][c])
                        touching = true;
                }
            }
        } else {
            for (int c = Math.max((int)((nx-width/2)/10), 0); c < Math.min(Math.max((int)((x-width/2)/10), Math.max((int)((nx-width/2)/10), 0)+1),149); c++ ) {
                for (int r = Math.max((int)((y-height/2)/10), 0); r < Math.min(Math.max((int)((y+height/2)/10), Math.max((int)((y-height/2)/10), 0)+1),77); r++ ) {
                    if (map[r][c])
                        touching = true;
                }
            }
        }
        return touching;
    }
    
    private boolean checkNY(double ny, boolean[][] map) {
        if (yVel == 0)
            return false;
        boolean touching = false;
        if (yVel > 0) {
            for (int r = Math.max((int)((y+height/2)/10), 0); r < Math.min(Math.max((int)((ny+height/2)/10), Math.max((int)((y+height/2)/10), 0) + 1),77); r++ ) {
                for (int c = Math.max((int)((x-width/2)/10), 0); c < Math.min(Math.max((int)((x+width/2)/10), Math.max((int)((x-width/2)/10), 0)+1),149); c++ ) {
                    if (map[r][c])
                        touching = true;
                }
            }
        } else {
            for (int r = Math.max((int)((ny-height/2)/10), 0); r < Math.min(Math.max((int)((y-height/2)/10), Math.max((int)((ny-height/2)/10), 0)+1),77); r++ ) {
                for (int c = Math.max((int)((x-width/2)/10), 0); c < Math.min(Math.max((int)((x+width/2)/10), Math.max((int)((x-width/2)/10), 0)+1),149); c++ ) {
                    if (map[r][c])
                        touching = true;
                }
            }
        }
        return touching;
    }

    public int getX(){
        return (int)x;
    }

    public int getY(){
        return (int)y;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }
       
}
