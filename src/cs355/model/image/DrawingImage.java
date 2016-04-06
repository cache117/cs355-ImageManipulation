package cs355.model.image;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Created by cstaheli on 3/30/2016.
 */
public class DrawingImage extends CS355Image
{
    @Override
    public BufferedImage getImage()
    {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = image.getRaster();
        for (int y = 0; y < getHeight(); y++)
        {
            for (int x = 0; x < getWidth(); x++)
            {
                int[] rgb = getPixel(x, y, new int[3]);
                raster.setPixel(x, y, rgb);
            }
        }
        return image;
    }

    @Override
    public void edgeDetection()
    {
        this.notifyObservers();
    }

    @Override
    public void sharpen()
    {
        this.notifyObservers();
    }

    @Override
    public void medianBlur()
    {
        //Take 8 surrounding points, and pick the middle
        this.notifyObservers();
    }

    @Override
    public void uniformBlur()
    {
        this.notifyObservers();
    }

    @Override
    public void grayscale()
    {
        this.notifyObservers();
    }

    @Override
    public void contrast(int amount)
    {
        this.notifyObservers();
    }

    @Override
    public void brightness(int amount)
    {
        this.notifyObservers();
    }

    @Override
    public void notifyObservers()
    {
        super.setChanged();
        super.notifyObservers();
    }

    private void getSurroundingKernel(int x, int y)
    {

    }
}
