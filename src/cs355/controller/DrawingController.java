package cs355.controller;

import cs355.GUIFunctions;
import cs355.model.drawing.*;
import cs355.model.image.CS355Image;
import cs355.model.image.DrawingImage;
import cs355.model.scene.CS355Scene;
import cs355.view.DrawingViewer;
import cs355.view.ViewRefresher;
import cs355.view.ViewportParameters;
import cs355.view.drawing.state.DrawingState;
import cs355.view.drawing.state.InitialState;
import cs355.view.drawing.util.Transform;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.*;

/**
 * A Cront
 */
public class DrawingController implements CS355Controller, MouseListener, MouseMotionListener
{
    private ViewRefresher view;
    private DrawingState state;
    private final CS355Drawing model;
    private CS355Scene scene;
    private CS355Image image;

    public DrawingController()
    {
        model = new DrawingModel();
        state = new InitialState();
        state.setColor(Color.WHITE, model);
        scene = new CS355Scene();
        image = new DrawingImage();
    }

    /* begin CS355Controller methods */
    @Override
    public void colorButtonHit(Color c)
    {
        GUIFunctions.changeSelectedColor(c);
        state.setColor(c, model);
    }

    @Override
    public void lineButtonHit()
    {
        state.lineButtonHit(this);
    }

    @Override
    public void squareButtonHit()
    {
        state.squareButtonHit(this);
    }

    @Override
    public void rectangleButtonHit()
    {
        state.rectangleButtonHit(this);
    }

    @Override
    public void circleButtonHit()
    {
        state.circleButtonHit(this);
    }

    @Override
    public void ellipseButtonHit()
    {
        state.ellipseButtonHit(this);
    }

    @Override
    public void triangleButtonHit()
    {
        state.triangleButtonHit(this);
    }

    @Override
    public void selectButtonHit()
    {
        state.selectButtonHit(this);
    }

    @Override
    public void zoomInButtonHit()
    {
        ((DrawingViewer) view).zoomInButtonHit();
    }

    @Override
    public void zoomOutButtonHit()
    {
        ((DrawingViewer) view).zoomOutButtonHit();
    }

    @Override
    public void hScrollbarChanged(int value)
    {
        ((DrawingViewer) view).hScrollbarChanged(value);
    }

    @Override
    public void vScrollbarChanged(int value)
    {
        ((DrawingViewer) view).vScrollbarChanged(value);
    }

    @Override
    public void openScene(File file)
    {
        if (scene.open(file))
            GUIFunctions.refresh();
        else
            GUIFunctions.printf("Opening Scene failed");
    }

    @Override
    public void toggle3DModelDisplay()
    {
        ((DrawingViewer) view).toggle3DModelDisplay();
        GUIFunctions.refresh();
    }

    @Override
    public void keyPressed(Iterator<Integer> iterator)
    {
        ((DrawingViewer) view).keyPressed(iterator);
    }

    @Override
    public void openImage(File file)
    {
        image.open(file);
    }

    @Override
    public void saveImage(File file)
    {
        image.save(file);
    }

    @Override
    public void toggleBackgroundDisplay()
    {

    }

    @Override
    public void saveDrawing(File file)
    {
        model.save(file);
    }

    @Override
    public void openDrawing(File file)
    {
        model.open(file);
    }

    @Override
    public void doDeleteShape()
    {
        state.deleteShape(getModel());
    }

    @Override
    public void doEdgeDetection()
    {
        image.edgeDetection();
    }

    @Override
    public void doSharpen()
    {
        image.sharpen();
    }

    @Override
    public void doMedianBlur()
    {
        image.medianBlur();
    }

    @Override
    public void doUniformBlur()
    {
        image.uniformBlur();
    }

    @Override
    public void doGrayscale()
    {
        image.grayscale();
    }

    @Override
    public void doChangeContrast(int contrastAmountNum)
    {
        image.contrast(contrastAmountNum);
    }

