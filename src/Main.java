//	JumpingBall import statements
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame; 
import javax.swing.JPanel;
import javax.swing.Timer;

//	MouseKeyInput imports
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseMotionListener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

//	Maggot imports
// import java.util.Scanner;

@SuppressWarnings("serial")
public class Main extends JPanel {
	
	//don't forget to change size in Maps.java as well
	private static final int WIDTH = 1500;
	private static final int HEIGHT = 780;
	
	public static final Color cursorColor = new Color(0, 115, 255, 100);
	static final int cursorSize1 = 16;
	static final int cursorSize2 = 14;
	int cursorX = WIDTH / 2;
	int cursorY = HEIGHT / 2;

	private BufferedImage image;
	private Graphics g;
	public Timer timer;
//	private Music music;
//	private Player player;
//	private Enemy[] enemies;
//	private boolean[] liveEnemies;
	private boolean[][] map;
	
	private static JFrame frame;
		
	int turn = 0;
	final Color[] playerColors = {Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN};
	static int numPlayers=2; //TODO CHANGE BACK
	Player[] players;
	
	final double playerMoveTime = 2.0;
	double pMoveTimer = playerMoveTime;
	boolean playerMovingR = false;
	boolean playerMovingL = false;
	final double playerJumpCost = 0.48;
	boolean playerPressJump = false;
	boolean playerPressShoot = false;
	
	double power = 0.5;
	boolean powerL = false;
	boolean powerR = false;
	double angle = 0.0;
	
	int phase = 0;
	double ttimer;
	boolean debug = false;
		
	double dt = 0.01; // in milliseconds, multiply by 1000 to get seconds
	
	boolean setupDone = false;
	
	boolean penDown = false;
	boolean pen = true;
	
	boolean allAlive = true;;
	
	public Main() {
		//	Old stuff
		image =  new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = image.getGraphics();
		timer = new Timer((int)(dt * 1000), new TimerListener());
		timer.start();
		
		//	MouseKeyInput stuff
		addMouseListener(new Mouse());
		addMouseMotionListener(new MouseMotion());
		addMouseWheelListener(new MouseWheel());
		addKeyListener(new Keyboard());
		setFocusable(true);
		
		setupDone = true;
	}
	
	//	Old classes & methods
	
	private class TimerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (setupDone) { // quick check 
			// phases:
			// 0 = Console input asking for number of players
			// 1 = Player moving, changing weapons, aiming, throwing power timing whetevr
			// 2 = Projectile moving, physics happening
			// 3 = Game over screen
			// 4 = map creator
			
