/**
 * Copyright (c) 2012, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the University of Konstanz nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.Color;
import java.util.ArrayList;

/**
 * This Class offers helper methods for the byte painters.
 * 
 * @author Wolfgang Miller, University of Konstanz
 */
public final class HBytesToImagePainterHelper {

    /**
     * Private constructor. Protects helper class from being instantiated.
     */
    private HBytesToImagePainterHelper() {
    };

    /**
     * Gets color from byte.
     * 
     * @param b
     *            the byte
     * @param numeralSystem
     *            the numeral system
     * @param pixelInNumSys
     *            the pixels needed per byte in the numeral system
     * @return the color from byte
     */
    static int[] getColorsFromByte(final byte b, final int numeralSystem, final int pixelInNumSys) {
        final int it = b & 0xFF;
        String numSysVal = Integer.toString(it, numeralSystem);
        final int[] byteColors = new int[pixelInNumSys];

        while (numSysVal.length() < pixelInNumSys) {
            numSysVal = "0" + numSysVal;
        }

        for (int i = 0; i < pixelInNumSys; i++) {

            String val = numSysVal.substring(i, i + 1);

            int dc = Integer.parseInt(val, numeralSystem);

            byteColors[i] = dc;
        }
        return byteColors;
    }

    /**
     * Converts the current pixel's RGB-value to the given numeral system.
     * 
     * @param colors
     *            The colors used
     * @param rgb
     *            The RGB-value of the current pixel
     * @param numeralSystem
     *            The numeral system used
     * @return String with the pixel's value converted to the numeral system.
     */
    static String getNumeralValueFromPixelColor(final Color[] colors, final int rgb, final int numeralSystem) {
        final int red = extractLayerColorFromRGB(rgb, 2);
        final int green = extractLayerColorFromRGB(rgb, 1);
        final int blue = extractLayerColorFromRGB(rgb, 0);

        // the distance
        int dist = -1;
        // the color index
        int idx = -1;

        for (int i = 0; i < colors.length; i++) {
            final int cred = colors[i].getRed();
            final int cgreen = colors[i].getGreen();
            final int cblue = colors[i].getBlue();

            final int currDist = Math.abs(cred - red) + Math.abs(cgreen - green) + Math.abs(cblue - blue);

            if (dist == -1 || currDist < dist) {
                dist = currDist;
                idx = i;
            }
        }

        return Integer.toString(idx, numeralSystem);
    }

    /**
     * Calculates the colors for the current byte.
     * 
     * @param colors
     *            The colors used
     * @param b
     *            The current byte
     * @param layer
     *            The current layer
     * @param numeralSystem
     *            The numeral system used
     * @param pixelInNumSys
     *            The amount of pixels needed for one byte in one layer
     * @return The byte's colors.
     */
    static int[] getLayeredColorsFromByte(final Color[][] colors, final byte b, final int layer,
        final int numeralSystem, final int pixelInNumSys) {
        final int it = b & 0xFF;
        String numVal = Integer.toString(it, numeralSystem);
        final int[] byteColors = new int[pixelInNumSys];

        while (numVal.length() < pixelInNumSys) {
            numVal = "0" + numVal;
        }

        for (int i = 0; i < pixelInNumSys; i++) {

            String val = numVal.substring(i, i + 1);

            int dc = Integer.parseInt(val, numeralSystem);

            byteColors[i] = colors[layer][dc].getRGB();
        }
        return byteColors;
    }

    /**
     * Returns a 2-dimensional array with 3 layers of colors. The first dimension stands for each layer, the
     * second for the color of the layer.
     * 
     * @param numColors
     *            the number of different colors
     * @return the calculated colors
     */

    static Color[][] generate3LayeredUniformlyDistributedColors(final int numColors) {
        final Color[][] caa = new Color[3][numColors];
        final int len = numColors;

        for (int layer = 0; layer < 3; layer++) {
            Color[] ca = caa[layer];
            int sum = 0;
            final float ratio = 255f / (len - 1);

            for (int y = 0; y < len; y++) {

                if (layer == 0) {
                    ca[y] = new Color(0, 0, sum);
                } else if (layer == 1) {
                    ca[y] = new Color(0, sum, 0);
                } else {
                    ca[y] = new Color(sum, 0, 0);
                }

                sum += ratio;
            }
        }
        return caa;
    }

    /**
     * Converts ArrayList<Byte> to byte[].
     * 
     * @param li
     *            the array list
     * @return the byte array
     */

    static byte[] arrayListToByteArray(final ArrayList<Byte> li) {
        byte[] bs = new byte[li.size()];
        int i = 0;
        for (Byte b : li) {
            bs[i++] = b;
        }
        return bs;
    }

    /**
     * Extracts the level's color-value of the given RGB-value.
     * 
     * @param rgb
     *            The RGB-value
     * @param layer
     *            The layer
     * @return The color's value
     */
    static int extractLayerColorFromRGB(final int rgb, final int layer) {
        return (rgb >> (layer * 8)) & 0xFF;
    }

    /**
     * Extracts the numeral value from current pixel's RGB-value.
     * 
     * @param layer
     *            The current layer
     * @param colors
     *            The colors used
     * @param colorVal
     *            The RGB-value of the current pixel
     * @param numeralSystem
     *            The numeral system used
     * @return The numeral value extracted from the RGB-value
     */
    static String getLayeredNumeralValueFromPixelColor(final int layer, final Color[][] colors,
        final int colorVal, final int numeralSystem) {

        // the distance
        int dist = -1;
        // the color index
        int idx = -1;

        final int layerLength = colors[layer].length;
        for (int i = 0; i < layerLength; i++) {
            final int ccRGB = colors[layer][i].getRGB();
            final int cColorVal = extractLayerColorFromRGB(ccRGB, layer);

            int currDist = Math.abs(cColorVal - colorVal);

            if (dist == -1 || currDist < dist) {
                dist = currDist;
                idx = i;
            }

        }
        return Integer.toString(idx, numeralSystem);
    }
    
    /**
     * Calculates how many pixels needed to store one byte in one layer.
     * 
     * @param numSys
     *            numeral system
     * @return the amount of pixels needed to store one byte in one layer
     */
    static int calcPixelsPerBytePerLayer(final int numSys) {
        return (int)Math.ceil(Math.log(256) / Math.log(numSys));
    }

}
