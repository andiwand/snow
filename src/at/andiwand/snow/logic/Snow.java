package at.andiwand.snow.logic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

import at.andiwand.commons.graphics.GraphicsUtil;
import at.andiwand.commons.math.vector.Vector2d;
import at.andiwand.snow.physic.Particle;
import at.andiwand.snow.physic.Physics;
import at.andiwand.snow.physic.StickyWall;
import at.andiwand.snow.physic.Wall;
import at.andiwand.snow.physic.WindArea;

public class Snow extends JComponent {

    // TODO: implement "build" mode
    // TODO: implement fixed angle link

    private static final long serialVersionUID = -4711280507701020643L;

    private static final double GRAVITY = 20;
    private static final double WIND_RADIUS = 100;
    private static final double WIND_FORCE_FACTOR = 100;
    private static final double PARTICLE_MASS = 1;
    private static final double PARTICLE_RADIUS = 3;
    private static final double PARTICLE_MAX_VELOCITY = 60;
    private static final double PARTICLE_START_VELOCITY = 40;

    private static final int NONE = 0;
    private static final int MOUSE_WIND = 1;

    private final Physics physics = new Physics();
    private final Random random = new Random();

    private Vector2d mouse;
    private int mode;
    private WindArea windArea = new WindArea(WIND_RADIUS);

    private StickyWall bottom = new StickyWall(Vector2d.NULL, Vector2d.NULL);
    private Wall left = new Wall(Vector2d.NULL, Vector2d.NULL);
    private Wall right = new Wall(Vector2d.NULL, Vector2d.NULL);

    private ComponentAdapter componentAdapter = new ComponentAdapter() {
	public void componentResized(ComponentEvent e) {
	    physics.removeWall(bottom);
	    physics.removeWall(left);
	    physics.removeWall(right);

	    bottom = new StickyWall(new Vector2d(0, getHeight()), new Vector2d(
		    0, -1));
	    left = new Wall(new Vector2d(0, getHeight()), new Vector2d(1, 0));
	    right = new Wall(new Vector2d(getWidth(), getHeight()),
		    new Vector2d(-1, 0));

	    physics.addWall(bottom);
	    physics.addWall(left);
	    physics.addWall(right);
	}
    };

    private MouseAdapter mouseAdapter = new MouseAdapter() {
	public void mousePressed(MouseEvent e) {
	    mouse = new Vector2d(e.getPoint());
	    mode = MOUSE_WIND;
	    mouseDragged(e);
	}

	public void mouseDragged(MouseEvent e) {
	    Vector2d newMouse = new Vector2d(e.getPoint());
	    Vector2d difference = newMouse.sub(mouse);

	    if ((mode & MOUSE_WIND) != 0) {
		windArea.setMiddle(newMouse);
		windArea.setForce(difference.mul(WIND_FORCE_FACTOR));
	    }

	    mouse = newMouse;
	}

	public void mouseReleased(MouseEvent e) {
	    windArea.setForce(Vector2d.NULL);

	    mode = NONE;
	}
    };

    public Snow() {
	physics.setGravity(new Vector2d(0, GRAVITY));
	physics.start();

	new Thread() {
	    public void run() {
		try {
		    while (true) {
			newRandomParticle();

			Thread.sleep(50);
		    }
		} catch (InterruptedException e) {
		}
	    }
	}.start();

	physics.addWindArea(windArea);

	addComponentListener(componentAdapter);

	addMouseListener(mouseAdapter);
	addMouseMotionListener(mouseAdapter);
    }

    public void newRandomParticle() {
	Particle particle = new Particle(PARTICLE_MASS, PARTICLE_RADIUS);
	particle.setMaxVelocity(PARTICLE_MAX_VELOCITY);

	double x = particle.getRadius() + random.nextDouble()
		* (getWidth() - particle.getRadius() * 2);
	particle.setPosition(new Vector2d(x, -particle.getRadius()));
	particle.setVelocity(new Vector2d(0, PARTICLE_START_VELOCITY));

	physics.addParticle(particle);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
	Graphics2D g = (Graphics2D) graphics;
	GraphicsUtil graphicsUtil = new GraphicsUtil(g);

	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);

	g.setBackground(new Color(0.7f, 0.7f, 1f));
	g.clearRect(0, 0, getWidth(), getHeight());

	g.setColor(Color.WHITE);
	for (Particle particle : physics.getParticles()) {
	    graphicsUtil.fillCircle(particle.getPosition(),
		    particle.getRadius() + 2);
	}

	// if ((mode & MOUSE_WIND) != 0) {
	// g.setColor(Color.BLACK);
	// graphicsUtil.drawCircle(windArea.getMiddle(), windArea.getRadius());
	// }

	try {
	    Thread.sleep(10);
	} catch (InterruptedException e) {
	}

	repaint();
    }

    public static void main(String[] args) {
	JFrame frame = new JFrame("Snow World");

	Snow snowWorld = new Snow();

	frame.add(snowWorld);

	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(600, 600);
	frame.setLocationRelativeTo(null);
	frame.setVisible(true);

	snowWorld.requestFocus();
    }

}