package src.convolution.filters;

import java.util.ArrayList;

public abstract class Filter {

	public static ArrayList<Filter> filters = new ArrayList<Filter>();

	public static final Filter none = new None();
	public static final Filter horizontal = new Horizontal();
	public static final Filter vertical = new Vertical();
	public static final Filter diagonalPos = new DiagonalPositive();
	public static final Filter diagonalNeg = new DiagonalNegative();

	private float[][] weights;

	private int width;
	private int height;

	public Filter(float[][] weights, int width, int height) {
		this.weights = weights;
		this.width = width;
		this.height = height;
		filters.add(this);
	}

	public float getWeight(int x, int y) {
		try {
			return weights[x][y];
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Error: Weight coordinate not within filter bounds.");
		}
		return 0;
	}

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

	public static Filter getFilter(String filtID) {
		for (Filter filter : filters)
			if (filter.getID().equals(filtID)) return filter;
		return null;
	}

	public static boolean filterExists(String filtID) {
		for (Filter filter : filters)
			if (filter.getID().equals(filtID)) return true;
		return false;
	}
}