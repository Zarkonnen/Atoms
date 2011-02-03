package atoms;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AtomThread implements Runnable {
	final Canvas c;
	final Atom[] atoms;
	final Link[] links;
	final int gridSize;
	final int squareSize;
	final int drawDiff;
	final double repulsionDistance;
	final double repulsionStrength;

	LinkedList<Long> msMeasures = new LinkedList<Long>();
	long lastTime;
	volatile boolean stop = false;

	public AtomThread(Canvas c, List<Atom> atoms, List<Link> links, int gridSize, int squareSize,
			int drawDiff, double repulsionDistance, double repulsionStrength)
	{
		this.c = c;
		this.atoms = atoms.toArray(new Atom[atoms.size()]);
		this.links = links.toArray(new Link[links.size()]);
		this.gridSize = gridSize;
		this.squareSize = squareSize;
		this.drawDiff = drawDiff;
		this.repulsionDistance = repulsionDistance;
		this.repulsionStrength = repulsionStrength;
		c.createBufferStrategy(2);
	}

	void links() {
		for (int i = 0; i < links.length; i++) {
			if (links[i] != null && links[i].tick()) { links[i] = null; }
		}
	}

	void movement() {
		for (int i = 0; i < atoms.length; i++) {
			Atom a = atoms[i];
			a.x = (a.x + a.dx + gridSize) % gridSize;
			a.y = (a.y + a.dy + gridSize) % gridSize;
		}
	}

	void draw() {
		long timeNow = System.currentTimeMillis();
		msMeasures.add(timeNow - lastTime);
		if (msMeasures.size() > 10) { msMeasures.poll(); }
		long total = 0;
		for (long msm : msMeasures) { total += msm; }
		total /= msMeasures.size();
		if (total > 0) {
			total = 1000 / total;
		}
		Graphics2D g = (Graphics2D) c.getBufferStrategy().getDrawGraphics();
		//g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		g.clearRect(0, 0, gridSize, gridSize);

		for (int i = 0; i < atoms.length; i++) {
			Atom at = atoms[i];
			g.setColor(at.col);
			g.fillRect((int) at.x, (int) at.y, 2, 2);
		}
		g.setColor(Color.WHITE);
		g.drawString(total + " visual /" + (total * drawDiff) + " physics FPS, " +
				atoms.length + " atoms", 20, 20);
		c.getBufferStrategy().show();
	}

	public void run() {
		SquaresThread sqt = new SquaresThread(atoms, gridSize, squareSize, 0,
				gridSize / squareSize, repulsionDistance, repulsionStrength);

		int tick = 0;
		lastTime = System.currentTimeMillis();
		while (!stop) {
			if (tick++ % drawDiff == 0) {
				draw();
				lastTime = System.currentTimeMillis();
			}
			sqt.forces();
			links();
			movement();
		}
	}
}
