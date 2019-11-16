package convolution.filters;

public class DiagonalPositive extends Filter {

	private static float[][] getWeights() {
		float[][] weights = new float[3][3];
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++)
				weights[x][y] = 0;
		weights[0][2] = 1;
		weights[1][1] = 1;
		weights[2][0] = 1;
		return weights;
	}

	public DiagonalPositive() {
		super(getWeights(), 3, 3);
	}
}
