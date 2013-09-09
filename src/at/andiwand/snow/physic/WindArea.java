package at.andiwand.snow.physic;

import at.stefl.commons.math.vector.Vector2d;

public class WindArea {

    private Vector2d middle;
    private double radius;
    private Vector2d force;

    public WindArea(double radius) {
	this(Vector2d.NULL, radius, Vector2d.NULL);
    }

    public WindArea(Vector2d middle, double radius, Vector2d force) {
	this.middle = middle;
	this.radius = radius;
	this.force = force;
    }

    public Vector2d getMiddle() {
	return middle;
    }

    public double getRadius() {
	return radius;
    }

    public Vector2d getForce() {
	return force;
    }

    public void setMiddle(Vector2d middle) {
	this.middle = middle;
    }

    public void setRadius(double radius) {
	this.radius = radius;
    }

    public void setForce(Vector2d force) {
	this.force = force;
    }

    public boolean contains(Particle particle) {
	Vector2d distance = particle.getPosition().sub(middle);

	return distance.length() < radius;
    }

}