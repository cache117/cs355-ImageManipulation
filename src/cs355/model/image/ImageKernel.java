package cs355.model.image;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cs355.model.image.DrawingImage.*;

/**
 * Represents a 9-tuple set of pixels in an image.
 *
 */
public class ImageKernel
{

    private final ArrayList<int[]> pixels;

    public ImageKernel(int[] m00, int[] m10, int[] m20,
                        int[] m01, int[] m11, int[] m21,
                        int[] m02, int[] m12, int[] m22)
    {
        pixels = new ArrayList<>();
        pixels.add(m00);
        pixels.add(m10);
        pixels.add(m20);
        pixels.add(m01);
        pixels.add(m11);
        pixels.add(m21);
        pixels.add(m02);
        pixels.add(m12);
        pixels.add(m22);
    }

    private List<Integer> getColorChannel(int colorType)
    {
        List<Integer> colorChannel = new ArrayList<>();
        for (int i = 0; i < pixels.size(); ++i)
        {
            colorChannel.add(pixels.get(i)[colorType]);
        }
        return colorChannel;
    }

    public int[] medianBlur(int[] medianValue)
    {
        int redMedian = getMedianValue(getColorChannel(RGB_RED));
        int greenMedian = getMedianValue(getColorChannel(RGB_GREEN));
        int blueMedian = getMedianValue(getColorChannel(RGB_BLUE));
        //get closest actual pixel (least square distance (sum of square distances of each color. Like an xyz)
        medianValue[RGB_RED] = redMedian;
        medianValue[RGB_GREEN] = greenMedian;
        medianValue[RGB_BLUE] = blueMedian;
        int[] closestValue = getM00();
        double lowestSquareDistance = getSquaredDistance(closestValue, medianValue);
        for (int i = 1; i < pixels.size(); ++i)
        {
            int[] currentPixel = pixels.get(i);
            double squareDistance = getSquaredDistance(currentPixel, medianValue);
            if (squareDistance < lowestSquareDistance)
                closestValue = currentPixel;
        }
        return closestValue;
    }

    private int getMedianValue(List<Integer> colorChannel)
    {
        Collections.sort(colorChannel);
        int medianIndex = 4;
        return colorChannel.get(medianIndex);
    }

    private double getSquaredDistance(int[] pixel, int[] medianValue)
    {
        return square(pixel[RGB_RED] - medianValue[RGB_RED]) + square(pixel[RGB_GREEN] - medianValue[RGB_GREEN]) + square(pixel[RGB_BLUE] - medianValue[RGB_BLUE]);
    }

    public int[] uniformBlur(int[] rgb)
    {
        int redMean = getMeanValue(getColorChannel(RGB_RED));
        int greenMean = getMeanValue(getColorChannel(RGB_GREEN));
        int blueMean = getMeanValue(getColorChannel(RGB_BLUE));
        rgb[RGB_RED] = redMean;
        rgb[RGB_GREEN] = greenMean;
        rgb[RGB_BLUE] = blueMean;
        return rgb;
    }

    private int getMeanValue(List<Integer> colorChannel)
    {
        int total = 0;
        for (Integer each: colorChannel)
        {
            total += each;
        }
        return (int) Math.round(total / 9d);
    }

    public int[] edgeDetection(int[] rgb)
    {
        float m00 = getBrightnessFromRGB(getM00());
        float m01 = getBrightnessFromRGB(getM01());
        float m02 = getBrightnessFromRGB(getM02());
        float m10 = getBrightnessFromRGB(getM10());
        float m12 = getBrightnessFromRGB(getM12());
        float m20 = getBrightnessFromRGB(getM20());
        float m21 = getBrightnessFromRGB(getM21());
        float m22 = getBrightnessFromRGB(getM22());
        float sobelX = sobelKernelX(m00, m20, m01, m21, m02, m22);
        float sobelY = sobelKernelY(m00, m10, m20, m02, m12, m22);
        float newBrightness = (float) Math.sqrt(square(sobelX) + square(sobelY));
        newBrightness = clipValue(newBrightness, 0f, 1f);
        int value = (int) (newBrightness * 255);
        rgb[RGB_RED] = value;
        rgb[RGB_GREEN] = value;
        rgb[RGB_BLUE] = value;
        return rgb;
    }

    private float getBrightnessFromRGB(int[] rgb)
    {
        return Color.RGBtoHSB(rgb[RGB_RED], rgb[RGB_GREEN], rgb[RGB_BLUE], null)[HSB_BRIGHTNESS];
    }

    private static float square(float number)
    {
        return (float) Math.pow(number, 2);
    }

    private static float sobelKernelX(
            float m00, float m20,
            float m01, float m21,
            float m02, float m22
    )
    {
        return ((-1 * m00) + (-2 * m01) + (-1 * m02) + m20 + (2 * m21) + m22) / 8f;
    }

    private static float sobelKernelY(
            float m00, float m10, float m20,
            float m02, float m12, float m22)
    {
        return ((-1 * m00) + (-2 * m10) + (-1 * m20) + m02+ (2 * m12) + m22) / 8f;
    }

    public int[] sharpen(int[] rgb)
    {
        for (int i = 0; i < 3; ++i)
        {
            double value = ((6 * getM11()[i]) + (-1 * getM01()[i]) + (-1 * getM10()[i]) + (-1 * getM21()[i]) + (-1 * getM12()[i])) /2.0;
            value = clipValue(value, 0d, 255d);
            rgb[i] = (int) value;
        }
        return rgb;
    }

    public int[] getM00()
    {
        return pixels.get(0);
    }

    public int[] getM10()
    {
        return pixels.get(1);
    }

    public int[] getM20()
    {
        return pixels.get(2);
    }

    public int[] getM01()
    {
        return pixels.get(3);
    }

    public int[] getM11()
    {
        return pixels.get(4);
    }

    public int[] getM21()
    {
        return pixels.get(5);
    }

    public int[] getM02()
    {
        return pixels.get(6);
    }

    public int[] getM12()
    {
        return pixels.get(7);
    }

    public int[] getM22()
    {
        return pixels.get(8);
    }

    @Override
    public String toString()
    {
        return String.format(
                "|(%d, %d, %d) (%d, %d, %d)  (%d, %d, %d)|\n" +
                "|(%d, %d, %d) (%d, %d, %d)  (%d, %d, %d)|\n" +
                "|(%d, %d, %d) (%d, %d, %d)  (%d, %d, %d)|",
                getM00()[RGB_RED], getM00()[RGB_GREEN], getM00()[RGB_BLUE],/**/getM01()[RGB_RED], getM01()[RGB_GREEN], getM01()[RGB_BLUE],/**/getM02()[RGB_RED], getM02()[RGB_GREEN], getM02()[RGB_BLUE],
                getM10()[RGB_RED], getM10()[RGB_GREEN], getM10()[RGB_BLUE],/**/getM11()[RGB_RED], getM11()[RGB_GREEN], getM11()[RGB_BLUE],/**/getM12()[RGB_RED], getM12()[RGB_GREEN], getM12()[RGB_BLUE],
                getM20()[RGB_RED], getM20()[RGB_GREEN], getM20()[RGB_BLUE],/**/getM21()[RGB_RED], getM21()[RGB_GREEN], getM21()[RGB_BLUE],/**/getM22()[RGB_RED], getM22()[RGB_GREEN], getM22()[RGB_BLUE]);
    }
}
