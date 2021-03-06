package cs355.view;

import cs355.GUIFunctions;
import cs355.controller.DrawingController;
import cs355.model.drawing.*;
import cs355.model.drawing.Shape;
import cs355.model.image.CS355Image;
import cs355.model.image.DrawingImage;
import cs355.model.scene.*;
import cs355.view.drawing.Drawable3DLine;
import cs355.view.drawing.DrawableNullShape;
import cs355.view.drawing.DrawableShape;
import cs355.view.drawing.util.DrawableShapeFactory;
import cs355.view.drawing.util.Matrix;
import cs355.view.drawing.util.Transform;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * The View in the MVC model. Takes care of all the drawing when the Model changes.
 *
 * @see Observer
 * @see Observable
 * @see DrawingModel
 * @see DrawingController
 */
public class DrawingViewer implements ViewRefresher
{
    private static final Logger LOGGER = Logger.getLogger(DrawingViewer.class.getName());

    //Models
    private CS355Drawing model;
    private CS355Scene scene;
    private CS355Image image;

    private Point2D.Double viewportUpperLeft;
    private double scalingFactor;
    private static final double VIEWPORT_SIZE = 512, HALF_WORLD_SIZE = 1024, MAX_SCALING_FACTOR = 4, MIN_SCALING_FACTOR = .25;
    private final VirtualCamera camera;
    // private List<Shape> specificUpdatedShapes;

    /**
     * Creates a new Viewer.
     * @param scene the 3D scene to render the viewer with.
     */
    public DrawingViewer(CS355Scene scene)
    {
        model = new DrawingModel();
        scalingFactor = 1.0;
        viewportUpperLeft = new Point2D.Double(HALF_WORLD_SIZE, HALF_WORLD_SIZE);
        this.scene = scene;
        camera = new VirtualCamera();
        this.image = new DrawingImage();
    }

    /* begin ViewRefresher methods */
    @Override
    public void refreshView(Graphics2D graphics2D)
    {
        this.drawImage(graphics2D);
        this.drawShapes(graphics2D);
        this.drawScene(graphics2D);
    }

    private void drawImage(Graphics2D graphics)
    {
        AffineTransform affineTransform = Transform.buildWorldToViewTransformation(new ViewportParameters(getViewportUpperLeft(), getScalingFactor()));
        affineTransform.concatenate(AffineTransform.getTranslateInstance(512, 512));
        graphics.drawImage(image.getImage(), affineTransform, null);
    }

    private void drawShapes(Graphics2D graphics)
    {
        //Draw selection handles last
        List<Shape> shapes = model.getShapesReversed();

        DrawableShape selectedShape = new DrawableNullShape(Color.WHITE);
        DrawingParameters drawingParameters = new DrawingParameters(graphics, new ViewportParameters(viewportUpperLeft, scalingFactor));
        for (Shape shape : shapes)
        {
            DrawableShape drawableShape = DrawableShapeFactory.createDrawableShape(shape);
            if (shape.isSelected())
            {
                selectedShape = drawableShape;
            }
            drawableShape.draw(drawingParameters);
        }
        selectedShape.drawOutline(drawingParameters);
    }


    private void drawScene(Graphics2D graphics)
    {
        AffineTransform affineTransform = Transform.buildWorldToViewTransformation(new ViewportParameters(getViewportUpperLeft(), getScalingFactor()));
        graphics.setTransform(affineTransform);
        if (camera.is3DEnabled())
        {
            SceneParser sceneParser = new SceneParser(scene, graphics);
            sceneParser.renderScene();
        }
    }

    /* end ViewRefresher methods */

    /* begin Observer methods */
    @Override
    public void update(Observable o, Object specificShapes)
    {
        if (o instanceof DrawingModel)
            model = (DrawingModel) o;
        else if (o instanceof CS355Scene)
            scene = (CS355Scene) o;
        else if (o instanceof DrawingImage)
            image = (CS355Image) o;
        GUIFunctions.refresh();
    }
    /* end Observer methods */

