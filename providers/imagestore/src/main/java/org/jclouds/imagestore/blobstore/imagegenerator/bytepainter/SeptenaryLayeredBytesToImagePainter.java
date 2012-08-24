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
package org.jclouds.imagestore.blobstore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jclouds.imagestore.blobstore.imagegenerator.BytesToImagePainter;

/**
 * This Class offers a byte painter.
 * 
 * Numeral System: Septenary
 * Layers: 3
 * 1 Byte = 1 Pixel
 * 
 * @author Wolfgang Miller
 */
public class SeptenaryLayeredBytesToImagePainter implements BytesToImagePainter {

    /** The different pixel colors. */
    private final Color[][] colors = BytesToImagePainterHelper.generateUniformlyDistributedColors(7);

    @Override
    public BufferedImage storeBytesInImage(final BufferedImage img, final byte[] bs) {

        final int w = img.getWidth();
        final int h = img.getHeight();
        final Graphics g = img.getGraphics();

        int[] currByteColor = null;
        int len = bs.length;

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;
                final int pos = pix % 3;

                if (pix % 3 == 0) {

                    currByteColor = null;

                    for (int layer = 0; layer < 3; layer++) {

                        final int bsPos = pix + layer;

                        /* if picture is too small for next bytes return */
                        if ((y == h - 1) && (x + 3 > w))
                            return img;

                        if (bsPos >= len) {
                            if (layer == 0)
                                return img;
                            else
                                break;
                        }

                        byte currB = bs[bsPos];

                        int[] bc = getColorsFromByte(currB, layer);

                        if (currByteColor == null) {
                            currByteColor = bc;
                            continue;
                        }

                        for (int c = 0; c < 3; c++) {
                            currByteColor[c] = currByteColor[c] + bc[c];
                        }
                    }
                }

                Color nc = new Color(currByteColor[pos]);
                g.setColor(nc);

                g.drawLine(x, y, x, y);

            }
        }
        return img;
    }

    /**
     * Calculates the colors for the current byte.
     * 
     * @param b
     *            the current byte
     * @param layer
     *            the current layer
     * @return the byte's colors
     */
    private int[] getColorsFromByte(final byte b, final int layer) {
        final int it = b & 0xFF;
        String hept = Integer.toString(it, 7);
        int[] byteColors = new int[3];

        while (hept.length() < 3) {
            hept = "0" + hept;
        }

        final int l = hept.length();

        for (int i = 0; i < l; i++) {

            String val = hept.substring(i, i + 1);

            int dc = Integer.parseInt(val, 7);

            byteColors[i] = colors[layer][dc].getRGB();

        }
        return byteColors;
    }

    @Override
    public byte[] getBytesFromImage(final BufferedImage img) {
        final ArrayList<Byte> al = new ArrayList<Byte>();

        final int w = img.getWidth();
        final int h = img.getHeight();

        String[] septs = new String[] {
            "", "", ""
        };

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;

                getNumericalValueFromPixelColor(img.getRGB(x, y), septs);

                if (pix % 3 == 2) {

                    for (int i = 0; i < 3; i++) {
                        byte b = (byte)Integer.parseInt(septs[i], 7);
                        al.add(b);
                    }

                    septs = new String[] {
                        "", "", ""
                    };
                }
            }
        }

        return BytesToImagePainterHelper.arrayListToByteArray(al);
    }

    /**
     * Extracts the numerical value from current pixel's RGB-value.
     * 
     * @param rgb
     *            the RGB-value of the current pixel
     * @param septs
     *            array to be filled with the numerical values of the current pixel
     */
    private void getNumericalValueFromPixelColor(final int rgb, final String[] septs) {

        Color c = new Color(rgb);
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
            septs[l] += Integer.toString(idx, 7);
        }
    }

    public int[] getImageWidthAndHeight(final int byteArrayLength) {
        int w = 2048;
        int h = (int)(byteArrayLength / (float)w) + 1;
        return new int[] {
            w, h
        };
    }

}
