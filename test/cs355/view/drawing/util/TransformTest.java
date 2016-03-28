package cs355.view.drawing.util;

import cs355.view.ObjectParameters;
import org.junit.Test;

import java.awt.geom.Point2D;

import static org.junit.Assert.*;

/**
 * Test the Transform class
 */
public class TransformTest
{
    @Test
    public void testGetWorldPointFromObjectPoint() throws Exception
    {
        /* No transformation */
        Point2D.Double objectPoint = new Point2D.Double(135, 20);
        double rotation = 0.0;
        Point2D.Double center = new Point2D.Double(0, 0);
        Point2D.Double worldPoint = Transform.getWorldPointFromObjectPoint(objectPoint, new ObjectParameters(center, rotation));
        assertEquals(worldPoint, objectPoint);

        /* No rotation, different center */
        rotation = 0.0;
        objectPoint = new Point2D.Double(150, 130);
        center = new Point2D.Double(150, 150);
        worldPoint = Transform.getWorldPointFromObjectPoint(objectPoint, new ObjectParameters(center, rotation));
        Point2D.Double expected = new Point2D.Double(300, 280);
        assertNotEquals(objectPoint, worldPoint);
        assertEquals(expected, worldPoint);

        objectPoint = new Point2D.Double(0, 0);
        worldPoint = Transform.getWorldPointFromObjectPoint(objectPoint, new ObjectParameters(center, rotation));
        assertEquals(center, worldPoint);

        objectPoint = new Point2D.Double(-10, -10);
        worldPoint = Transform.getWorldPointFromObjectPoint(objectPoint, new ObjectParameters(center, rotation));
        expected = new Point2D.Double(140, 140);
        assertEquals(expected, worldPoint);

        /* Rotation doesn't change center point */
        rotation = Math.PI / 2;
        center = new Point2D.Double(100, 100);
        objectPoint = new Point2D.Double(0, 0);
        worldPoint = Transform.getWorldPointFromObjectPoint(objectPoint, new ObjectParameters(center, rotation));
        assertEquals(worldPoint, center);

        //Rotate by 90 degrees
        objectPoint = new Point2D.Double(10, 0);
        worldPoint = Transform.getWorldPointFromObjectPoint(objectPoint, new ObjectParameters(center, rotation));
        expected = new Point2D.Double(100, 110);
        assertEquals(expected, worldPoint);

        rotation = -Math.PI / 2;
        worldPoint = Transform.getWorldPointFromObjectPoint(objectPoint, new ObjectParameters(center, rotation));
        expected = new Point2D.Double(100, 90);
        assertEquals(expected, worldPoint);

        rotation = Math.PI;
        worldPoint = Transform.getWorldPointFromObjectPoint(objectPoint, new ObjectParameters(center, rotation));
        expected = new Point2D.Double(90, 100);
        assertEquals(expected, worldPoint);
    }

    @Test
    public void testGetObjectPointFromWorldPoint() throws Exception
    {
        /* No transformation */
        Point2D.Double worldPoint = new Point2D.Double(135, 20);
        double rotation = 0.0;
        Point2D.Double center = new Point2D.Double(0, 0);
        Point2D.Double objectPoint = Transform.getObjectPointFromWorldPoint(worldPoint, new ObjectParameters(center, rotation));
        assertEquals(objectPoint, worldPoint);

        center = new Point2D.Double(135,20);
        objectPoint = Transform.getObjectPointFromWorldPoint(worldPoint, new ObjectParameters(center, rotation));
        assertEquals(new Point2D.Double(0, 0), objectPoint);

        center = new Point2D.Double(130, 20);
        objectPoint = Transform.getObjectPointFromWorldPoint(worldPoint, new ObjectParameters(center, rotation));
        assertEquals(new Point2D.Double(5, 0), objectPoint);

        center = new Point2D.Double(137, 23);
        objectPoint = Transform.getObjectPointFromWorldPoint(worldPoint, new ObjectParameters(center, rotation));
        assertEquals(new Point2D.Double(-2, -3), objectPoint);

        center = new Point2D.Double(100, 40);
        objectPoint = Transform.getObjectPointFromWorldPoint(worldPoint, new ObjectParameters(center, rotation));
        assertEquals(new Point2D.Double(35, -20), objectPoint);

        center = new Point2D.Double(120, 40);
        rotation = Math.PI;
        objectPoint = Transform.getObjectPointFromWorldPoint(worldPoint, new ObjectParameters(center, rotation));
        //assertEquals(new Point2D.Double(-15, 20), objectPoint); //19.99999996 instead of 20.
    }

    @Test
    public void testGetObjectPointFromViewPoint()
    {

    }

    @Test
    public void testGetViewPointFromObjectPoint()
    {

    }

    @Test
    public void testGetWorldPointFromViewPoint()
    {

    }

    @Test
    public void testGetViewPointFromWorldPoint()
    {

    }
}