package OnePlayerCasualMode;

import GUI.Difficultyselected;
import GUI.Endinglosscasualmode;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;


public class Game extends Canvas implements Runnable,KeyListener {
	
	private static final long serialVersionUID = 1L; 
	private boolean isRunning = false;
	
	public static final int WIDTH = 800,HEIGTH = 640;
	public static final String TITLE = "Chasing-Game-SKE";
	
	private Thread thread;
	
	public static Player player;
	public static Bot enemy;
	public static SmallItem small;
	public static Level level;
	public static BotSheet enemySheet;
	public static BotSheet playerSheet;
        public static GUI.Difficultyselected resultCS;
        public static JFrame frame;
	
        Music song = TinySound.loadMusic("chasing-game.wav");
        
	public Game() {
		Dimension dimension = new Dimension(WIDTH, HEIGTH);
		setPreferredSize(dimension);
		setMinimumSize(dimension);
		setMaximumSize(dimension);
		
		addKeyListener(this);
		player = new Player(Game.WIDTH/2,Game.HEIGTH/2);
		enemy = new Bot(Game.WIDTH/2,Game.HEIGTH/2);
		level = new Level("/map/map_chasing2.png");
		enemySheet = new BotSheet("/bot/tui.png");
		playerSheet = new BotSheet("/bot/banana2.png");
                resultCS = new Difficultyselected();
                frame = new JFrame();
	}
	
	public synchronized void start(){
		if(isRunning) return;
		isRunning = true;
		
		thread = new Thread(this);
		thread.start();
                song.play(true); 
	}
	
	public synchronized void stop(){
                frame.dispose();
		if(!isRunning){
                    return;
                }
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
                
                
	}
	
	private void tick(){
		player.tick();
		level.tick();
	}
	
	private void render(){
		BufferStrategy bs = getBufferStrategy();
		if(bs == null){
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, Game.WIDTH, Game.HEIGTH);
		player.render(g);
		level.render(g);
		g.dispose();
		bs.show();
	}
	
	public static void main() {
		Game game = new Game();
		frame = new JFrame();
		frame.setTitle(Game.TITLE);
		frame.add(game);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);
		game.start();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT)player.rigth = true;
		if(e.getKeyCode() == KeyEvent.VK_LEFT)player.left = true;
		if(e.getKeyCode() == KeyEvent.VK_UP)player.up = true;
		if(e.getKeyCode() == KeyEvent.VK_DOWN)player.down = true;
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(1);
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT)player.rigth = false;
		if(e.getKeyCode() == KeyEvent.VK_LEFT)player.left = false;
		if(e.getKeyCode() == KeyEvent.VK_UP)player.up = false;
		if(e.getKeyCode() == KeyEvent.VK_DOWN)player.down = false;
		
	}
	
	@Override
	public void run() {
		requestFocus();
		int fps = 0;
		double timer = System.currentTimeMillis();
		long lastTime = System.nanoTime();
		double targetTick = 60.0;
		double delta = 0;
		double ns = 1000000000 / targetTick; 
		
		while(isRunning){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			
			while(delta >= 1){
				tick();
				render();
                                if(resultCS.getResult() == 1) break;
				fps++;
				delta--;
			}
			if(resultCS.getResult() == 1) break;
			if(System.currentTimeMillis() - timer >= 1000){
				System.out.println(fps);
				fps = 0;
				timer += 1000;
			}
		}
                song.stop();
                new Endinglosscasualmode().setVisible(true);
              
                Sound coin = TinySound.loadSound("win.wav");
                for (int i = 0; i < 2; i++) {
			coin.play();
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {}
		}
		//be sure to shutdown TinySound when done
		
		stop();
                
	}
}