				if (phase == 0) {
					newMap();
				} else if (phase == 1) {
					turnPhase();
				} else if (phase == 2) {
					physicsPhase();
				} else if (phase == 3) {
					g.drawImage(new ImageIcon("images/win"+(players[0].isAlive() ? "0" : "1")+".png").getImage(), 430, 230, null);
					repaint();
				} else if (phase == 4) {
					mapCreate();
				}
			}
			
		}
		
	}
	
	private void newMap() {
		// System.out.println("new map phase");
		map = Maps.newMap();
		// System.out.println("map made ");
		players = new Player[numPlayers];
		//	TODO: initialize players if we have more than 2
		
		players[0] = new Player(WIDTH/4, HEIGHT, playerColors[0]);
		players[1] = new Player(3*WIDTH/4, HEIGHT, playerColors[1]);
		for (int i = 0; i < numPlayers; i++) {
			while (!map[players[i].getY()/10 - 10][players[i].getX()/10])
				players[i].moveY(-10);
		}
		// System.out.println("players initialized");
		
		phase = 1;
	}
	
	private void turnPhase() {
		// System.out.println("turn phase");
		
		
		if ((playerMovingL || playerMovingR) && pMoveTimer > 0) {
			players[turn].moveLR(playerMovingL ? -1 : 1);
			pMoveTimer -= dt;
		}
		if (playerPressJump && !players[turn].falling() && pMoveTimer > playerJumpCost) {
			playerPressJump = false;
			players[turn].jump();
			pMoveTimer -= playerJumpCost;
		}
		if (powerL ^ powerR) {
			if (powerL && power >0)
				power = Math.max(power-0.025,0.0);
			else if (power <10)
				power = Math.min(power+0.025,1.0);
		}
		
		for (int i = 0; i < numPlayers; i++) {
			players[i].move(map);
		}
		
		players[turn].setDir(cursorX > players[turn].getX());
		drawEverything();
		
		for (Player p : players) {
			if (!p.isAlive())
				allAlive = false;
		}
		
		if (!allAlive) { //TODO add check to see if one player is alive
			phase = 3;
			// System.out.println("HE DEAD");
		}
		if (playerPressShoot) {
			players[turn].shoot(power, Math.atan2(780-players[turn].getY() - cursorY, players[turn].getX() - cursorX) + Math.PI);
			
			playerPressShoot = false;
			pMoveTimer = 0;
			phase = 2;
		}
	}
	
	private void physicsPhase() {
		// System.out.println("physics phase");
		
		for (int i = 0; i < numPlayers; i++) {
			players[i].move(map);
		}
		players[turn].projMove(map, players);
		
		
		drawEverything();
		boolean isMoving = false;
		for (Player p : players) {
			if (p.isMoving())
				isMoving = true;
			if (!p.isAlive())
				allAlive = false;
		}
		if (!allAlive) { //TODO add check to see if only one player is alive
			phase = 3;
			// System.out.println("HE DEAD");
		} else if (!isMoving) { 
			players[turn].newTurn();
			do {
				turn = (turn+1) % (numPlayers);
			} while (players[turn].getHealth() <= 0);
			// turn++;
			phase = 1;
			power = 0.5;
			pMoveTimer = playerMoveTime;
			playerMovingL = false;
			playerMovingR = false;
			playerPressJump = false;
		}
	}
	
	private void mapCreate() {
		Maps.drawMap(g, map);
		drawCursor();
		repaint();
	}
	
	private void drawEverything() {
		
		// Draw map
		Maps.drawMap(g, map);
		
		for (int i = 0; i < numPlayers; i++) {
			players[i].draw(g);
		}
		if (phase == 1) {
			g.setColor(Color.GRAY.brighter());
			g.fillRect(players[turn].getX()-17, 684-players[turn].getY(), 32, 13);
			g.setColor(playerColors[turn]);
			g.drawString("M:"+(int)(pMoveTimer *10), players[turn].getX()-14, 695-players[turn].getY());
			
			g.setColor(Color.GRAY.brighter());
			g.fillRect(players[turn].getX()-21, 739-players[turn].getY(), 42, 12);
			g.setColor(playerColors[turn]);
			g.fillRect(players[turn].getX()-20, 740-players[turn].getY(), (int)(power*40), 10);
		} else if (phase == 2) {
			players[turn].projDraw(g);
		}
		
		g.setColor(Color.GRAY.brighter());
		g.fillRect(players[turn].getX()-19, 704-players[turn].getY(), 36, 13);
		g.setColor(playerColors[turn]);
		g.drawString("H:"+(int)(players[turn].getHealth()), players[turn].getX()-17, 715-players[turn].getY());
		
		
		drawCursor();
		
		repaint();
	}
	
	
	
	public void drawCursor() {
		//TODO: update cursor icon??
		g.setColor(Color.WHITE);
		g.fillOval(cursorX - (cursorSize1 / 2) - 2, cursorY - (cursorSize1 / 2) - 2, cursorSize1 + 4, cursorSize1 + 4);
		g.setColor(playerColors[turn]);
		g.fillOval(cursorX - (cursorSize1 / 2), cursorY - (cursorSize1 / 2), cursorSize1, cursorSize1);
		
		if (phase == 1) {
			g.setColor(new Color(55, 55, 55, 128));
			// if (Math.sqrt(Math.pow(players[turn].getX() - cursorX, 2) + Math.pow(players[turn].getY() - cursorY+10, 2)) > 100) {
				for (int i = 3; i <= 7; i++) {
					g.fillOval(players[turn].getX() + (int)(((100*Math.cos(angle))/7) * i), 780-players[turn].getY()+(int)(((100*Math.sin(angle))/6) * i), 10, 10);
				}
			// }
		}
	}

	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	}

	public static void main(String[] args) {
		// Scanner sc = new Scanner(System.in);
		// String playrs;
		// while (true) {
		// 	System.out.print("\nEnter number of players: ");
		// 	playrs = sc.next();
		// 	if (new String("234").contains(playrs)) {
		// 		break;
		// 	}
		// 	System.out.println("Please enter a number from 2 to 4");
		// }
		// sc.close();
		// numPlayers = Integer.parseInt(playrs.substring(0, 1));
		//TODO reintroduce
		frame = new JFrame("Maggots");
		frame.setCursor( frame.getToolkit().createCustomCursor(new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB ),new Point(),null ) );
		frame.setSize(WIDTH + 18, HEIGHT + 47);
		frame.setLocation(0, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new Main()); 
		frame.setVisible(true);
	}
	
	
	
	
	
	
	private class Mouse implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!setupDone) {
				return;
			}
			if (phase == 1 && playerPressShoot == false) {
				playerPressShoot = true;
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		
	}
	
	private class MouseMotion implements MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {
			cursorX = e.getX();
			cursorY = e.getY();
			if (debug) {
				//g.setColor(Color.WHITE);
				//g.fillOval(e.getX()-3, e.getY()-3, 6, 6);
				//System.out.println("Mouse distance: " + player.distanceFromTank(e.getX(), e.getY()) + ", Origin distance: " + player.distanceFromTank(WIDTH / 2, HEIGHT / 2));
			}
			angle = Math.atan2(780-players[turn].getY() - cursorY, players[turn].getX() - cursorX) + Math.PI;
			
			if (phase == 4 && penDown) {
				map[Math.max(Math.min(78-e.getY()/10,77),0)][Math.max(Math.min(e.getX()/10,149),0)] = pen;
			}
		}
		
	}
	
	private class MouseWheel implements MouseWheelListener {

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			
		}
		
	}
	
	private class Keyboard implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {
			if (phase == 1) {
				if (e.getKeyCode() == KeyEvent.VK_1) {
					players[turn].setWeapon(1);
				} else if (e.getKeyCode() == KeyEvent.VK_2) {
					players[turn].setWeapon(2);
				} else if (e.getKeyCode() == KeyEvent.VK_3) {
					players[turn].setWeapon(3);
				}
				
				
				if (e.getKeyCode() == KeyEvent.VK_D) {
					playerMovingR = true;
				} else if (e.getKeyCode() == KeyEvent.VK_A) {
					playerMovingL = true;
				}
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					playerPressJump = true;
				}
				
				
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					powerR = true;
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					powerL = true;
				}
			}
			
			// map editor code
			if (phase == 4) {
				if (e.getKeyCode() == KeyEvent.VK_P) {
					System.out.print("{");
					for (boolean[] r : map) {
						System.out.print("{");
						for (boolean c : r) {
							System.out.print(c+", ");
						}
						System.out.print("}\n");
					}
					System.out.print("{");
				}
				if (e.getKeyCode() == KeyEvent.VK_Z) {
					penDown = !penDown;
				}
				if (e.getKeyCode() == KeyEvent.VK_X) {
					pen = !pen;
				}
				if (e.getKeyCode() == KeyEvent.VK_E) {
					for (int r = 0; r < map.length; r++) for (int c = 0; c < map[r].length; c++) map[r][c] = false;
				}
			}
			
			// M toggles debug mode, N toggles the player's debug mode
			if (e.getKeyCode() == KeyEvent.VK_M) {
				debug = !debug;
			}
			if (e.getKeyCode() == KeyEvent.VK_N) {
				// player.toggleDebug();
			}
			
			// cheat keycodes to speed up development
			if (e.getKeyCode() == KeyEvent.VK_G && debug) {
				//immediate game over
				// phase = 4;
			}
			if (e.getKeyCode() == KeyEvent.VK_C && debug) {
				// mission cleared
				// ttimer = gameplayPostviewTime;
				// phase = 3;
			}

			if (e.getKeyCode() == KeyEvent.VK_1 && debug) {
				
			}
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			
			if (phase == 1) {
				if (e.getKeyCode() == KeyEvent.VK_D) {
					playerMovingR = false;
				} else if (e.getKeyCode() == KeyEvent.VK_A) {
					playerMovingL = false;
				}
				
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					powerR = false;
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					powerL = false;
				}
			}
			
			if (e.getKeyCode() == KeyEvent.VK_W) {
				// player.setMovement(0, false);
			}
			if (e.getKeyCode() == KeyEvent.VK_D) {
				// player.setMovement(1, false);
			}
			if (e.getKeyCode() == KeyEvent.VK_S) {
				// player.setMovement(2, false);
			}
			if (e.getKeyCode() == KeyEvent.VK_A) {
				// player.setMovement(3, false);
			}
		}
		
	}
	
}
