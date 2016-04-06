package cs355.model.image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a 9-tuple set of pixels in an image.
 *
 */
public class ImageKernel
{
    private static final int RED = 0, GREEN = 1, BLUE = 2;

    private final ArrayList<int[]> pixels;

    private ImageKernel(int[] m00, int[] m10, int[] m20,
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
        int index = 0;
        switch (colorType)
        {
            case RED:
                index = 0;
                break;
            case GREEN:
                index = 1;
                break;
            case BLUE:
                index = 2;
                break;
            default:
                assert false;
        }
        List<Integer> colorChannel = new ArrayList<>();
        for (int i = 0; i < pixels.size(); i++)
        {
            colorChannel.add(pixels.get(i)[index]);
        }
        return colorChannel;
    }

    public static int[] noiseRemoval_MedianFilter(int[] m00, int[] m10, int[] m20,
                                                  int[] m01, int[] m11, int[] m21,
                                                  int[] m02, int[] m12, int[] m22)
    {
        ImageKernel imageKernel = new ImageKernel(m00, m10, m20, m01, m11, m21, m02, m12, m22);
        int redMedian = imageKernel.getMedianValue(imageKernel.getColorChannel(RED));
        int greenMedian = imageKernel.getMedianValue(imageKernel.getColorChannel(GREEN));
        int blueMedian = imageKernel.getMedianValue(imageKernel.getColorChannel(BLUE));
        return new int[] {redMedian, greenMedian, blueMedian};
    }

    private int getMedianValue(List<Integer> colorChannel)
    {
        Collections.sort(colorChannel);
        int medianIndex = 4;
        return colorChannel.get(medianIndex);
    }

    public static int[] gradientMagnitude(int[] m00, int[] m10, int[] m20,
                                    int[] m01, int[] m11, int[] m21,
                                    int[] m02, int[] m12, int[] m22)
    {
        int[] sobelX = sobelKernelX(m00, m10, m20, m01, m11, m21, m02, m12, m22);
        int[] sobelY = sobelKernelY(m00, m10, m20, m01, m11, m21, m02, m12, m22);
        int red = (int) Math.sqrt(square(sobelX[RED]) + square(sobelY[RED]));
        int green = (int) Math.sqrt(square(sobelX[GREEN]) + square(sobelY[GREEN]));
        int blue = (int) Math.sqrt(square(sobelX[BLUE]) + square(sobelY[BLUE]));
        return new int[] {red, green, blue};
    }

    private static int square(int number)
    {
        return (int) Math.pow(number, 2);
    }

    private static int[] sobelKernelX(
            int[] m00, int[] m10, int[] m20,
            int[] m02, int[] m12, int[] m22)
    {
        int red = (-1 * m00[RED]) + (-2 * m10[RED]) + (-1 * m20[RED]) + m02[RED] + (2 * m12[RED]) + m22[RED];
        int green = (-1 * m00[GREEN]) + (-2 * m10[GREEN]) + (-1 * m20[GREEN]) + m02[GREEN] + (2 * m12[GREEN]) + m22[GREEN];
        int blue = (-1 * m00[BLUE]) + (-2 * m10[BLUE]) + (-1 * m20[BLUE]) + m02[BLUE] + (2 * m12[BLUE]) + m22[BLUE];
        return new int[] {red,  green, blue};
    }

    private static int[] sobelKernelY(
            int[] m00, int[] m20,
            int[] m01, int[] m21,
            int[] m02, int[] m22)
    {
        int red = (-1 * m00[RED]) + (-2 * m01[RED]) + (-1 * m02[RED]) + m20[RED] + (2 * m21[RED]) + m22[RED];
        int green = (-1 * m00[GREEN]) + (-2 * m01[GREEN]) + (-1 * m02[GREEN]) + m20[GREEN] + (2 * m21[GREEN]) + m22[GREEN];
        int blue = (-1 * m00[BLUE]) + (-2 * m01[BLUE]) + (-1 * m02[BLUE]) + m20[BLUE] + (2 * m21[BLUE]) + m22[BLUE];
        return new int[] {red,  green, blue};
    }

    public static int[] brightnessAdjustment(int[] m00, int[] m10, int[] m20,
                            int[] m01, int[] m11, int[] m21,
                            int[] m02, int[] m12, int[] m22)
    {

    }

    public static int[] contrastAdjustment(int[] m00, int[] m10, int[] m20,
                            int[] m01, int[] m11, int[] m21,
                            int[] m02, int[] m12, int[] m22)
    {

    }

    public static int[] blur(int[] m00, int[] m10, int[] m20,
                            int[] m01, int[] m11, int[] m21,
                            int[] m02, int[] m12, int[] m22)
    {

    }

    public static int[] unsharpMasking(int[] m00, int[] m10, int[] m20,
                            int[] m01, int[] m11, int[] m21,
                            int[] m02, int[] m12, int[] m22)
    {

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
                getM00()[RED], getM00()[GREEN], getM00()[BLUE],/**/getM01()[RED], getM01()[GREEN], getM01()[BLUE],/**/getM02()[RED], getM02()[GREEN], getM02()[BLUE],
                getM10()[RED], getM10()[GREEN], getM10()[BLUE],/**/getM11()[RED], getM11()[GREEN], getM11()[BLUE],/**/getM12()[RED], getM12()[GREEN], getM12()[BLUE],
                getM20()[RED], getM20()[GREEN], getM20()[BLUE],/**/getM21()[RED], getM21()[GREEN], getM21()[BLUE],/**/getM22()[RED], getM22()[GREEN], getM22()[BLUE]);
    }
}
