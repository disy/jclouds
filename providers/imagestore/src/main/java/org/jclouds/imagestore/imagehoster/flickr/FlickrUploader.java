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
package org.jclouds.imagestore.imagehoster.flickr;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.jclouds.imagestore.imagehoster.HImageHostHelper;
import org.xml.sax.SAXException;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;
import com.googlecode.flickrjandroid.uploader.Uploader;

/**
 * This class offers an Flickr uploader for images.
 * 
 * @author Wolfgang Miller
 */
public class FlickrUploader {

    /** The Uploader instance. */
    private final Uploader up;

    /**
     * Instantiates a new Flickr uploader.
     * 
     * @param fl
     *            The Flickr instance.
     */
    public FlickrUploader(final Flickr fl) {
        up = fl.getUploader();
    }

    /**
     * Upload buffered image.
     * 
     * @param imageTitle
     *            The image title.
     * @param image
     *            The BufferedImage.
     * @param meta
     *            The Flickr meta data.
     * @return The Flickr photo id.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws FlickrException
     *             Signals that a flickr exception has occurred.
     * @throws SAXException
     *             Signals that an SAX exception has occurred.
     */
    public String uploadImage(final String imageTitle, final BufferedImage image,
        final UploadMetaData meta) throws IOException, FlickrException, SAXException {
        return up.upload(imageTitle, HImageHostHelper.getInputStreamFromImage(image), meta);
    }
}
