package at.andiwand.snow.physic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import at.andiwand.commons.math.vector.Vector2d;

public class Physics extends Thread {

    private List<Wall> walls = new ArrayList<Wall>();
    private List<Particle> particles = new ArrayList<Particle>();
    private List<Link> links = new ArrayList<Link>();
    private Vector2d gravity = Vector2d.NULL;
    private List<WindArea> windAreas = new ArrayList<WindArea>();
    private long last;

    public List<Particle> getParticles() {
	return new ArrayList<Particle>(particles);
    }

    public Vector2d getGravity() {
	return gravity;
    }

    public void setGravity(Vector2d gravity) {
	this.gravity = gravity;
    }

    public void addWall(Wall wall) {
	synchronized (walls) {
	    walls.add(wall);
	}
    }

    public void addParticle(Particle particle) {
	synchronized (particles) {
	    synchronized (links) {
		for (Particle p : particles) {
		    links.add(new Link(particle, p));
		}
	    }

	    particles.add(particle);
	}
    }

    public void addWindArea(WindArea windArea) {
	synchronized (windAreas) {
	    windAreas.add(windArea);
	}
    }

    public void removeWall(Wall wall) {
	synchronized (walls) {
	    walls.remove(wall);
	}
    }

    public void removeParticle(Particle particle) {
	synchronized (particles) {
	    synchronized (links) {
		for (Link link : new HashSet<Link>(links)) {
		    if ((link.getParticleA() == particle)
			    || (link.getParticleB() == particle))
			links.remove(link);
		}
	    }

	    particles.remove(particle);
	}
    }

    public void removeWindArea(WindArea windArea) {
	synchronized (windAreas) {
	    windAreas.remove(windArea);
	}
    }

    public void run() {
	last = System.nanoTime();

	try {
	    while (true) {
		Thread.sleep(1);

		long now = System.nanoTime();
		double delta = (now - last) / 1000000000d;
		last = now;

		synchronized (particles) {
		    synchronized (windAreas) {
			for (Particle particle : particles) {
			    particle.addForce(gravity);

			    for (WindArea windArea : windAreas) {
				if (windArea.contains(particle)) {
				    double distance = particle.getPosition()
					    .sub(windArea.getMiddle()).length();
				    double forceFactor = 1 - distance
					    / windArea.getRadius();
				    Vector2d force = windArea.getForce();
				    force = force.mul(forceFactor);

				    particle.addForce(force);
				}
			    }
			}
		    }

		    synchronized (links) {
			for (Link link : links) {
			    link.update();
			}
		    }

		    handleWallCollision();

		    for (Particle particle : particles) {
			particle.update(delta);
		    }
		}
	    }
	} catch (InterruptedException e) {
	}
    }

    private void handleWallCollision() {
	synchronized (walls) {
	    synchronized (particles) {
		for (Wall wall : walls) {
		    for (Particle particle : particles) {
			Vector2d position = particle.getPosition();
			double radius = particle.getRadius();
			Vector2d velocity = particle.getVelocity();

			if (!wall.behind(position, radius))
			    continue;

			Vector2d sp = position.sub(wall.getStart());

			double distance = wall.getDirection().dot(sp)
				/ wall.getDirection().length() - radius;
			position = position.add(wall.getDirection().normalize()
				.mul(Math.abs(distance)));

			if (wall instanceof StickyWall) {
			    particle.setPosition(position);
			    particle.setVelocity(Vector2d.NULL);
			    particle.clearForceCache();
			    continue;
			}

			double wallVelocity = wall.getDirection().dot(velocity)
				/ wall.getDirection().length();
			velocity = velocity.add(wall.getDirection().normalize()
				.mul(Math.abs(wallVelocity * 1.1)));

			if (Math.abs(velocity.getX()) < 1)
			    velocity = velocity.setX(0);

			if (Math.abs(velocity.getY()) < 1)
			    velocity = velocity.setY(0);

			particle.setPosition(position);
			particle.setVelocity(velocity);
			particle.clearForceCache();
		    }
		}
	    }
	}
    }

}