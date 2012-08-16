/*
 * 
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

// TODO: Auto-generated Javadoc
/**
 * The Class FlickrDownloader.
 */
public class FlickrDownloader {

	/** The photo interface. */
	private final PhotosInterface pi;
	/** The transport-reference. */
	private final Transport transport;
	/** The user id. */
	private final String id;

	/**
	 * Instantiates a new flickr downloader.
	 *
	 * @param f the f
	 * @param userID the user id
	 */
	public FlickrDownloader(final Flickr f, final String userId) {
		transport = f.getTransport();
		pi = f.getPhotosInterface();
		id = userId;
	}
	
	/**
	 * Downloads specified photo from flickr.
	 * 
	 * @param imageId the image-id
	 * @return the image as BufferedImage
	 * @throws IOException
	 * @throws FlickrException
	 * @throws JSONException
	 */
	public BufferedImage getImageAsBufferedImage(String imageId) throws IOException, FlickrException, JSONException{
	    final Photo ph = pi.getPhoto(imageId);
	    return ImageIO.read(getImageAsStream(ph));
	}

	/**
	 * Gets the image as stream.
	 *
	 * @return the image as stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws FlickrException the flickr exception
	 * @throws JSONException the jSON exception
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
	 * Gets the image as stream.
	 *
	 * @param ph the ph
	 * @return the image as stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws FlickrException the flickr exception
	 * @throws JSONException the jSON exception
	 */
	private InputStream getImageAsStream(final Photo ph)
			throws IOException, FlickrException, JSONException {
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
	 * Gets the biggest sized photo url.
	 *
	 * @param ph the ph
	 * @return the biggest sized photo url
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws FlickrException the flickr exception
	 * @throws JSONException the jSON exception
	 */
	private URL getBiggestSizedPhotoURL(final Photo ph)
			throws IOException, FlickrException, JSONException {
		final Collection<Size> si = pi.getSizes(ph.getId(), false);
		// for(Size ss : si){
		// System.out.println(ss.getSource());
		// }
		final int last = si.size() - 1;
		final Size biggest = si.toArray(new Size[last])[last];
		final String urlString = biggest.getSource();
		return new URL(urlString);
	}

}
