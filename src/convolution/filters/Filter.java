package src.convolution.filters;

import java.util.ArrayList;

/**
 * 2D array of coefficients to be multiplied by the values of the RasterImage to which it will be applied. 
 * The desired pattern is encoded as an arrangement of 1s, with the undesired space remaining as 0. 
 * TODO: Change range of weights from 0 to 1 to -1 to 1 to detect features in more complex images
 * When the filter is applied, the output image will maintain the desired features since the pixels it has 
 * multiplied to the pattern are of higher value.
 */
public abstract class Filter {

	/**
	 * Dummy class to tell the program to apply all filters separately to the image
	 */
	private static class AllFilters extends Filter {
		private AllFilters() {
			super(null, 0, 0);
		}

		public String toString() {
			return "All";
		}
	}

	public static ArrayList<Filter> filters = new ArrayList<Filter>();

	public static final Filter none = new None();
	public static final Filter horizontal = new Horizontal();
	public static final Filter vertical = new Vertical();
	public static final Filter diagonalPos = new DiagonalPositive();
	public static final Filter diagonalNeg = new DiagonalNegative();
	public static final Filter all = new AllFilters();

	private float[][] weights;

	private int width;
	private int height;

	/**
	 * Filter constructor, all filters have 2D array of weights.
	 * 
	 * @param weights:
	 *            Coefficients to multiply by the pixels of the image
	 * @param width:
	 *            Width of the filter
	 * @param height:
	 *            Height of the filter
	 */
	public Filter(float[][] weights, int width, int height) {
		this.weights = weights;
		this.width = width;
		this.height = height;
		filters.add(this);
	}

	/**
	 * Gets coefficient at x, y
	 * 
	 * @param x:
	 *            Horizontal position of the weight within the filter
	 * @param y:
	 *            Vertical position of the weight within the filter
	 * @return: The weight at x, y
	 */
	public float getWeight(int x, int y) {
		try {
			return weights[x][y];
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Error: Weight coordinate not within filter bounds.");
		}
		return 0;
	}

	/**
	 * @return: Filter name
	 */
	public String toString() {
		String name = getClass().getName();
		name = name.substring(name.lastIndexOf('.') + 1);
		String nBuilder = "";
		String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int i = 0; i < name.length(); i++) {
			if (i > 0 && upper.contains("" + name.charAt(i))) nBuilder += " ";
			nBuilder += name.charAt(i);
		}
		return nBuilder;
	}

	/**
	 * @return: Filter ID as first 3 characters of each word in the name
	 */
	public String getID() {
		String[] parts = toString().split(" ");
		String nBuilder = "";
		for (String part : parts)
			nBuilder += part.toLowerCase().substring(0, 3);
		return nBuilder;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Gets the filter with the specified ID
	 * 
	 * @param filtID:
	 *            ID of the filter to get
	 * @return: The filter with ID filtID, or null of no filter exists with that ID
	 */
	public static Filter getFilter(String filtID) {
		for (Filter filter : filters)
			if (filter.getID().equals(filtID)) return filter;
		return null;
	}
}