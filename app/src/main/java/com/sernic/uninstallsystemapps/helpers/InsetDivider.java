/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Nicola Serlonghi <nicolaserlonghi@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, SUBJECT to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sernic.uninstallsystemapps.helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.sernic.uninstallsystemapps.R;

import androidx.annotation.ColorInt;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class InsetDivider extends RecyclerView.ItemDecoration {

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private Paint mPaint;
    // in pixel
    private int mDividerHeight;
    // left inset for vertical list, top inset for horizontal list
    private int mFirstInset;
    // right inset for vertical list, bottom inset for horizontal list
    private int mSecondInset;
    private int mColor;
    private int mOrientation;
    // set it to true to draw divider on the tile, or false to draw beside the tile.
    // if you set it to false and have inset at the same time, you may see the background of
    // the parent of RecyclerView.
    private boolean mOverlay;

    private InsetDivider() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public int getDividerHeight() {
        return mDividerHeight;
    }

    public void setDividerHeight(int dividerHeight) {
        this.mDividerHeight = dividerHeight;
    }

    public int getFirstInset() {
        return mFirstInset;
    }

    public void setFirstInset(int firstInset) {
        this.mFirstInset = firstInset;
    }

    public int getSecondInset() {
        return mSecondInset;
    }

    public void setSecondInset(int secondInset) {
        this.mSecondInset = secondInset;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
        mPaint.setColor(color);
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
    }

    public boolean getOverlay() {
        return mOverlay;
    }

    public void setOverlay(boolean overlay) {
        this.mOverlay = overlay;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    protected void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft() + mFirstInset;
        final int right = parent.getWidth() - parent.getPaddingRight() - mSecondInset;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            if (parent.getChildAdapterPosition(child) == (parent.getAdapter().getItemCount() - 1)) {
                continue;
            }
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int bottom;
            final int top;
            if (mOverlay) {
                bottom = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
                top = bottom - mDividerHeight;
            } else {
                top = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
                bottom = top + mDividerHeight;
            }
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    protected void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop() + mFirstInset;
        final int bottom = parent.getHeight() - parent.getPaddingBottom() - mSecondInset;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            if (parent.getChildAdapterPosition(child) == (parent.getAdapter().getItemCount() - 1)) {
                continue;
            }
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int right;
            final int left;
            if (mOverlay) {
                right = child.getRight() + params.rightMargin + Math.round(ViewCompat.getTranslationX(child));
                left = right - mDividerHeight;
            } else {
                left = child.getRight() + params.rightMargin + Math.round(ViewCompat.getTranslationX(child));
                right = left + mDividerHeight;
            }
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOverlay) {
            super.getItemOffsets(outRect, view, parent, state);
            return;
        }

        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDividerHeight);
        } else {
            outRect.set(0, 0, mDividerHeight, 0);
        }
    }

    /**
     * Handy builder for creating {@link InsetDivider} instance.
     */
    public static class Builder {

        private Context mContext;
        private int mDividerHeight;
        private int mFirstInset;
        private int mSecondInset;
        private int mColor;
        private int mOrientation;
        private boolean mOverlay = true; // set default to true to follow Material Design Guidelines

        public Builder(Context context) {
            mContext = context;
        }

        public Builder dividerHeight(int dividerHeight) {
            mDividerHeight = dividerHeight;
            return this;
        }

        public Builder insets(int firstInset, int secondInset) {
            mFirstInset = firstInset;
            mSecondInset = secondInset;
            return this;
        }

        public Builder color(@ColorInt int color) {
            mColor = color;
            return this;
        }

        public Builder orientation(int orientation) {
            mOrientation = orientation;
            return this;
        }

        public Builder overlay(boolean overlay) {
            mOverlay = overlay;
            return this;
        }

        public InsetDivider build() {
            InsetDivider insetDivider = new InsetDivider();

            if (mDividerHeight == 0) {
                // Set default divider height to 1dp.
                insetDivider.setDividerHeight(mContext.getResources().getDimensionPixelSize(R.dimen.divider_height));
            } else if (mDividerHeight > 0) {
                insetDivider.setDividerHeight(mDividerHeight);
            } else {
                throw new IllegalArgumentException("Divider's height can't be negative.");
            }

            insetDivider.setFirstInset(mFirstInset < 0 ? 0 : mFirstInset);
            insetDivider.setSecondInset(mSecondInset < 0 ? 0 : mSecondInset);

            if (mColor == 0) {
                throw new IllegalArgumentException("Don't forget to set color");
            } else {
                insetDivider.setColor(mColor);
            }

            if (mOrientation != InsetDivider.HORIZONTAL_LIST && mOrientation != InsetDivider.VERTICAL_LIST) {
                throw new IllegalArgumentException("Invalid orientation");
            } else {
                insetDivider.setOrientation(mOrientation);
            }

            insetDivider.setOverlay(mOverlay);

            return insetDivider;
        }
    }
}