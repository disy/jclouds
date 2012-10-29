/*
 * 
 */
package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;

/**
 * The Class OctalLayeredByteToPixelPainter.
 */
public class OctalLayeredColorAlternatingBytesToImagePainter implements
		IBytesToImagePainter {

	/** The image type to be used. */
	private static final int BUFFERED_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;

	/** The amount of layers. */
	private static final int LAYERS = 3;

	/** The number system. */
	private final int numberSystem = 8;

	/** The colors. */
	private final Color[][] colors = HBytesToImagePainterHelper
			.generateLayeredUniformlyDistributedColors(numberSystem);

	/** Bytes needed per pixel. */
	public static final float BYTES_PER_PIXEL = 3;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getImageType() {
		return BUFFERED_IMAGE_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float pixelsPerByte() {
		return BYTES_PER_PIXEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BufferedImage storeBytesInImage(BufferedImage bi, final byte[] bs) {

		bi = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
		final int w = bi.getWidth();
		final int h = bi.getHeight();
		final Graphics g = bi.getGraphics();
		final int sumPix = w * h;

		ArrayList<Integer> cList = new ArrayList<Integer>(sumPix);

		int[] currByteColor = new int[0];

		/* color index */
		int cIdx = 0;
		int len = bs.length;
		boolean moreBytes = true;

		// the current position in the color-array
		int cp = 0;
		// the current index position in the byte-array
		int bp = 0;

		// the image's amount of pixels
		final int ps = w * h;
		// the color index
		int[] ci;

		FirstTwoLayersFinished: while (bp < len) {
			final byte b = bs[bp++];
			ci = getColorFromByte(b, bp % 2 == 0);

			final int cLen = ci.length;
			for (int i = 0; i < cLen; i++) {
				cList.add(ci[i]);

				// break if first two layers are full
				if (cList.size() / ps >= 2) {
					// copy overflow for third layer
					if (i < cLen)
						ci = Arrays.copyOfRange(ci, i, cLen);
					break FirstTwoLayersFinished;
				}
			}
		}

		for (int y = 0; y < h; y++) {

			// amount of used pixels
			final int hpix = w * y;

			for (int x = 0; x < w; x++) {

				final int pix = hpix + x;

				if (bp < len) {

					final byte b = bs[bp++];

				}
			}

		}
		return bi;
	}

	/**
	 * Gets the color from byte.
	 * 
	 * @param b
	 *            the b
	 * @param layer
	 *            the layer
	 * @param even
	 *            the even
	 * @return the color from byte
	 */
	private int[] getColorFromByte(final byte b, final boolean even) {

		// if even convert byte to integer, if uneven, add Byte.MAX_VALUE to
		// byte and convert to integer. This is done to alternate the colors.
		// colors1: between 0 and 255, colors2: between 256 and 511
		int it = even ? b & 0xFF : (b & 0xFF) + Byte.MAX_VALUE;

		String octs = Integer.toString(it, numberSystem);
		final int len = octs.length();
		int[] dc = new int[len];

		for (int i = 0; i < len; i++) {

			String val = octs.substring(i, i + 1);

			dc[i] = Integer.parseInt(val, numberSystem);
		}
		return dc;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getBytesFromImage(BufferedImage img) {

		final ArrayList<Byte> li = new ArrayList<Byte>();

		final int w = img.getWidth();
		final int h = img.getHeight();

		String[] hepts = new String[] { "", "", "" };

		for (int y = 0; y < h; y++) {

			final int hpix = w * y;

			for (int x = 0; x < w; x++) {

				final int pix = hpix + x;

				getHeptsFromPixel(img.getRGB(x, y), hepts);

				if (pix % 3 == 2) {

					for (int i = 0; i < 3; i++) {
						byte b = (byte) Integer.parseInt(hepts[i], 7);
						li.add(b);
					}

					hepts = new String[] { "", "", "" };
				}
			}
		}
		return HBytesToImagePainterHelper.arrayListToByteArray(li);
	}

	/**
	 * Gets the hepts from pixel.
	 * 
	 * @param pix
	 *            the pix
	 * @param hepts
	 *            the hepts
	 * @return the hepts from pixel
	 */
	private void getHeptsFromPixel(final int pix, String[] hepts) {

		Color c = new Color(pix);
		int red = c.getRed();
		int green = c.getGreen();
		int blue = c.getBlue();

		for (int l = 0; l < 3; l++) {
			int dist = -1;
			int idx = -1;

			if (l == 0) {

				for (int i = 0; i < colors[l].length; i++) {
					int cred = colors[l][i].getRed();

					int currDist = Math.abs(cred - red);

					if (dist == -1 || currDist < dist) {
						dist = currDist;
						idx = i;
					}
				}

			} else if (l == 1) {

				for (int i = 0; i < colors[l].length; i++) {
					int cgreen = colors[l][i].getGreen();

					int currDist = Math.abs(cgreen - green);

					if (dist == -1 || currDist < dist) {
						dist = currDist;
						idx = i;
					}
				}
			} else {

				for (int i = 0; i < colors[l].length; i++) {
					int cblue = colors[l][i].getBlue();

					int currDist = Math.abs(cblue - blue);

					if (dist == -1 || currDist < dist) {
						dist = currDist;
						idx = i;
					}
				}
			}
			hepts[l] += Integer.toString(idx, 7);
		}
	}
}
