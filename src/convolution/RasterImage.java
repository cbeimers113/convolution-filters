package convolution;

public class RasterImage {

	private float[][] pixels;

	private int width;
	private int height;

	public RasterImage(float[][] pixels, int width, int height) {
		this.pixels = pixels;
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float[][] getPixels() {
		return pixels;
	}
}
