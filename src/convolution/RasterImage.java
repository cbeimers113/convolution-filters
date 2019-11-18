package src.convolution;

/**
 * Image data converted to array of floats between 0.0 and 1.0, with 0.0 representing
 * an RGB pixel of 0x000000, and 1.0 representing 0xFFFFFF. Each channel has the same
 * value, as it is loaded in grayscale.
 */
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
