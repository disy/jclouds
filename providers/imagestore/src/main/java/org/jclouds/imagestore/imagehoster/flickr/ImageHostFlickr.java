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
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jclouds.imagestore.imagehoster.IImageHost;
import org.json.JSONException;
import org.xml.sax.SAXException;

import com.google.inject.Inject;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.googlecode.flickrjandroid.photosets.PhotosetsInterface;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;

/**
 * This class offers an implementation of the IImageHost interface for the image provider Flickr.
 * 
 * @author Wolfgang Miller
 */
public class ImageHostFlickr implements IImageHost {

    /** Dummy Picture for marking deleted albums. */
    private static final BufferedImage DUMMYIMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
    /** Deleted set name. */
    private static final String MARKERFORSET = "MARKER";

    /** The Flickr instance. */
    private Flickr fl;
    /** The flickr user ID. */
    private String userId;
    /** The Flickr uploader. */
    private final FlickrUploader fup;
    /** The Flickr downloader. */
    private final FlickrDownloader fdown;
    /** The Flickr meta data. */
    private final UploadMetaData meta;
    /** The photo set interface. */
    private final PhotosetsInterface psi;
    /** The photos interface. */
    private final PhotosInterface poi;
    /** The maximum image width. */
    private static final int MAX_IMAGE_WIDTH = 2048;
    /** The maximum image height. */
    private static final int MAX_IMAGE_HEIGHT = 2048;

