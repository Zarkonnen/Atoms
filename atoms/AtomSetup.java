package atoms;

public class AtomSetup {
	int winSize = 600;
	int gridSize = 2;
	int stepDiff = 5;
	double atomSpacing = 1.23;
	double repulsionDistance = 1.2;
	double repulsionStrength = 0.01;
	
	int aRed = 255;
	int aGreen = 50;
	int aBlue = 50;
	int aBlockSize = 20;
	Packing aPacking = Packing.TRIANGLE;
	double aMass = 2.0;
	double aHeat = 0.0001;
	double aLeft = 100;
	double aTop = 100;
	double aXSpeed = 0.15;
	double aYSpeed = 0.18;
	double aLinkLength = 1.26;
	double aLinkSnap = 2.4;
	double aLinkStrength = 0.05;

	int bRed = 70;
	int bGreen = 255;
	int bBlue = 70;
	int bBlockSize = 100;
	Packing bPacking = Packing.TRIANGLE;
	double bMass = 1.0;
	double bHeat = 0.0001;
	double bLeft = 200;
	double bTop = 200;
	double bXSpeed = 0;
	double bYSpeed = 0;
	double bLinkLength = 1.26;
	double bLinkSnap = 2.4;
	double bLinkStrength = 0.007;
	
	String saveFolderPath = "";
	String filePrefix = "";
	int numberOfTicks = 1000;

	public static enum Packing {
		SQUARE,
		TRIANGLE
	};
}
