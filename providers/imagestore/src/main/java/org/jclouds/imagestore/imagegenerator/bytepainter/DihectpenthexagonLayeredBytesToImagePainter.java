package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;

/**
 * This Class offers a byte painter.
 * <p/>
 * Numeral System: 256 <br/>
 * Layers: 3 <br/>
 * 1 Byte = 1/3 Pixel <br/>
 * 256^3 colors <br/>
 * <p/>
 * Working with
 * <ul>
 * <li>Picasa</li>
 * </ul>
 * Not working with
 * <ul>
 * <li>Facebook</li>
 * <li>Flickr</li>
 * </ul>
 * 
 * @author Wolfgang Miller, University of Konstanz
 */
public class DihectpenthexagonLayeredBytesToImagePainter implements
		IBytesToImagePainter {

	/** The image type to be used. */
	private static final int BUFFERED_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;
	/** Pixels needed per Byte in one layer. */
	private static final int PIXELS_PER_BYTE_PER_LAYER = 1;
	/** The amount of layers */
	private static final int LAYERS = 3;

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
		return PIXELS_PER_BYTE_PER_LAYER / (float) LAYERS;
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public BufferedImage storeBytesInImage(BufferedImage image, byte[] bs) {

		final int w = image.getWidth();
		final int h = image.getHeight();
		final Graphics g = image.getGraphics();

		int len = bs.length;
		int bp = 0;

		for (int y = 0; y < h; y++) {

			for (int x = 0; x < w; x++) {

				if (bp >= len)
					return image;

				Color nc = getPixelColorFromBytes(bs, len, bp);
				g.setColor(nc);
				g.drawLine(x, y, x, y);
				bp += 3;
			}
		}
		return image;
	}

	/**
	 * Returns the Color composed from the next thre next three bytes in the byte array.
	 * 
	 * @param bs The byte array
	 * @param len The lenght of the byte array
	 * @param bp The current position in the byte array
	 * @return
	 */
	private Color getPixelColorFromBytes(final byte[] bs, final int len,
			final int bp) {

		int c = 0;
		for (int i = 0; i < 3; i++) {
			final int pos = bp + i;

			if (pos >= len)
				break;

			final int b = bs[pos] & 0xFF;
			// shift byte to match right position in integer container
			final int shift = b << (i * 8);
			c += shift;
		}
		return new Color(c);
	}

	/**
	 * Returns a byte-array with the bytes stored in the given pixel.
	 * 
	 * @param rgb The RGB-value of the current pixel
	 * @return The three bytes stored in the pixel
	 */
	private byte[] getBytesFromPixel(int rgb) {
		return new byte[] { (byte) rgb, (byte) (rgb >> 8), (byte) (rgb >> 16) };
	}

	@Override
	public byte[] getBytesFromImage(BufferedImage image) {
		final ArrayList<Byte> al = new ArrayList<Byte>();

		final int w = image.getWidth();
		final int h = image.getHeight();

		for (int y = 0; y < h; y++) {

			for (int x = 0; x < w; x++) {

				final int rgb = image.getRGB(x, y);
				final byte [] bs = getBytesFromPixel(rgb);

				for (int layer = 0; layer < 3; layer++) {
					al.add(bs[layer]);
				}

			}
		}

		return HBytesToImagePainterHelper.arrayListToByteArray(al);
	}

}
