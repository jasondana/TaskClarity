package com.reductivetech.taskclarity.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.reductivetech.taskclarity.page.TaskListAdapter;

import java.util.ArrayList;

public class DynamicListView<T> extends ListView {

    public static final String TAG = DynamicListView.class.getSimpleName();

    private ArrayList<T> _list;

    private int _downX = -1;
    private int _downY = -1;

    private BitmapDrawable _hoverCell;
    private Rect _hoverCellOriginalBounds;
    private Rect _hoverCellCurrentBounds;

    private boolean _cellMobile;
    private int _activePointerId;
    private int _mobileId;

    public interface OnListUpdateListener {
        public void onListUpdate();
    }

    private OnListUpdateListener _onListUpdateListener = new OnListUpdateListener() {
        @Override
        public void onListUpdate() {
        }
    };

    public DynamicListView(Context context) {
        super(context);
    }

    public DynamicListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DynamicListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setList(ArrayList<T> list) {
        _list = list;
    }

    public void setAdapter(TaskListAdapter adapter) {
        adapter.setSliderLongClickListener(_onLongClickListener);
        super.setAdapter(adapter);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                _downX = (int) event.getX();
                _downY = (int) event.getY();
                _activePointerId = event.getPointerId(0);
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                int pointerIndex = event.findPointerIndex(_activePointerId);
                int deltaY = (int) event.getY(pointerIndex) - _downY;

                if (_cellMobile) {
                    int left = _hoverCellOriginalBounds.left;
                    int top = _hoverCellOriginalBounds.top + deltaY; //+ _totalOffset

                    _hoverCellCurrentBounds.offsetTo(left, top);
                    _hoverCell.setBounds(_hoverCellCurrentBounds);

                    invalidate();

                    int position = pointToPosition((int) event.getX(), (int) event.getY());
                    int itemNum = position - getFirstVisiblePosition();

                    if (itemNum >= 0) {
                        T temp = _list.get(_mobileId);
                        _list.set(_mobileId, _list.get(itemNum));
                        _list.set(itemNum, temp);

                        View mobileView = getChildAt(_mobileId);
                        mobileView.setVisibility(VISIBLE);

                        View overView = getChildAt(itemNum);
                        overView.setVisibility(INVISIBLE);

                        _mobileId = itemNum;

                        ((ArrayAdapter) getAdapter()).notifyDataSetChanged();
                    }

                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.v(TAG, "Action up");
                _cellMobile = false;
                View mobileView = getChildAt(_mobileId);
                mobileView.setVisibility(VISIBLE);
                _hoverCell = null;
                invalidate();
                _onListUpdateListener.onListUpdate();
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.v(TAG, "Action cancel");
                _cellMobile = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setOnListUpdateListener(OnListUpdateListener listener) {
        _onListUpdateListener = listener;
    }

    /**
     *  dispatchDraw gets invoked when all the child views are about to be drawn.
     *  By overriding this method, the hover cell (BitmapDrawable) can be drawn
     *  over the listview's items whenever the listview is redrawn.
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (_hoverCell != null) {
            _hoverCell.draw(canvas);
        }
    }

    private OnLongClickListener _onLongClickListener =
            new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = pointToPosition(_downX, _downY);
                    int itemNum = position - getFirstVisiblePosition();
                    _mobileId = itemNum;

                    View selectedView = getChildAt(itemNum);
                    _hoverCell = getAndAddHoverView(selectedView);
                    _cellMobile = true;

                    selectedView.setVisibility(INVISIBLE);

                    return true;
                }
            };

    /**
     * Creates the hover cell with the appropriate bitmap and of appropriate
     * size. The hover cell's BitmapDrawable is drawn on top of the bitmap every
     * single time an invalidate call is made.
     */
    private BitmapDrawable getAndAddHoverView(View v) {

        int w = v.getWidth();
        int h = v.getHeight();
        int top = v.getTop();
        int left = v.getLeft();

        Log.v(TAG, "TOP: " + top);
        Log.v(TAG, "LEFT: " + left);

        //Bitmap b = getBitmapWithBorder(v);
        Bitmap b = getBitmapFromView(v);

        BitmapDrawable drawable = new BitmapDrawable(getResources(), b);

        _hoverCellOriginalBounds = new Rect(left, top, left + w, top + h);
        _hoverCellCurrentBounds = new Rect(_hoverCellOriginalBounds);

        drawable.setBounds(_hoverCellCurrentBounds);

        return drawable;
    }

    /**
     * Returns a bitmap showing a screenshot of the view passed in.
     */
    private Bitmap getBitmapFromView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas (bitmap);
        v.draw(canvas);
        return bitmap;
    }

}
