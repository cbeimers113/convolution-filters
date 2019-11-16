package src.convolution;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class OutputDisplay extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final float SCALE = 4;

	public OutputDisplay(ArrayList<BufferedImage> imgs) {
		if (imgs.size() == 0) return;
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setPreferredSize(new Dimension((int) ((imgs.size() + 1) * (imgs.get(0).getWidth() * SCALE + 5) - 5), (int) (imgs.get(0).getHeight() * SCALE + 60)));
		JLabel[] labels = new JLabel[imgs.size()];
		for (int i = 0; i < labels.length; i++)
			add(labels[i] = new JLabel(new ImageIcon(scale(imgs.get(i), SCALE))));
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("Output");
	}

	private static BufferedImage scale(BufferedImage img, float scale) {
		BufferedImage out = null;
		if (img != null) {
			out = new BufferedImage((int) (img.getWidth() * scale), (int) (img.getHeight() * scale), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = out.createGraphics();
			AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
			g.drawRenderedImage(img, at);
		}
		return out;
	}
}
