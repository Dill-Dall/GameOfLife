package com.example.martinstromolsen.goq;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import Model.GoLBoard;
import Model.StaticGoLBoard;

/**
 * Created by MartinStromOlsen on 21/03/2017.
 */

/**
 * Logic related to visualizing the <code>GoLBoard</code>.
 */
public class GameViewer extends View {

    private StaticGoLBoard staticGBoard;
    private GoLBoard goLBoard;
    private Paint cellPaint;
    private Paint backgroundColor;
    private int cellColor = Color.BLACK;

    /**
     * Constructor that takes the <code>Context</code> and <code>AttributeSet</code> as parameters.
     * Passing the parameters to the superclass constructor and setting up
     * the <code>Paint</code> needed.
     * @param context <code>Context</code> passed as an argument
     * @param as <code>AttributeSet</code> the attribute set to use
     */
    public GameViewer(Context context, AttributeSet as)
    {
        super(context, as);
        setupColors();
    }

    /**
     * Constructor that takes only the <code>Context</code> as parameter and passes this to the
     * superclass constructor and setting up the <code>Paint</code> needed.
     * @param context <code>Context</code> passed as an argument
     */
    public GameViewer(Context context)
    {
        super(context);
        setupColors();
    }

    /**
     * Sets an existing <code>GBoard</code> to be visualized.
     * @param goLBoard the <code>GBoard</code> to display
     */
    protected void setGBoard(GoLBoard goLBoard)
    {
        this.goLBoard = goLBoard;
    }

    /**
     * Uses a <code>byte[][]</code> from an image to be visualized and initializes a new instance of
     * <code>GBoard</code> to represent the <code>byte[][]</code> from the image.
     * <code>invalidate</code> is called to draw the the new board after initialization.
     * @param byteArray <code>byte[][]</code>-representation of the image taken with the camera
     */
    protected void setGBoardFromImage(byte[][] byteArray)
    {
        if(goLBoard != null)
        {
            goLBoard = null;
        }

        goLBoard = new StaticGoLBoard(byteArray);
        invalidate();
    }

    /**
     * Draws the background as a rectangle with background color being
     * <code>Color.TRANSPARENT</code>. An implementation which allows changing background color
     * could allow the color of the paint <code>backgroundColor</code> to change. The
     * <code>draw</code>-method of the <code>gBoard</code> is then called with the
     * <code>canvas</code> and <code>cellPaint</code> as parameters in order for the
     * <code>gBoard</code> to draw itself.
     * @param canvas the canvas to place the drawing on
     * @see #backgroundColor
     * @see #goLBoard
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Quadratic board with bottom = getWidth()
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundColor);

        if(goLBoard != null)
        {
            goLBoard.draw(canvas, cellPaint);
        }
    }

    /**
     * Calls the superclass method with parameters when size of view is changed.
     *
     * Source: https://developer.android.com/training/custom-views/create-view.html
     * @param width current width
     * @param height current height
     * @param oldWidth old width
     * @param oldHeight old height
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight)
    {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
    }

    /**
     * Initializes the <code>cellPaint</code> and the <code>backgroundColor</code>, both are
     * initialized to their default; black and transparent, respectively.
     */
    private void setupColors()
    {
        cellPaint = new Paint();
        cellPaint.setColor(cellColor);
        cellPaint.setStyle(Paint.Style.FILL);

        backgroundColor = new Paint();
        backgroundColor.setColor(Color.TRANSPARENT);
    }

    /**
     * Gets the current <code>gBoard</code>.
     * @return the current <code>gBoard</code>
     */
    public GoLBoard getGoLBoard()
    {
        return this.goLBoard;
    }

    /**
     * Changes the color for the <code>cellPaint</code> and calls <code>invalidate</code> to redraw
     * the board.
     * @param color the new color to be used
     * @see #cellPaint
     * @see #cellColor
     */
    public void setCellColor(int color)
    {
        cellPaint.setColor(color);
        invalidate();
    }

    /**
     * The method called by the animation, this method calls the <code>setNextGeneration</code> of
     * the <code>gBoard</code> and then redraws the board.
     * @see #goLBoard
     */
    public void setNextGeneration() {
        goLBoard.setNextGeneration();
        invalidate();
    }




}
