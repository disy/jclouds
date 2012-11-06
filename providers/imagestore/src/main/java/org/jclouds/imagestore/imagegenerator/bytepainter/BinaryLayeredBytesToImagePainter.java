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

import java.awt.image.BufferedImage;

/**
 * This Class offers a byte painter.
 * <p/>
 * Numeral System: Binary <br/>
 * Layers: 3 <br/>
 * 4 Pixel = 3 Byte (based on layers(RGB)) <br/>
 * 8(2power3) colors pro pixel <br/>
 * <p/>
 * Working with
 * <ul>
 * <li>Flickr</li>
 * <li>Picasa</li>
 * </ul>
 * Not working with
 * <ul>
 * <li>Facebook</li>
 * </ul>
 * 
 * @author Wolfgang Miller
 */

public class BinaryLayeredBytesToImagePainter extends AAbstractLayeredBytesToImagePainter {

    /** The image type to be used. */
    private static final int BUFFERED_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;
    /** The used numeral system. */
    private static final int NUMERAL_SYSTEM = 2;
    /** Pixels needed per Byte in one layer. */
    private static final int PIXELS_PER_BYTE = 8;

    /**
     * Constructor. Invokes AAbstractLayeredBytesToImagePainter with given numeral system and the amount of
     * pixels needed to store one byte in the image.
     */
    public BinaryLayeredBytesToImagePainter() {
        super(NUMERAL_SYSTEM, PIXELS_PER_BYTE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getImageType() {
        return BUFFERED_IMAGE_TYPE;
    }

}
