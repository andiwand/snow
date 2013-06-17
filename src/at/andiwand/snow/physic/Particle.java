package at.andiwand.snow.physic;

import at.andiwand.commons.math.vector.Vector2d;

public class Particle {

    private final double mass;
    private Vector2d middle;
    private final double radius;
    private Vector2d velocity;

    private double maxVelocity = Double.NaN;

    private Vector2d forceCache = new Vector2d();

    public Particle(double mass, double radius) {
	this(mass, new Vector2d(), radius);
    }

    public Particle(double mass, Vector2d middle, double radius) {
	this(mass, middle, radius, new Vector2d());
    }

    public Particle(double mass, Vector2d middle, double radius,
	    Vector2d velocity) {
	this.mass = mass;
	this.middle = middle;
	this.radius = radius;
	this.velocity = velocity;
    }

    public double getMass() {
	return mass;
    }

    public Vector2d getPosition() {
	return middle;
    }

    public double getRadius() {
	return radius;
    }

    public Vector2d getVelocity() {
	return velocity;
    }

    public void setPosition(Vector2d position) {
	this.middle = position;
    }

    public void setVelocity(Vector2d velocity) {
	this.velocity = velocity;
    }

    public void setMaxVelocity(double maxVelocity) {
	this.maxVelocity = maxVelocity;
    }

    public void addForce(Vector2d force) {
	forceCache = forceCache.add(force);
    }

    public void clearForceCache() {
	forceCache = Vector2d.NULL;
    }

    public void update(double delta) {
	Vector2d acceleration = forceCache.div(mass);

	middle = middle.add(velocity.mul(delta)).add(
		acceleration.mul(delta * delta / 2));
	velocity = velocity.add(acceleration.div(mass).mul(delta));

	if ((maxVelocity == maxVelocity) && (velocity.length() > maxVelocity))
	    velocity = velocity.normalize().mul(maxVelocity);

	clearForceCache();
    }

    public Vector2d calcNewPosition(double delta) {
	Vector2d acceleration = forceCache.div(mass);

	return middle.add(velocity.mul(delta)).add(
		acceleration.mul(delta * delta / 2));
    }

}