/*
 * 
 */
package org.jclouds.imagestore.blobstore.flickr;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;

import org.jclouds.imagestore.blobstore.ImageHost;
import org.json.JSONException;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.googlecode.flickrjandroid.photosets.PhotosetsInterface;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;

// TODO: Auto-generated Javadoc
/**
 * The Class ImageHoster_Flickr.
 */
public class ImageHostFlickr implements ImageHost {

    /** The fl. */
    private final Flickr fl;

    /** The flickr-user ID. */
    private final String userId;

    /** The fup. */
    private final FlickrUploader fup;

    /** The fdown. */
    private final FlickrDownloader fdown;

    /** The meta. */
    private final UploadMetaData meta;

    private final PhotosetsInterface fsi;

    /**
     * Instantiates a new image hoster_ flickr.
     * 
     * @param foa
     *            the Flickr OAuth instance
     */
    public ImageHostFlickr(final FlickrOAuth foa) {
        fl = foa.getFlickr();
        userId = foa.getUser().getId();
        fup = new FlickrUploader(fl);
        fdown = new FlickrDownloader(fl, userId);
        meta = new UploadMetaData();
        fsi = fl.getPhotosetsInterface();
    }

    /**
     * Searches for a given image-title in a specified image-set.
     * 
     * @param imageSetId
     *            the flickr set-id
     * @param imageTitle
     *            the image-title
     * @return the flickr image-id
     */
    private String getFlickrImageId(final String imageSetId, final String imageTitle) {
        try {
            PhotoList pl = fsi.getPhotos(imageSetId, -1, -1);
            for (Photo ph : pl) {
                if (ph.getTitle().equals(imageTitle)) {
                    return ph.getId();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FlickrException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Searches for set-title and return flickr-set-id.
     * 
     * @param imageSetTitle
     *            the image-title
     * @return the flickr set-id
     */
    private String getFlickrImageSetId(final String imageSetTitle) {
        try {
            Collection<Photoset> pc = fsi.getList(userId).getPhotosets();
            for (Photoset ps : pc) {
                if (ps.getTitle().equals(imageSetTitle)) {
                    return ps.getId();
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FlickrException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean createImageSet(String imageSetTitle) {
        if (imageSetExists(imageSetTitle))
            return false;
        
        final BufferedImage dummyImage = new BufferedImage(10, 10, BufferedImage.TYPE_BYTE_BINARY);
        
        final String dummyID = uploadImage("Dummy_" + Long.toString(System.currentTimeMillis()), dummyImage);
        try {
            fsi.create(imageSetTitle, "", dummyID);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FlickrException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean imageExists(String imageSetTitle, String imageTitle) {
        String imageSetId = getFlickrImageSetId(imageSetTitle);
        if (imageSetId.isEmpty())
            return false;
        return !getFlickrImageId(imageSetId, imageTitle).isEmpty();
    }

    @Override
    public boolean imageSetExists(String imageSetTitle) {
        return !getFlickrImageSetId(imageSetTitle).isEmpty();
    }

    @Override
    public void deleteImage(String imageSetTitle, String imageTitle) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean deleteAndVerifyImageSetGone(String imageSetTitle) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String uploadImage(String imageSetTitle, String imageTitle, BufferedImage img) {
        final String imageId = uploadImage(imageTitle, img);
        
        if(!imageSetExists(imageSetTitle)){
            createImageSet(imageSetTitle);
        }
        
        String imageSetId = getFlickrImageSetId(imageSetTitle);
        

        try {
            fsi.addPhoto(imageSetId, imageId);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FlickrException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return imageId;
    }

    @Override
    public BufferedImage downloadImage(String imageSetTitle, String imageTitle) {
        final String imageSetId = getFlickrImageSetId(imageSetTitle);
        final String imageId = getFlickrImageId(imageSetId, imageTitle);
        try {
            return fdown.getImageAsBufferedImage(imageId);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FlickrException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String uploadImage(String imageTitle, BufferedImage img) {
        return fup.uploadImage(imageTitle, img, meta);
    }
    
}