    @Override
    public void doChangeBrightness(int brightnessAmountNum)
    {
        image.brightness(brightnessAmountNum);
    }

    @Override
    public void doMoveForward()
    {
        state.moveShapeForward(getModel());
    }

    @Override
    public void doMoveBackward()
    {
        state.moveShapeBackward(model);
    }

    @Override
    public void doSendToFront()
    {
        state.moveShapeToFront(model);
    }

    @Override
    public void doSendtoBack()
    {
        state.moveShapeToBack(model);
    }

    /* end CS355Controller methods */

    /* begin MouseListener methods */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        Point2D.Double viewPoint = new Point2D.Double(e.getX(), e.getY());
        Point2D.Double worldPoint = getWorldPointFromViewPoint(viewPoint);
        printRelativePoints("Mouse Clicked: W:(%d,%d), V:(%d,%d)", worldPoint, viewPoint);
        state.mouseClicked(worldPoint, model);
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        Point2D.Double viewPoint = new Point2D.Double(e.getX(), e.getY());
        Point2D.Double worldPoint = getWorldPointFromViewPoint(viewPoint);
        printRelativePoints("Mouse Pressed: W:(%d,%d), V:(%d,%d)", worldPoint, viewPoint);
        state.mousePressed(worldPoint, model);
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        Point2D.Double viewPoint = new Point2D.Double(e.getX(), e.getY());
        Point2D.Double worldPoint = getWorldPointFromViewPoint(viewPoint);
        printRelativePoints("Mouse Released: W:(%d,%d), V:(%d,%d)", worldPoint, viewPoint);
        state.mouseReleased(worldPoint, model);
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        Point2D.Double viewPoint = new Point2D.Double(e.getX(), e.getY());
        //GUIFunctions.printf("Mouse Entered: %s", viewPoint.toString());
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        Point2D.Double viewPoint = new Point2D.Double(e.getX(), e.getY());
        //GUIFunctions.printf("Mouse Exited: %s", viewPoint.toString());
    }

    /* end MouseListener methods */

    /* begin MouseMotionListener methods */
    @Override
    public void mouseDragged(MouseEvent e)
    {
        Point2D.Double viewPoint = new Point2D.Double(e.getX(), e.getY());
        Point2D.Double worldPoint = getWorldPointFromViewPoint(viewPoint);
        printRelativePoints("Mouse Dragged: W:(%d,%d), V:(%d,%d)", worldPoint, viewPoint);
        state.mouseDragged(worldPoint, model);
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        Point2D.Double viewPoint = new Point2D.Double(e.getX(), e.getY());
        //GUIFunctions.printf("Mouse Moved: %s", viewPoint.toString());
    }
    /* end MouseMotionListener methods */

    public void setView(ViewRefresher view)
    {
        this.view = view;

        this.model.deleteObservers();
        this.model.addObserver(view);

        this.scene.deleteObservers();
        this.scene.addObserver(view);

        this.image.deleteObservers();
        this.image.addObserver(view);
    }

    public ViewRefresher getView()
    {
        return view;
    }

    public CS355Drawing getModel()
    {
        return model;
    }

    public void setState(DrawingState state)
    {
        this.state = state;
    }

    private double getScalingFactor()
    {
        return ((DrawingViewer) view).getScalingFactor();
    }

    private Point2D.Double getViewportUpperLeft()
    {
        return ((DrawingViewer) view).getViewportUpperLeft();
    }

    private void printRelativePoints(String message, Point2D.Double worldPoint, Point2D.Double viewPoint)
    {
        GUIFunctions.printf(message, (int) worldPoint.x, (int) worldPoint.y, (int) viewPoint.x, (int) viewPoint.y);
    }

    private Point2D.Double getWorldPointFromViewPoint(Point2D.Double viewPoint)
    {
        return Transform.getWorldPointFromViewPoint(viewPoint, new ViewportParameters(getViewportUpperLeft(), getScalingFactor()));
    }

    public CS355Scene getScene()
    {
        return scene;
    }
}
