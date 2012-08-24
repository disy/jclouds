/*
 * 
 */
package org.jclouds.imagestore.blobstore.imagegenerator.bytepainter;

import java.awt.Color;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class ColorGenerator.
 */
public class ColorGenerator {
	
	/**
	 * returns 3-dimensional array. Each dimension stands for one RGB color.
	 *
	 * @param numColors the num colors
	 * @return the color[][]
	 */

	public static Color[][] generateUniformlyDistributedColors(int numColors) {
		Color[][] caa = new Color[3][numColors];

		for (int i = 0; i < caa.length; i++) {
			Color[] ca = caa[i];
			int sum = 0;
			final int len = ca.length;
			final float ratio = 255f / (len - 1);

			for (int y = 0; y < len; y++) {

				if (i == 0) {
					ca[y] = new Color(sum, 0, 0);
				} else if (i == 1) {
					ca[y] = new Color(0, sum, 0);
				} else {
					ca[y] = new Color(0, 0, sum);
				}

				sum += ratio;
			}
		}
		return caa;
	}
	
	/**
	 * Converts ArrayList<Byte> to byte[].
	 * 
	 * @param li the array-list
	 * @return the byte-array
	 */
	
	public static byte[] arrayListToByteArray(ArrayList<Byte> li){
            byte[] bs = new byte[li.size()];
            int i = 0;
            for(Byte b : li){
                bs[i++] = b;
            }
            return bs;
        }
	
	/**
	 * Y cb cr2 rgb.
	 *
	 * @param y the y
	 * @param cr the cr
	 * @param cb the cb
	 * @return the color
	 */
	public static Color YCbCr2RGB(int y, int cr, int cb){
		
		int r = (int) (y + 1.402 * (cr - 128));
		int g = (int) (y - 0.34414 * (cb -128) - 0.71414 * (cr -128));
		int b = (int) (y + 1.772 * (cb -128));
		
		r = Math.min(255, Math.max(0, r));
		g = Math.min(255, Math.max(0, g));
		b = Math.min(255, Math.max(0, b));

		return new Color(r,g,b);
	}

}
