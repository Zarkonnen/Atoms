package atoms;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.JFrame;

public class AtomFrame extends JFrame {
	static final double COS_30 = Math.cos(Math.PI / 6);
	static final double[] TRI_OFFSET = { 0, 0.5 };
	static final int[][] TRI_LINKS = { { -1, 0 }, { 0, 1 } };

	public AtomFrame(AtomSetup as) {
		Canvas c = new Canvas();
		c.setBackground(Color.BLACK);
		add(c);
		setSize(as.winSize, as.winSize);
		ArrayList<Atom> ats = new ArrayList<Atom>();
		LinkedList<Link> links = new LinkedList<Link>();
		Random r = new Random();

		Atom[][] solid = new Atom[as.aBlockSize][as.aBlockSize];

		int atc = 0;
		Color col = new Color(as.aRed, as.aGreen, as.aBlue);
		switch (as.aPacking) {
			case SQUARE:
				for (int y = 0; y < as.aBlockSize; y++) {
					for (int x = 0; x < as.aBlockSize; x++) {
						solid[y][x] = new Atom(atc++, as.aLeft + as.atomSpacing * x, as.aTop + as.atomSpacing * y,
								(r.nextDouble() - 0.5) * as.aHeat + as.aXSpeed,
								(r.nextDouble() - 0.5) * as.aHeat + as.aYSpeed,
								as.aMass);
						solid[y][x].col = col;
						ats.add(solid[y][x]);
					}
				}
				for (int a = 0; a < as.aBlockSize; a++) {
					for (int b = 0; b < as.aBlockSize - 1; b++) {
						links.add(new Link(solid[a][b], solid[a][b + 1], as.aLinkLength,
								as.aLinkStrength, as.aLinkSnap, as.winSize));
						links.add(new Link(solid[b][a], solid[b + 1][a], as.aLinkLength,
								as.aLinkStrength, as.aLinkSnap, as.winSize));
					}
				}
				break;
			case TRIANGLE:
				for (int y = 0; y < as.aBlockSize; y++) {
					for (int x = 0; x < as.aBlockSize; x++) {
						solid[y][x] = new Atom(atc++,
								as.aLeft + as.atomSpacing * (x + TRI_OFFSET[y % 2]),
								as.aTop + as.atomSpacing * y * COS_30,
								(r.nextDouble() - 0.5) * as.aHeat + as.aXSpeed,
								(r.nextDouble() - 0.5) * as.aHeat + as.aYSpeed,
								as.aMass);
						solid[y][x].col = col;
						ats.add(solid[y][x]);
					}
				}
				for (int y = 0; y < as.aBlockSize; y++) {
					for (int x = 0; x < as.aBlockSize; x++) {
						// Link right
						if (x + 1 < as.aBlockSize) {
							links.add(new Link(solid[y][x], solid[y][x + 1], as.aLinkLength,
								as.aLinkStrength, as.aLinkSnap, as.winSize));
						}
						// Links down either go -1/0 or 0/1 depending on row.

						// Link left/down
						int hOffset = TRI_LINKS[y % 2][0];
						if (y + 1 < as.aBlockSize && x + hOffset >= 0 && x + hOffset < as.aBlockSize) {
							links.add(new Link(solid[y][x], solid[y + 1][x + hOffset], as.aLinkLength,
									as.aLinkStrength, as.aLinkSnap, as.winSize));
						}
						// Link right/down
						hOffset = TRI_LINKS[y % 2][1];
						if (y + 1 < as.aBlockSize && x + hOffset >= 0 && x + hOffset < as.aBlockSize) {
							links.add(new Link(solid[y][x], solid[y + 1][x + hOffset], as.aLinkLength,
									as.aLinkStrength, as.aLinkSnap, as.winSize));
						}
					}
				}
				break;
		}

		solid = new Atom[as.bBlockSize][as.bBlockSize];

		col = new Color(as.bRed, as.bGreen, as.bBlue);

		switch (as.bPacking) {
			case SQUARE:
				for (int y = 0; y < as.bBlockSize; y++) {
					for (int x = 0; x < as.bBlockSize; x++) {
						solid[y][x] = new Atom(atc++, as.bLeft + as.atomSpacing * x, as.bTop + as.atomSpacing * y,
								(r.nextDouble() - 0.5) * as.bHeat + as.bXSpeed,
								(r.nextDouble() - 0.5) * as.bHeat + as.bYSpeed,
								as.bMass);
						solid[y][x].col = col;
						ats.add(solid[y][x]);
					}
				}
				for (int a = 0; a < as.bBlockSize; a++) {
					for (int b = 0; b < as.bBlockSize - 1; b++) {
						links.add(new Link(solid[a][b], solid[a][b + 1], as.bLinkLength,
								as.bLinkStrength, as.bLinkSnap, as.winSize));
						links.add(new Link(solid[b][a], solid[b + 1][a], as.bLinkLength,
								as.bLinkStrength, as.bLinkSnap, as.winSize));
					}
				}
				break;
			case TRIANGLE:
				for (int y = 0; y < as.bBlockSize; y++) {
					for (int x = 0; x < as.bBlockSize; x++) {
						solid[y][x] = new Atom(atc++,
								as.bLeft + as.atomSpacing * (x + TRI_OFFSET[y % 2]),
								as.bTop + as.atomSpacing * y * COS_30,
								(r.nextDouble() - 0.5) * as.bHeat + as.bXSpeed,
								(r.nextDouble() - 0.5) * as.bHeat + as.bYSpeed,
								as.bMass);
						solid[y][x].col = col;
						ats.add(solid[y][x]);
					}
				}
				for (int y = 0; y < as.bBlockSize; y++) {
					for (int x = 0; x < as.bBlockSize; x++) {
						// Link right
						if (x + 1 < as.bBlockSize) {
							links.add(new Link(solid[y][x], solid[y][x + 1], as.bLinkLength,
								as.bLinkStrength, as.bLinkSnap, as.winSize));
						}
						// Links down either go -1/0 or 0/1 depending on row.

						// Link left/down
						int hOffset = TRI_LINKS[y % 2][0];
						if (y + 1 < as.bBlockSize && x + hOffset >= 0 && x + hOffset < as.bBlockSize) {
							links.add(new Link(solid[y][x], solid[y + 1][x + hOffset], as.bLinkLength,
									as.bLinkStrength, as.bLinkSnap, as.winSize));
						}
						// Link right/down
						hOffset = TRI_LINKS[y % 2][1];
						if (y + 1 < as.bBlockSize && x + hOffset >= 0 && x + hOffset < as.bBlockSize) {
							links.add(new Link(solid[y][x], solid[y + 1][x + hOffset], as.bLinkLength,
									as.bLinkStrength, as.bLinkSnap, as.winSize));
						}
					}
				}
				break;
		}

		/*
		for (int y = 0; y < as.bBlockSize; y++) {
			for (int x = 0; x < as.bBlockSize; x++) {
				solid[y][x] = new Atom(atc++, as.bLeft + as.atomSpacing * x, as.bTop + as.atomSpacing * y,
						(r.nextDouble() - 0.5) * as.bHeat + as.bXSpeed,
						(r.nextDouble() - 0.5) * as.bHeat + as.bYSpeed,
						as.bMass);
				solid[y][x].col = col;
				ats.add(solid[y][x]);
			}
		}

		for (int a = 0; a < as.bBlockSize; a++) {
			for (int b = 0; b < as.bBlockSize - 1; b++) {
				links.add(new Link(solid[a][b], solid[a][b + 1], as.bLinkLength,
						as.bLinkStrength, as.bLinkSnap, as.winSize));
				links.add(new Link(solid[b][a], solid[b + 1][a], as.bLinkLength,
						as.bLinkStrength, as.bLinkSnap, as.winSize));
			}
		}*/


		setVisible(true);
		final AtomThread at = new AtomThread(c, ats, links, as.winSize, as.gridSize,
				as.stepDiff, as.repulsionDistance, as.repulsionStrength, as.numberOfTicks,
				as.saveFolderPath.isEmpty() ? null : new File(as.saveFolderPath), as.filePrefix);
		final Thread t = new Thread(at);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				at.stop = true;
			}
		});
		t.start();
	}
}
