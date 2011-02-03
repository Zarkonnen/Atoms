package atoms;

import java.awt.Color;

public class Atom {
	double x;
	double y;
	double dx;
	double dy;
	final double mass;
	Color col = Color.WHITE;
	int num = 0;

	public Atom(int num, double x, double y, double dx, double dy, double mass) {
		this.num = num;
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.mass = mass;
	}
}
