/**
 * 
 */
package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.image.BufferedImage;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;

/**
 * @author Wolfgang Miller, University of Konstanz
 *
 */
public class DihectpenthexagonBytesToImagePainter implements IBytesToImagePainter{

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
    public BufferedImage storeBytesInImage(BufferedImage bi, byte[] bs) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBytesFromImage(BufferedImage bi) {
        // TODO Auto-generated method stub
        return null;
    }

    
}
