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
package org.jclouds.imagestore.imagegenerator;

import java.awt.image.BufferedImage;

/**
 * The interface for BytePainters.
 * 
 * @author Wolfgang Miller, University of Konstanz
 */
public interface IBytesToImagePainter {

    /**
     * Returns the image type needed for the specific painter.
     * 
     * @return the image type
     */
    int getImageType();

    /**
     * Returns the number of pixels needed per byte.
     * 
     * @return The number of pixels needed per byte.
     */
    float pixelsPerByte();

    /**
     * Stores bytes in an image.
     * 
     * @param bi
     *            the BufferedImage to store the bytes
     * @param bs
     *            the bytes to be stored
     * @param startP
     *            the pixel where the painter starts
     * @param endP
     *            the pixel where the painter ends
     * @return the image from the given bytes
     */
    BufferedImage
        storeBytesInImage(final BufferedImage bi, final byte[] bs, final int startP, final int endP);

    /**
     * Extracts bytes from given image.
     * 
     * @param bi
     *            the BufferedImage to extract bytes from
     *            * @param startP
     *            the pixel where the painter starts
     * @param endP
     *            the pixel where the painter ends
     * @return the bytes from pixels
     */
    byte[] getBytesFromImage(BufferedImage bi, final int startP, final int endP);

    /**
     * Mandatory to String for better result.
     * 
     * @return a String with name
     */
    String toString();

    /**
     * Returns the numeral system used by the painter.
     * 
     * @return the numeral system
     */
    int getNumeralSystem();

    /**
     * Returns true if the painter uses layers, false if not.
     * 
     * @return true if layered painter
     */
    boolean isLayered();

}
