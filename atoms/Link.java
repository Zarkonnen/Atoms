package atoms;

public class Link {
	final Atom a;
	final Atom b;
	final double flipSquared;
	final double strength;
	final double snapDSquared;
	final int gridSize;

	public Link(Atom a, Atom b, double flip, double strength, double snapD, int gridSize) {
		this.a = a;
		this.b = b;
		this.flipSquared = flip * flip;
		this.strength = strength;
		this.snapDSquared = snapD * snapD;
		this.gridSize = gridSize;
	}

	boolean tick() {
		double xd = a.x - b.x;
		double yd = a.y - b.y;
		if (xd > gridSize / 2) {
			xd -= gridSize;
		}
		if (xd < -gridSize / 2) {
			xd += gridSize;
		}
		if (yd > gridSize / 2) {
			yd -= gridSize;
		}
		if (yd < -gridSize / 2) {
			yd += gridSize;
		}
		double sqD = xd * xd + yd * yd;
		if (sqD > snapDSquared) {
			return true;
		}
		if (sqD > flipSquared) {
			// They are pulling on one another.
			xd = xd * (sqD - flipSquared) / sqD * strength;
			yd = yd * (sqD - flipSquared) / sqD * strength;
			a.dx -= xd / a.mass;
			a.dy -= yd / a.mass;
			b.dx += xd / b.mass;
			b.dy += yd / b.mass;
		}
		return false;
	}
}
