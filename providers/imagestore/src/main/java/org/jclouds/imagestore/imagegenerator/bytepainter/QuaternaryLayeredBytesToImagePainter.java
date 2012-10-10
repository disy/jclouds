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
 * 
 * Numeral System: Quaternary
 * Layers: 3
 * 1 Byte = 4/3 Pixel
 * 
 * @author Wolfgang Miller
 */
public class QuaternaryLayeredBytesToImagePainter implements IBytesToImagePainter {

    /** The image type to be used. */
    private static final int BUFFERED_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;
    /** The used numeral system. */
    private static final int NUMERAL_SYSTEM = 4;
    /** Pixels needed for one Byte. */
    private static final float PIXELS_PER_BYTE = 4 / 3f;
    /** Pixels needed per Byte in one layer. */
    private static final int BYTES_PER_PIXEL_PER_LAYER = 4;

    /** The colors. */
    private final Color[][] colors = HBytesToImagePainterHelper
        .generateLayeredUniformlyDistributedColors(NUMERAL_SYSTEM);

    @Override
    public int getImageType() {
        return BUFFERED_IMAGE_TYPE;
    }
    
    @Override
    public float pixelsPerByte() {
        return PIXELS_PER_BYTE;
    }

    @Override
    public BufferedImage storeBytesInImage(final BufferedImage image, final byte[] bs) {

        final int w = image.getWidth();
        final int h = image.getHeight();
        final Graphics g = image.getGraphics();

        final int len = bs.length;
        int[] currByteColor = null;

        int bp = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x += 4) {
                for (int layer = 0; layer < 3; layer++) {

                    if (bp >= len)
                        break;

                    final byte b = bs[bp++];

                    int[] bc =
                        HBytesToImagePainterHelper.getLayeredColorsFromByte(colors, b, layer, NUMERAL_SYSTEM,
                            BYTES_PER_PIXEL_PER_LAYER);

                    if (currByteColor == null) {
                        currByteColor = bc;

                    } else {
                        for (int c = 0; c < 4; c++) {
                            currByteColor[c] = currByteColor[c] + bc[c];
                        }
                    }

                    if (layer == 2 || bp == len) {
                        for (int c = 0; c < 4; c++) {
                            Color nc = new Color(currByteColor[c]);

                            g.setColor(nc);

                            g.drawLine(x + c, y, x + c, y);

                        }
                        currByteColor = null;
                    }
                }
            }
        }
        return image;
    }

    @Override
    public byte[] getBytesFromImage(final BufferedImage img) {

        final ArrayList<Byte> al = new ArrayList<Byte>();

        final int w = img.getWidth();
        final int h = img.getHeight();

        String[] quaters = new String[] {
            "", "", ""
        };

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;

                HBytesToImagePainterHelper.getLayeredNumericalValueFromPixelColor(colors, img.getRGB(x, y),
                    NUMERAL_SYSTEM, quaters);

                if (pix % 4 == 3) {

                    for (int layer = 0; layer < 3; layer++) {
                        byte b = (byte)Integer.parseInt(quaters[layer], NUMERAL_SYSTEM);
                        al.add(b);
                    }

                    quaters = new String[] {
                        "", "", ""
                    };
                }
            }
        }

        return HBytesToImagePainterHelper.arrayListToByteArray(al);
    }
}
