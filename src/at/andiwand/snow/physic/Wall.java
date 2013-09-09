package at.andiwand.snow.physic;

import at.stefl.commons.math.vector.Vector2d;

public class Wall {

    private Vector2d start;
    private Vector2d direction;

    public Wall(Vector2d start, Vector2d direction) {
	this.start = start;
	this.direction = direction;
    }

    public Vector2d getStart() {
	return start;
    }

    public Vector2d getDirection() {
	return direction;
    }

    public boolean behind(Vector2d point) {
	return behind(point, 0);
    }

    public boolean behind(Vector2d middle, double radius) {
	Vector2d sm = middle.sub(start);
	return sm.dot(direction) < radius;
    }

}