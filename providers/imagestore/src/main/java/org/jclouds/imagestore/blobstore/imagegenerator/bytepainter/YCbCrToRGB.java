/*
 * 
 */
package org.jclouds.imagestore.blobstore.imagegenerator.bytepainter;

import java.awt.Color;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class YCbCrToRGB.
 */
public class YCbCrToRGB {

	/** The Constant cRed. */
	final static double cRed = 0.299;
	
	/** The Constant cGreen. */
	final static double cGreen = 0.587;
	
	/** The Constant cBlue. */
	final static double cBlue = 0.114;


	/**
	 * Gets the rGB color from y cb cr.
	 *
	 * @param y the y
	 * @param cb the cb
	 * @param cr the cr
	 * @return the rGB color from y cb cr
	 */
	static Color getRGBColorFromYCbCr(final float y, final float cb, final float cr) {
		return getRGBColorFromYCbCr((int) (y*255+0.5), (int) (cb*255+0.5), (int) (cr*255+0.5));
	}
	
	/**
	 * Gets the rGB color from y cb cr.
	 *
	 * @param Y the y
	 * @param cB the c b
	 * @param cR the c r
	 * @return the rGB color from y cb cr
	 */
	static Color getRGBColorFromYCbCr(final int Y, final int cB, final int cR) {
				
		//range of each input (R,G,B) is [-128...+127]
		//http://www.impulseadventure.com/photo/jpeg-color-space.html
		
		int R = (int) (Y + 1.402 * (cR - 128)) & 0xFF;
		int G = (int) (Y - 0.34414 * (cB - 128) - 0.71414 * (cR - 128)) & 0xFF;
		int B = (int) (Y + 1.772 * (cB - 128)) & 0xFF;

		System.out.println(R + " " + Integer.toBinaryString(R));
		System.out.println(G + " " + Integer.toBinaryString(G));
		System.out.println(B + " " + Integer.toBinaryString(B));
		
		return new Color((int) R, (int) G, (int) B);
	}
}
