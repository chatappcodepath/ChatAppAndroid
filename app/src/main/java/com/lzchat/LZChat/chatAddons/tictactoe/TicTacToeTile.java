package com.lzchat.LZChat.chatAddons.tictactoe;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by patelkev on 11/25/16.
 */

public class TicTacToeTile extends ImageView implements TicTacToeDrawable {

    // Constants
    public static final int DEFAULT_PAINT_WIDTH = 10;

    // Borders
    private Paint mBorderPaint;
    private boolean mBorderLeft = false;
    private boolean mBorderTop = false;
    private boolean mBorderRight = false;
    private boolean mBorderBottom = false;

    // Drawables
    private static Drawable drawableX;
    private static Drawable drawableO;
    private static Drawable drawableBlank;

    // Game related
    private int mState;
    private int mRow;
    private int mCol;

    public TicTacToeTile(Context context) {
        super(context);
        initTile();
    }

    public TicTacToeTile(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Get the border information
        TypedArray a = context.obtainStyledAttributes(attrs, com.lzchat.firebase.codelab.friendlychat.R.styleable.TicTacToeTile, 0,0);

        mBorderLeft = a.getBoolean(
                com.lzchat.firebase.codelab.friendlychat.R.styleable.TicTacToeTile_borderLeft,
                false);
        mBorderRight = a.getBoolean(
                com.lzchat.firebase.codelab.friendlychat.R.styleable.TicTacToeTile_borderRight,
                false);
        mBorderTop = a.getBoolean(
                com.lzchat.firebase.codelab.friendlychat.R.styleable.TicTacToeTile_borderTop,
                false);
        mBorderBottom = a.getBoolean(
                com.lzchat.firebase.codelab.friendlychat.R.styleable.TicTacToeTile_borderBottom,
                false);

        // Get the row and column information
        mRow = a.getInteger(com.lzchat.firebase.codelab.friendlychat.R.styleable.TicTacToeTile_row, 0);
        mCol = a.getInteger(com.lzchat.firebase.codelab.friendlychat.R.styleable.TicTacToeTile_col, 0);

        initTile();
    }

    private void initTile() {

        // Create the paint
        mBorderPaint = new Paint();
        mBorderPaint.setStrokeWidth(getContext().getResources()
                .getDisplayMetrics().density
                * DEFAULT_PAINT_WIDTH);
        mBorderPaint.setColor(Color.BLACK);

        // Default the state to empty
        mState = TicTacToeConstants.TILE_STATE_EMPTY;


    }

    public static void setDrawableX(Drawable x) {
        drawableX = x;
    }

    public static void setDrawableO(Drawable o) {
        drawableO = o;
    }

    public static void setDrawableBlank(Drawable blank) {
        drawableBlank = blank;
    }

    public int getRow() {
        return mRow;
    }

    public int getCol() {
        return mCol;
    }

    @Override
    public void setState(int state) {
        mState = state;

        if(state == TicTacToeConstants.TILE_STATE_X) {
            setImageDrawable(drawableX);
        } else if (state == TicTacToeConstants.TILE_STATE_O) {
            setImageDrawable(drawableO);
        } else {
            setImageDrawable(drawableBlank);
        }

        // force a redraw
        invalidate();

    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Dimensions
        int mWidth = getWidth();
        int mHeight = getHeight();

        // Always call the super
        super.onDraw(canvas);

        // Always draw the borders
        if (mBorderRight == true)
            canvas.drawLine(mWidth, 0, mWidth, mHeight, mBorderPaint);
        if (mBorderLeft == true)
            canvas.drawLine(0, 0, 0, mHeight, mBorderPaint);
        if (mBorderTop == true)
            canvas.drawLine(0, 0, mWidth, 0, mBorderPaint);
        if (mBorderBottom == true)
            canvas.drawLine(0, mHeight, mWidth, mHeight, mBorderPaint);

    }


}