    /**
     * Instantiates a new Flickr IImageHost instance.
     * 
     */
    @Inject
    public ImageHostFlickr() {
        try {
            FlickrOAuth foa = new FlickrOAuth();
            fl = foa.getAuthenticatedFlickrInstance();
            userId = foa.getUser().getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (FlickrException e) {
            throw new RuntimeException(e);
        }
        fup = new FlickrUploader(fl);
        fdown = new FlickrDownloader(fl, userId);
        meta = new UploadMetaData();
        psi = fl.getPhotosetsInterface();
        poi = fl.getPhotosInterface();
    }

    /**
     * Searches for a given image-title in a specified image-set.
     * 
     * @param imageSetId
     *            the flickr set-id
     * @param imageTitle
     *            the image-title
     * @return the flickr Photo
     */
    private Photo getFlickrImage(final String imageSetId, final String imageTitle) {

        // hack to try it multiple times in a row
        Exception thrown = null;
        int i = 10;
        do {
            i--;
            try {
                final PhotoList pl = psi.getPhotos(imageSetId, -1, -1).getPhotoList();
                for (final Photo ph : pl) {
                    if (ph.getTitle().equals(imageTitle)) {
                        return ph;
                    }
                }
                return null;
            } catch (IOException e) {
                thrown = e;
            } catch (FlickrException e) {
                thrown = e;
            } catch (JSONException e) {
                thrown = e;
            }
        } while (i >= 0);
        throw new RuntimeException(thrown);
    }

    /**
     * Searches for set-title and return flickr set-id.
     * 
     * @param imageSetTitle
     *            the image-title
     * @return the flickr Photoset
     */
    private Photoset getFlickrImageSet(final String imageSetTitle) {
        // hack to try it multiple times in a row
        Exception thrown = null;
        int i = 10;
        do {
            i--;
            try {
                final Collection<Photoset> pc = psi.getList(userId).getPhotosets();
                for (final Photoset ps : pc) {
                    if (ps.getTitle().equals(imageSetTitle)) {
                        return ps;
                    }
                }
                return null;
            } catch (IOException e) {
                thrown = e;
            } catch (FlickrException e) {
                thrown = e;
            } catch (JSONException e) {
                thrown = e;
            }
        } while (i >= 0);
        throw new RuntimeException(thrown);
    }

    /**
     * Returns flickr set-id from given set-title.
     * 
     * @param imageSetTitle
     *            the image-title
     * @return the flickr set-id
     */
    private String getFlickrImageSetId(final String imageSetTitle) {
        Photoset ps = getFlickrImageSet(imageSetTitle);
        return ps == null ? "" : ps.getId();
    }

    /**
     * Returns flickr image-id form given set-id and image-title.
     * 
     * @param imageSetId
     *            the flickr set-id
     * @param imageSetTitle
     *            the set-title
     * @return the flickr image-id
     */
    private String getFlickrImageId(final String imageSetId, final String imageSetTitle) {
        Photo ph = getFlickrImage(imageSetId, imageSetTitle);
        return ph == null ? "" : ph.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createImageSet(final String imageSetTitle) {
        return createImageSetAndGetSet(imageSetTitle) != null;
    }

    /**
     * Creates a Photoset with the given image-title and returns it's id. Returns null if a Photoset with
     * given name already exists.
     * 
     * @param imageSetTitle
     *            the set-title
     * @return the Photoset
     */
    private Photoset createImageSetAndGetSet(final String imageSetTitle) {
        if (imageSetExists(imageSetTitle)) {
            return null;
        }

        final String dummyID = uploadImage(MARKERFORSET, DUMMYIMAGE);
        // hack to try it multiple times in a row
        Exception thrown = null;
        int i = 10;
        do {
            i--;
            try {
                return psi.create(imageSetTitle, "", dummyID);
            } catch (IOException e) {
                thrown = e;
            } catch (FlickrException e) {
                thrown = e;
            } catch (JSONException e) {
                thrown = e;
            }
        } while (i >= 0);
        throw new RuntimeException(thrown);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean imageExists(final String imageSetTitle, final String imageTitle) {
        String imageSetId = getFlickrImageSetId(imageSetTitle);
        if (imageSetId.isEmpty())
            return false;
        return !getFlickrImageId(imageSetId, imageTitle).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean imageSetExists(final String imageSetTitle) {
        return !getFlickrImageSetId(imageSetTitle).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteImage(final String imageSetTitle, final String imageTitle) {
        final String imageSetId = getFlickrImageSetId(imageSetTitle);
        final String imageId = getFlickrImageId(imageSetId, imageTitle);
        if (imageExists(imageSetTitle, imageTitle)) {
            // hack to try it multiple times in a row
            Exception thrown = null;
            int i = 10;
            do {
                i--;
                try {
                    poi.delete(imageId);
                    return true;
                } catch (IOException e) {
                    thrown = e;
                } catch (FlickrException e) {
                    thrown = e;
                } catch (JSONException e) {
                    thrown = e;
                }
            } while (i >= 0);
            throw new RuntimeException(thrown);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteImageSet(final String imageSetTitle) {
        final String imageSetId = getFlickrImageSetId(imageSetTitle);

        if (imageSetId.isEmpty())
            return false;

        // hack to try it multiple times in a row
        Exception thrown = null;
        int i = 10;
        do {
            i--;
            try {
                final PhotoList pl = psi.getPhotos(imageSetId, -1, -1).getPhotoList();
                for (final Photo ph : pl) {
                    poi.delete(ph.getId());
                }
                return true;
            } catch (IOException e) {
                thrown = e;
            } catch (FlickrException e) {
                thrown = e;
            } catch (JSONException e) {
                thrown = e;
            }
        } while (i >= 0);
        throw new RuntimeException(thrown);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean uploadImage(final String imageSetTitle, final String imageTitle, final BufferedImage img) {
        if (imageExists(imageSetTitle, imageTitle)) {
            return false;
        }
        final String imageId = uploadImage(imageTitle, img);

        Photoset ps = createImageSetAndGetSet(imageSetTitle);
        if (ps == null) {
            ps = getFlickrImageSet(imageSetTitle);
        }
        // hack to try it multiple times in a row
        Exception thrown = null;
        int i = 10;
        do {
            i--;
            try {
                psi.addPhoto(ps.getId(), imageId);
                return true;
            } catch (FlickrException e) {
                thrown = e;
            } catch (JSONException e) {
                thrown = e;
            } catch (IOException e) {
                thrown = e;
            }
        } while (i >= 0);
        throw new RuntimeException(thrown);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage downloadImage(final String imageSetTitle, final String imageTitle) {
        final String imageSetId = getFlickrImageSetId(imageSetTitle);
        final Photo ph = getFlickrImage(imageSetId, imageTitle);
        // hack to try it multiple times in a row
        Exception thrown = null;
        int i = 10;
        do {
            i--;
            try {
                return fdown.getImageAsBufferedImage(ph);
            } catch (IOException e) {
                thrown = e;
            } catch (FlickrException e) {
                thrown = e;
            } catch (JSONException e) {
                thrown = e;
            } catch (NullPointerException e) {
                return null;
            }
        } while (i >= 0);
        throw new RuntimeException(thrown);
    }

    private String uploadImage(final String imageTitle, final BufferedImage image) {
        // hack to try it multiple times in a row
        Exception thrown = null;
        int i = 10;
        do {
            i--;
            try {
                return fup.uploadImage(imageTitle, image, meta);
            } catch (IOException e) {
                thrown = e;
            } catch (FlickrException e) {
                thrown = e;
            } catch (SAXException e) {
                thrown = e;
            }
        } while (i >= 0);
        throw new RuntimeException(thrown);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> imageSetContent(final String imageSetTitle) {
        Set<String> returnVal = new HashSet<String>();

        final Photoset ps = getFlickrImageSet(imageSetTitle);

        if (ps == null) {
            return returnVal;
        }

        // hack to try it multiple times in a row
        Exception thrown = null;
        int i = 10;
        do {
            i--;
            try {
                PhotoList list = psi.getPhotos(ps.getId(), 1000, 0).getPhotoList();
                for (Photo photo : list) {
                    if (!photo.getTitle().equals(MARKERFORSET)) {
                        returnVal.add(photo.getTitle());
                    }
                }
                return returnVal;
            } catch (IOException exc) {
                thrown = exc;
            } catch (FlickrException exc) {
                thrown = exc;
            } catch (JSONException exc) {
                thrown = exc;
            }
        } while (i >= 0);
        throw new RuntimeException(thrown);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clearImageSet(final String imageSetTitle) {
        final String imageSetId = getFlickrImageSetId(imageSetTitle);

        // hack to try it multiple times in a row
        Exception thrown = null;
        int i = 10;
        do {
            i--;
            try {
                if (!imageSetId.isEmpty()) {
                    final PhotoList pl = psi.getPhotos(imageSetId, -1, -1).getPhotoList();
                    for (final Photo ph : pl) {
                        psi.removePhoto(imageSetId, ph.getId());
                    }
                    return true;
                }
                return false;
            } catch (IOException e) {
                thrown = e;
            } catch (FlickrException e) {
                thrown = e;
            } catch (JSONException e) {
                thrown = e;
            }
        } while (i > 0);
        throw new RuntimeException(thrown);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxImageWidth() {
        return MAX_IMAGE_WIDTH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxImageHeight() {
        return MAX_IMAGE_HEIGHT;
    }
}
