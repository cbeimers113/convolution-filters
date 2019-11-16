package convolution.filters;

public class Vertical extends Filter {

	private static float[][] getWeights() {
		float[][] weights = new float[3][3];
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++)
				if (x == 1) weights[x][y] = 1;
				else
					weights[x][y] = 0;
		return weights;
	}

	public Vertical() {
		super(getWeights(), 3, 3);
	}
}