    public void zoomInButtonHit()
    {
        if (scalingFactor < MAX_SCALING_FACTOR)
        {
            scalingFactor *= 2.0;
            Point2D.Double oldUpperLeft = (Point2D.Double) viewportUpperLeft.clone();
            viewportUpperLeft = new Point2D.Double(oldUpperLeft.x + (VIEWPORT_SIZE / scalingFactor), oldUpperLeft.y + (VIEWPORT_SIZE / scalingFactor));
            doZoom();
        }
    }

    public void zoomOutButtonHit()
    {
        if (scalingFactor > MIN_SCALING_FACTOR)
        {
            scalingFactor /= 2.0;
            Point2D.Double oldUpperLeft = (Point2D.Double) viewportUpperLeft.clone();
            viewportUpperLeft = new Point2D.Double(oldUpperLeft.x - (VIEWPORT_SIZE / scalingFactor), oldUpperLeft.y - (VIEWPORT_SIZE / scalingFactor));
            doZoom();
        }
    }

    public void hScrollbarChanged(int value)
    {
        if (value != 0)
            viewportUpperLeft.x = value;
        GUIFunctions.printf("hScroll: %d", value);
        GUIFunctions.refresh();
    }

    public void vScrollbarChanged(int value)
    {
        if (value != 0)
            viewportUpperLeft.y = value;
        GUIFunctions.printf("vScroll: %d", value);
        GUIFunctions.refresh();
    }

    private void doZoom()
    {
        GUIFunctions.setZoomText(scalingFactor);
        int hBarSize = (int) (VIEWPORT_SIZE / scalingFactor);
        GUIFunctions.setHScrollBarKnob(hBarSize);
        GUIFunctions.setHScrollBarPosit((int) viewportUpperLeft.x);

        int vBarSize = (int) (VIEWPORT_SIZE / scalingFactor);
        GUIFunctions.setVScrollBarKnob(vBarSize);
        GUIFunctions.setVScrollBarPosit((int) viewportUpperLeft.y);

        GUIFunctions.refresh();
    }

    public Point2D.Double getViewportUpperLeft()
    {
        return viewportUpperLeft;
    }

    public double getScalingFactor()
    {
        return scalingFactor;
    }

    public void keyPressed(Iterator<Integer> iterator)
    {
        if (camera.is3DEnabled())
        {
            while (iterator.hasNext())
            {
                camera.keyPressed(iterator.next());
                camera.updateScene(scene);
                GUIFunctions.refresh();
            }
        }
    }

    public void toggle3DModelDisplay()
    {
        camera.toggleCameraMovementEnabled();
    }

    public void setImage(DrawingImage image)
    {
        this.image = image;
    }

    public void setDrawing(DrawingModel drawing)
    {
        this.model = drawing;
    }

    private class SceneParser
    {
        private final List<Instance> instances;
        private final Point3D cameraLocation;
        private final double cameraRotation;
        private final Matrix cameraMatrix;
        private final Graphics2D graphics;

        /**
         * Initializes a SceneParser to render the elements of te scene.
         * @param scene the scene to be used.
         * @param graphics the graphics to draw with.
         */
        public SceneParser(CS355Scene scene, Graphics2D graphics)
        {
            this.instances = scene.instances();
            this.cameraLocation = scene.getCameraPosition();
            this.cameraRotation = scene.getCameraRotation();
            this.cameraMatrix = Transform.buildWorldToCameraTransformation(cameraLocation, cameraRotation);
            this.graphics = graphics;
        }

        /**
         * Renders the entire scene.
         */
        public void renderScene()
        {
            for (Instance instance : instances)
            {
                renderInstance(instance);
            }
        }

        /**
         * Renders all of the relevant information associated with a particular instance.
         * @param instance the instance to render.
         */
        private void renderInstance(Instance instance)
        {
            Matrix objectMatrix = Transform.buildObjectToWorldTransformation(instance.getPosition(), instance.getRotAngle());
            Color color = instance.getColor();
            double scale = instance.getScale();
            WireFrame model = instance.getModel();
            List<Line3D> lines = model.getLines();
            for (Line3D line : lines)
            {
                Drawable3DLine drawableLine = new Drawable3DLine(color, line.start, line.end, objectMatrix, cameraMatrix);
                drawableLine.draw(graphics);
            }
        }
    }
}
