/**
 * 
 */
package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.image.BufferedImage;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;

/**
 * This Class offers a byte painter.
 * <p/>
 * Numeral System: 256 <br/>
 * Layers: 1 <br/>
 * 1 Byte = 1 Pixel <br/>
 * 256 colors <br/>
 * <p/>
 * Working with
 * <ul>
 * <li>Picasa</li>
 * </ul>
 * Not working with
 * <ul>
 * <li>Facebook</li>
 * <li>Flickr</li>
 * </ul>
 * 
 * @author Wolfgang Miller, University of Konstanz
 */
public class DihectpenthexagonBytesToImagePainter implements IBytesToImagePainter {

    /**
     * {@inheritDoc}
     */
    @Override
    public int getImageType() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float pixelsPerByte() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage storeBytesInImage(final BufferedImage bi, final byte[] bs) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBytesFromImage(final BufferedImage bi) {
        // TODO Auto-generated method stub
        return null;
    }

}
