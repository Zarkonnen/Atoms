package atoms;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static java.lang.Math.*;

public class SquaresThread implements Runnable {
	final Atom[] atoms;
	final int gridSize;
	final int squareSize;
	final int sliceStart;
	final int sliceWidth;
	volatile boolean tickDone = false;
	final Atom[][][] squares;
	final int[][] sqSizes;
	final double repDistSq;
	final double repStrength;
	
	public SquaresThread(Atom[] atoms, int gridSize, int squareSize, int sliceStart,
			int sliceWidth, double repulsionDistance, double repStrength) {
		this.atoms = atoms;
		this.gridSize = gridSize;
		this.squareSize = squareSize;
		this.sliceStart = sliceStart;
		this.sliceWidth = sliceWidth;
		squares = new Atom[gridSize/squareSize][sliceWidth][8];
		sqSizes = new int[gridSize/squareSize][sliceWidth];
		repDistSq = repulsionDistance * repulsionDistance;
		this.repStrength = repStrength;
	}

	public void run() {
		while (true) {
			while(tickDone) {}
			forces();
			tickDone = true;
		}
	}

	public void redoSquares() {
		for (int y = 0; y < squares.length; y++) {
			for (int x = 0; x < squares[y].length; x++) {
				/*for (int a = 0; a < squares[y][x].length; a++) {
					squares[y][x][a] = null;
				}*/
				sqSizes[y][x] = 0;
			}
		}

		int start = sliceStart * squareSize;
		int end = (sliceStart + sliceWidth) * squareSize;
		for (int i = 0; i < atoms.length; i++) {
			Atom at = atoms[i];
			if (at.x >= start && at.x < end) {
				int y = ((int) at.y) / squareSize;
				int x = ((int) at.x) / squareSize - sliceStart;
				squares[y][x][sqSizes[y][x]++] = at;
				if (sqSizes[y][x] == squares[y][x].length) {
					Atom[] as = squares[y][x];
					Atom[] newAs = new Atom[as.length * 2];
					System.arraycopy(as, 0, newAs, 0, as.length);
					squares[y][x] = newAs;
				}
			}
		}
	}

	public void forces() {
		// Clear squares
		// Loop over all atoms and sort them into apportioned grid squares.
		// Loop over all apportioned grid squares A.
			// Loop over all neighbouring squares B including the own one.
				// Loop over all combinations of atoms in A and B, and if they are close enough
				// make them repel each other.

		redoSquares();

		int numSquares = gridSize / squareSize;
		//int colls = 0;
		//int its = 0;
		for (int y = 0; y < numSquares; y++) {
			// NB Slices had better be of width 3 or higher or else this all gets buried in jam.
			int xStart =
					sliceWidth == numSquares
					? sliceStart
					: sliceStart + 1;
			int xEnd =
					sliceWidth == numSquares
					? sliceStart + sliceWidth
					: sliceStart + sliceWidth - 1;
			for (int x = xStart; x < xEnd; x++) {
				Atom[] sqA = squares[y][x];
				final int sqAL = sqSizes[y][x];
				if (sqAL == 0) { continue; }
				for (int yy = y - 1; yy <= y + 1; yy++) {
				//for (int yy = y; yy <= y + 1; yy++) {
					int yyy = (yy + numSquares) % numSquares;
					for (int xx = x - 1; xx <= x + 1; xx++) {
						// Haaaack
						//if (xx == x - 1 && yy == y) { continue; }
						int xxx = (xx + numSquares) % numSquares;
						if (xxx < sliceStart || xxx >= (sliceStart + sliceWidth)) { continue; }
						Atom[] sqB = squares[yyy][xxx];
						//System.out.println(x + "/" + y + " " + xxx + "/" + yyy);
						//for (Atom aA : sqA) { for (Atom aB : sqB) {
						// Fun fact: this way of iterating is really really slow.
						final int sqBL = sqSizes[yyy][xxx];
						if (sqBL == 0) { continue; }
						for (int i = 0; i < sqAL; i++) { for (int j = 0; j < sqBL; j++) {
							Atom aA = sqA[i];
							Atom aB = sqB[j];
							//its++;
							if (aA == aB) { continue; }
							double xd = aA.x - aB.x;
							double yd = aA.y - aB.y;
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
							if (sqD < repDistSq && sqD > 0.0001) {
								// They are pushing on one another.
								//double d = sqrt(sqD);
								// Scale & push.
								//xd = xd * (FLIP - d) / d * PUSH_SPRING;
								//yd = yd * (FLIP - d) / d * PUSH_SPRING;
								xd = xd * (repDistSq - sqD) / sqD * repStrength;
								yd = yd * (repDistSq - sqD) / sqD * repStrength;
								//System.out.println("C: " + aA.num + " vs " + aB.num + " xd " + xd + " yd " + yd);
								aA.dx += xd / aA.mass;
								aA.dy += yd / aA.mass;
								aB.dx -= xd / aB.mass;
								aB.dy -= yd / aB.mass;
								//colls++;
							}
						}}
					}
				}
			}
		}
		/*if (colls != 0) {
			System.out.println(its + "/" + colls);
		}
		System.out.println();*/
	}
}
