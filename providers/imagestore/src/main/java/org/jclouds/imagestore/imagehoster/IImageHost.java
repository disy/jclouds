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
package org.jclouds.imagestore.imagehoster;

import java.awt.image.BufferedImage;

/**
 * The ImageHoster interface.
 * 
 * @author Wolfgang Miller, University of Konstanz
 */

public interface IImageHost {

    /**
     * The maximum image with.
     * 
     * @return The maximum image width.
     */
    int getMaxImageWidth();

    /**
     * The maximum image height.
     * 
     * @return The maximum image height.
     */
    int getMaxImageHeight();

    /**
     * Create image-set.
     * 
     * @param imageSetTitle
     *            the set-title
     * @return true if set was created
     */
    boolean createImageSet(final String imageSetTitle);

    /**
     * Check if image exists.
     * 
     * @param imageSetTitle
     *            the set-title
     * @param imageTitle
     *            the image-title
     * @return true if image exists
     */
    boolean imageExists(final String imageSetTitle, final String imageTitle);

    /**
     * Check if image-set exists.
     * 
     * @param imageSetTitle
     *            the set-title
     * @return true if image-set exists
     */
    boolean imageSetExists(final String imageSetTitle);

    /**
     * Delete image.
     * 
     * @param imageSetTitle
     *            the set-title
     * @param imageTitle
     *            the image-title
     * @return true if successful
     */
    boolean deleteImage(final String imageSetTitle, final String imageTitle);

    /**
     * Delete image set.
     * 
     * @param imageSetTitle
     *            the set-title
     * @return true if successful
     */
    boolean deleteImageSet(final String imageSetTitle);

    /**
     * Upload image and add it to a specified set.
     * 
     * @param imageSetTitle
     *            the set title
     * @param imageTitle
     *            the image title
     * @param image
     *            the image
     * @return true if upload successful, false if image already existing
     * 
     */
    boolean uploadImage(final String imageSetTitle, final String imageTitle, final BufferedImage image);

    /**
     * Download image.
     * 
     * @param imageSetTitle
     *            the set title
     * @param imageTitle
     *            the image title
     * @return the buffered image
     * 
     */
    BufferedImage downloadImage(final String imageSetTitle, final String imageTitle);

    /**
     * Returns number of images in given set.
     * 
     * @param imageSetTitle
     *            the set title
     * @return count of images in the set
     */
    int countImagesInSet(final String imageSetTitle);

    /**
     * Deletes all content of the image set without deleting the set itself.
     * 
     * @param imageSetTitle
     *            the set title
     * @return true if successful
     */
    boolean clearImageSet(final String imageSetTitle);

}
