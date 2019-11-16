package convolution;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import convolution.filters.Filter;

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

	private static RasterImage getFiltered(RasterImage image, Filter filter) {
		float[][] pixels = image.getPixels();
		int w = image.getWidth();
		int h = image.getHeight();
		int fw, fh;
		float[][] fPixels = new float[fw = w - (filter.getWidth() - 1)][fh = h - (filter.getHeight() - 1)];
		for (int y = 0; y < fw; y++) {
			for (int x = 0; x < fh; x++) {
				float s = 0;
				float c = 0;
				for (int yy = 0; yy < 3; yy++) {
					int yOffs = y + yy;
					if (yOffs >= h) continue;
					for (int xx = 0; xx < 3; xx++) {
						int xOffs = x + xx;
						if (xOffs >= w) continue;
						s += pixels[xOffs][yOffs] * filter.getWeight(xx, yy);
						c++;
					}
				}
				s /= c;
				try { //Crashes on some non-square images
					fPixels[x][y] = s;
				} catch (ArrayIndexOutOfBoundsException e) {
					continue;
				}
			}
		}
		return new RasterImage(fPixels, fw, fh);
	}

	private static RasterImage loadImage(File file) {
		float[][] pixels;
		try {
			BufferedImage img = ImageIO.read(file);
			int w, h;
			pixels = new float[w = img.getWidth()][h = img.getHeight()];
			for (int y = 0; y < h; y++)
				for (int x = 0; x < w; x++)
					pixels[x][y] = toSCPixel(img.getRGB(x, y));
			return new RasterImage(pixels, w, h);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	private static boolean filterAndSave(File file, Filter filter) {
		RasterImage img = loadImage(file);
		if (img == null) return false;
		// TODO: make this more elegant
		File output = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('\\')) + "\\" + file.getName().substring(0, file.getName().lastIndexOf('.')) + "_" + filter.getID() + ".png");
		if (output.exists()) output.delete();
		try {
			output.createNewFile();
			RasterImage outImg = getFiltered(img, filter);
			BufferedImage raster = new BufferedImage(outImg.getWidth(), outImg.getHeight(), BufferedImage.TYPE_INT_RGB);
			float[][] pixels = outImg.getPixels();
			for (int y = 0; y < outImg.getHeight(); y++)
				for (int x = 0; x < outImg.getWidth(); x++)
					raster.setRGB(x, y, reconstructRGB(pixels[x][y]));
			ImageIO.write(raster, "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	private static File getFile() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "bmp", "gif");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) return chooser.getSelectedFile();
		return null;
	}

	private static Filter getFilter() {
		return (Filter) JOptionPane.showInputDialog(null, "Choose Filter", "Choose Filter", JOptionPane.QUESTION_MESSAGE, null, Filter.filters.toArray(), Filter.filters.get(0));
	}

	public static void main(String[] args) {
		filterAndSave(getFile(), getFilter());
	}
}
