package atoms;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;

public class AtomThread implements Runnable {
	final Canvas c;
	final Atom[] atoms;
	final Link[] links;
	final int gridSize;
	final int squareSize;
	final int drawDiff;
	final double repulsionDistance;
	final double repulsionStrength;
	final int numTicks;

	LinkedList<Long> msMeasures = new LinkedList<Long>();
	long lastTime;
	volatile boolean stop = false;
	final File saveToFolder;
	final String filePrefix;
	int tick = 0;

	public AtomThread(Canvas c, List<Atom> atoms, List<Link> links, int gridSize, int squareSize,
			int drawDiff, double repulsionDistance, double repulsionStrength, int numTicks,
			File saveToFolder, String filePrefix)
	{
		this.c = c;
		this.atoms = atoms.toArray(new Atom[atoms.size()]);
		this.links = links.toArray(new Link[links.size()]);
		this.gridSize = gridSize;
		this.squareSize = squareSize;
		this.drawDiff = drawDiff;
		this.repulsionDistance = repulsionDistance;
		this.repulsionStrength = repulsionStrength;
		this.numTicks = numTicks;
		this.saveToFolder = saveToFolder;
		this.filePrefix = filePrefix;
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
		if (saveToFolder != null) {
			drawToImage();
			if (tick % (drawDiff * 100) == 0) { drawToScreen(); }
		} else {
			drawToScreen();
		}
	}
	
	void drawToScreen() {
		draw((Graphics2D) c.getBufferStrategy().getDrawGraphics());
		c.getBufferStrategy().show();
	}
	
	void drawToImage() {
		BufferedImage img = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);
		draw(img.createGraphics());
		try {
			ImageIO.write(img, "png", new File(saveToFolder, filePrefix + (tick / drawDiff) + ".png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void draw(Graphics2D g) {
		long timeNow = System.currentTimeMillis();
		msMeasures.add(timeNow - lastTime);
		if (msMeasures.size() > 10) { msMeasures.poll(); }
		long total = 0;
		for (long msm : msMeasures) { total += msm; }
		total /= msMeasures.size();
		if (total > 0) {
			total = 1000 / total;
		}
		//g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		g.clearRect(0, 0, gridSize, gridSize);

		/*g.setColor(Color.LIGHT_GRAY);
		for (Link l : links) {
			if (l == null) { continue; }
			g.drawLine((int) l.a.x, (int) l.a.y, (int) l.b.x, (int) l.b.y);
		}*/
		for (int i = 0; i < atoms.length; i++) {
			Atom at = atoms[i];
			g.setColor(at.col);
			g.fillRect((int) at.x, (int) at.y, 2, 2);
		}
		g.setColor(Color.WHITE);
		g.drawString(total + " visual /" + (total * drawDiff) + " physics FPS, " +
				atoms.length + " atoms", 20, 20);
	}

	public void run() {
		SquaresThread sqt = new SquaresThread(atoms, gridSize, squareSize, 0,
				gridSize / squareSize, repulsionDistance, repulsionStrength);

		lastTime = System.currentTimeMillis();
		while (!stop && tick++ < numTicks) {
			if (tick % drawDiff == 0) {
				draw();
				lastTime = System.currentTimeMillis();
			}
			//try { Thread.sleep(10000); } catch (Exception e) {}

			sqt.forces();
			links();
			movement();
		}
	}
}
