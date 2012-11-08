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
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;

/**
 * This Class offers a byte painter.
 * <p/>
 * Numeral System: Quaternary <br/>
 * Layers: 1 <br/>
 * 1 Byte = 4 Pixel<br/>
 * 4 colors <br/>
 * <p/>
 * Working with
 * <ul>
 * <li>Flickr</li>
 * <li>Facebook (only if ImmageType is RGB, GREY not working on Facebook)</li>
 * <li>Picasa</li>
 * </ul>
 * 
 * @author Wolfgang Miller, University of Konstanz
 */
public class QuaternaryBytesToImagePainter implements IBytesToImagePainter {

    /** The image type to be used. */
    private static final int BUFFERED_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;
    /** The used numeral system. */
    private static final int NUMERAL_SYSTEM = 4;
    /** Pixels needed for one Byte. */
    private static final float PIXELS_PER_BYTE = 4;

    /** The colors. */
    Color[] colors = new Color[] {
        Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.BLACK
    };

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
        return PIXELS_PER_BYTE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage storeBytesInImage(final BufferedImage bi, final byte[] bs) {

        final int w = bi.getWidth();
        final int h = bi.getHeight();
        final Graphics g = bi.getGraphics();

        int len = bs.length;
        int bsPos = 0;
        int[] colorIdx = null;

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;
                final int pos = pix % (int)PIXELS_PER_BYTE;

                if (pos == 0) {

                    /* if picture is too small for next bytes return */
                    if ((y == h - 1) && (x + 3 > w))
                        return bi;

                    if (bsPos >= len) {
                        break;
                    }

                    byte currB = bs[bsPos++];

                    colorIdx =
                        HBytesToImagePainterHelper.getColorsFromByte(currB, NUMERAL_SYSTEM,
                            (int)PIXELS_PER_BYTE);
                }

                g.setColor(colors[colorIdx[pos]]);

                g.drawLine(x, y, x, y);
            }
        }
        return bi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBytesFromImage(final BufferedImage img) {

        final ArrayList<Byte> li = new ArrayList<Byte>();
        final int w = img.getWidth();
        final int h = img.getHeight();
        final int mod = (int)(PIXELS_PER_BYTE - 1);

        String quaternary = "";

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;

                quaternary +=
                    HBytesToImagePainterHelper.getNumeralValueFromPixelColor(colors, img.getRGB(x, y),
                        NUMERAL_SYSTEM);

                if (pix % PIXELS_PER_BYTE == mod) {
                    byte b = (byte)Integer.parseInt(quaternary, NUMERAL_SYSTEM);
                    li.add(b);
                    quaternary = "";
                }
            }
        }
        return HBytesToImagePainterHelper.arrayListToByteArray(li);
    }

}
