package src.convolution;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.convolution.filters.Filter;

/**
 * Lets the user load a black and white image and outputs that image with one or more filters applied to it.
 * Filters find patterns such as lines in different directions, and the output image emphasizes those features
 * while minimalizing the rest of the image.
 */
public class Convolution {

	/**
	 * Convert RGB color to single channel pixel
	 * 
	 * @return input color converted to grayscale as a percentage of white (0xffffff)
	 */
	private static float toSCPixel(int RGB) {
		int r = (RGB >> 16) & 0xFF;
		int g = (RGB >> 8) & 0xFF;
		int b = RGB & 0xFF;

		return (((r + g + b) / 3.0f) / 0xff);
	}

	/**
	 * Turns single channel pixel back into grayscale RGB
	 */
	private static int reconstructRGB(float weight) {
		int c = (int) (weight * 0xFF);
		return c | (c << 8) | (c << 16);
	}

	/**
	 * Returns the image after being filtered
	 * @param image: image to filter
	 * @param filter: filter to apply
	 */
	private static RasterImage getFiltered(RasterImage image, Filter filter) {
		if (filter == Filter.none || filter == Filter.all) return image;
		float[][] pixels = image.getPixels();
		int w = image.getWidth();
		int h = image.getHeight();
		float[][] fPixels = new float[w][h];
		for (int y = 0; y < w; y++) {
			for (int x = 0; x < h; x++) {
				float s = 0;
				float c = 0;
				for (int yy = 0; yy < 3; yy++) {
					int yOffs = y + yy;
					if (yOffs >= h) continue;
					for (int xx = 0; xx < 3; xx++) {
						int xOffs = x + xx;
						if (xOffs >= w) continue;
						s += pixels[xOffs][yOffs] * filter.getWeight(xx, yy); //Filtered image is the dot product of the filter with each AxB section of the image (filters have AxB dimension)
						c++;
					}
				}
				s /= c;
				try {
					fPixels[x][y] = s;
				} catch (ArrayIndexOutOfBoundsException e) {
					continue;
				}
			}
		}
		return new RasterImage(fPixels, w, h);
	}

	//Loads image file as a raster image
	private static RasterImage loadImage(File file) {
		float[][] pixels;
		try {
			BufferedImage img = ImageIO.read(file);
			int w, h;
			pixels = new float[w = img.getWidth()][h = img.getHeight()];
			for (int y = 0; y < h; y++)
				for (int x = 0; x < w; x++)
					pixels[x][y] = toSCPixel(img.getRGB(x, y)); //Load each pixel as a grayscale value
			return new RasterImage(pixels, w, h);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	/**
	 * Loads image, applies filter, then outputs filtered image to new file and returns that image in memory
	 * @param file: File to load
	 * @param filter: Filter to apply
	 * @param amount: How many times to apply the filter
	 * @return: Image data in memory
	 */
	private static BufferedImage filterAndSave(File file, Filter filter, int amount) {
		RasterImage img = loadImage(file);
		if (img == null) return null;
		// TODO: make this more elegant
		File output = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('\\')) + "\\" + file.getName().substring(0, file.getName().lastIndexOf('.')) + "_" + filter.getID() + ".png");
		if (output.exists()) output.delete();
		BufferedImage raster = null;
		try {
			output.createNewFile();
			RasterImage outImg = map(getFiltered(img, filter));
			for (int i = 1; i < amount; i++)
				outImg = map(getFiltered(outImg, filter));
			raster = new BufferedImage(outImg.getWidth(), outImg.getHeight(), BufferedImage.TYPE_INT_RGB);
			float[][] pixels = outImg.getPixels();
			for (int y = 0; y < outImg.getHeight(); y++)
				for (int x = 0; x < outImg.getWidth(); x++)
					raster.setRGB(x, y, reconstructRGB(pixels[x][y])); //Output data restored to an RGB pixel
			ImageIO.write(raster, "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return raster;
	}

	/**
	 * Dialog box to choose and load image file
	 * @return: Chosen file
	 */
	private static File getFile() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "bmp", "gif");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) return chooser.getSelectedFile();
		return null;
	}

	/**
	 * Dialog box to choose filter
	 * @return: Chosen filter
	 */
	private static Filter getFilter() {
		return (Filter) JOptionPane.showInputDialog(null, "Choose Filter", "Choose Filter", JOptionPane.QUESTION_MESSAGE, null, Filter.filters.toArray(), Filter.filters.get(0));
	}

	/**
	 * Find the pixel with the lowest value
	 * @param image: Raster image to check
	 * @return: Value of lowest pixel
	 */
	private static float getMin(RasterImage image) {
		float[][] pixels = image.getPixels();
		float min = Float.MAX_VALUE;
		for (int y = 0; y < image.getHeight(); y++)
			for (int x = 0; x < image.getWidth(); x++)
				if (pixels[x][y] < min) min = pixels[x][y];
		return min;
	}

	/**
	 * Find the pixel with the highest value
	 * @param image: Raster image to check
	 * @return: Value of the highest pixel
	 */
	private static float getMax(RasterImage image) {
		float[][] pixels = image.getPixels();
		float max = Float.MIN_VALUE;
		for (int y = 0; y < image.getHeight(); y++)
			for (int x = 0; x < image.getWidth(); x++)
				if (pixels[x][y] > max) max = pixels[x][y];
		return max;
	}

	/**
	 * Maps the input image to the range 0x00->0xFF by stretching the filtered data to fit.
	 * The highest pixel in the filtered image is treated as 0xFF, and all pixels are multiplied
	 * by a coefficient to fit this range. (Only returns coefficients, multiplied by 0xFF in reconstruction)
	 * @param image: Image to map
	 * @return: Image mapped to 0x00->0xFF
	 */
	private static RasterImage map(RasterImage image) {
		/*
		 * [0xFF/(max-min)] * n
		 */
		float max = getMax(image);
		float min = getMin(image);
		float[][] pixels = image.getPixels();
		float[][] mapped = new float[image.getWidth()][image.getHeight()];
		for (int y = 0; y < image.getHeight(); y++)
			for (int x = 0; x < image.getWidth(); x++)
				mapped[x][y] = (pixels[x][y] - min) / (max - min);
		return new RasterImage(mapped, image.getWidth(), image.getHeight());
	}

	public static void main(String[] args) {
		File file = getFile();
		Filter filter = getFilter();
		ArrayList<BufferedImage> imgs = new ArrayList<BufferedImage>();
		if (filter == Filter.all) {
			for (Filter f : Filter.filters)
				if (f != Filter.all) imgs.add(filterAndSave(file, f, 1));
		} else {
			imgs.add(filterAndSave(file, Filter.none, 1));
			imgs.add(filterAndSave(file, filter, 1));
		}
		new OutputDisplay(imgs);
	}
}
