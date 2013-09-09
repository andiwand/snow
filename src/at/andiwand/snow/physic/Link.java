package at.andiwand.snow.physic;

import at.stefl.commons.math.vector.Vector2d;

public class Link {

    private Particle a;
    private Particle b;

    private double springFactor = 10;
    private double dumping = 2;

    public Link(Particle particleA, Particle particleB) {
	this.a = particleA;
	this.b = particleB;
    }

    public Particle getParticleA() {
	return a;
    }

    public Particle getParticleB() {
	return b;
    }

    public void update() {
	Vector2d ab = b.getPosition().sub(a.getPosition());
	Vector2d direction = ab.normalize();
	double length = ab.length();

	if (length > (a.getRadius() + b.getRadius()) * 1.5)
	    return;

	double springForceA = -springFactor
		* ((a.getRadius() + b.getRadius()) - length) / 2;
	double springForceB = -springForceA;

	double springVelocityA = a.getVelocity().dot(direction);
	double springVelocityB = b.getVelocity().dot(direction);
	double springVelocity = 0;
	if (Math.signum(springVelocityA) == Math.signum(springVelocityB)) {
	    springVelocity = Math.min(Math.abs(springVelocityA),
		    Math.abs(springVelocityB))
		    * Math.signum(springVelocityA);
	}

	double dampingForceA = -dumping * (springVelocityA - springVelocity);
	double dampingForceB = -dumping * (springVelocityB - springVelocity);

	double forceA = springForceA + dampingForceA;
	double forceB = springForceB + dampingForceB;

	a.addForce(direction.mul(forceA));
	b.addForce(direction.mul(forceB));

	if (ab.length() >= (a.getRadius() + b.getRadius()))
	    return;

	double radialA = direction.dot(a.getVelocity());
	double radialB = direction.dot(b.getVelocity());

	double newMiddle = 2 * ((a.getMass() * radialA + b.getMass() * radialB) / (a
		.getMass() + b.getMass()));
	double newRadialA = newMiddle - radialA;
	double newRadialB = newMiddle - radialB;

	Vector2d newVelocityA = a.getVelocity().add(
		direction.mul(newRadialA - radialA));
	Vector2d newVelocityB = b.getVelocity().add(
		direction.mul(newRadialB - radialB));

	// newVelocityA = newVelocityA.mul(0.9);
	// newVelocityB = newVelocityB.mul(0.9);

	if (Math.abs(newVelocityA.getX()) < 1)
	    newVelocityA = newVelocityA.setX(0);
	if (Math.abs(newVelocityA.getY()) < 1)
	    newVelocityA = newVelocityA.setY(0);

	if (Math.abs(newVelocityB.getX()) < 1)
	    newVelocityB = newVelocityB.setX(0);
	if (Math.abs(newVelocityB.getY()) < 1)
	    newVelocityB = newVelocityB.setY(0);

	a.setVelocity(newVelocityA);
	b.setVelocity(newVelocityB);

	Vector2d middle = a.getPosition().add(b.getPosition()).div(2);
	Vector2d newA = middle.sub(direction.mul(a.getRadius()));
	Vector2d newB = middle.add(direction.mul(b.getRadius()));

	a.setPosition(newA);
	b.setPosition(newB);
    }

}