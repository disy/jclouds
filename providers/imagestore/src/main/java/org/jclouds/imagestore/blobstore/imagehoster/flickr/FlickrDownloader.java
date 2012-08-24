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
package org.jclouds.imagestore.blobstore.flickr;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

import javax.imageio.ImageIO;

import org.json.JSONException;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.Transport;
import com.googlecode.flickrjandroid.photos.Extras;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.googlecode.flickrjandroid.photos.SearchParameters;
import com.googlecode.flickrjandroid.photos.Size;

/**
 * This class offers a downloader for flickr images.
 * 
 * @author Wolfgang Miller
 */
public class FlickrDownloader {

    /** The photo interface. */
    private final PhotosInterface pi;
    /** The transport reference. */
    private final Transport transport;
    /** The user id. */
    private final String id;

    /**
     * Instantiates a new flickr downloader.
     * 
     * @param fl
     *            the flickr instance
     * @param userId
     *            the user id
     */
    public FlickrDownloader(final Flickr fl, final String userId) {
        transport = fl.getTransport();
        pi = fl.getPhotosInterface();
        id = userId;
    }

    /**
     * Downloads specified photo from flickr.
     * 
     * @param imageId
     *            the image-id
     * @return the image as BufferedImage
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws FlickrException
     *             the flickr exception
     * @throws JSONException
     *             the jSON exception
     */
    public BufferedImage getImageAsBufferedImage(final String imageId) throws IOException, FlickrException,
        JSONException {
        final Photo ph = pi.getPhoto(imageId);
        return ImageIO.read(getImageAsStream(ph));
    }

    /**
     * Gets the image as stream.
     * 
     * @return the image as stream
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws FlickrException
     *             the flickr exception
     * @throws JSONException
     *             the jSON exception
     */
    public InputStream getImageAsStream() throws IOException, FlickrException, JSONException {
        SearchParameters sp = new SearchParameters();
        sp.setUserId(id);
        sp.setExtras(Extras.ALL_EXTRAS);
        PhotoList phL = pi.search(sp, -1, -1);
        final Photo ph = phL.get(0);
        return getImageAsStream(ph);
    }

    /**
     * Gets a stream of the given photo.
     * 
     * @param ph
     *            the photo
     * @return the image as stream
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws FlickrException
     *             the flickr exception
     * @throws JSONException
     *             the jSON exception
     */
    private InputStream getImageAsStream(final Photo ph) throws IOException, FlickrException, JSONException {
        final URL url = getBiggestSizedPhotoURL(ph);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (transport instanceof REST) {
            if (((REST) transport).isProxyAuth()) {
                conn.setRequestProperty("Proxy-Authorization", "Basic "
                    + ((REST) transport).getProxyCredentials());
            }
        }
        conn.connect();
        return conn.getInputStream();
    }

    /**
     * Return the URL of the biggest sized photo on flickr.
     * 
     * @param ph
     *            the photo
     * @return the biggest sized photo URL
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws FlickrException
     *             the flickr exception
     * @throws JSONException
     *             the jSON exception
     */
    private URL getBiggestSizedPhotoURL(final Photo ph) throws IOException, FlickrException, JSONException {
        final Collection<Size> si = pi.getSizes(ph.getId(), false);
        final int last = si.size() - 1;
        final Size biggest = si.toArray(new Size[last])[last];
        final String urlString = biggest.getSource();
        return new URL(urlString);
    }

}
