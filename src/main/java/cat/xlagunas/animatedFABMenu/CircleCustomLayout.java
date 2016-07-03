package cat.xlagunas.lib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xlagunas on 3/7/16.
 */
public class CircleCustomLayout extends CustomLayout {

    public CircleCustomLayout(Context context) {
        super(context);
    }

    public CircleCustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleCustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleCustomLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int getSubElementsCount() {
        return super.getSubElementsCount() -1;
    }

    @Override
    protected int getSubElementsPosition(int subElement) {
        return super.getSubElementsPosition(subElement);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i=0; i<getChildCount() -1;i++) {
            super.onLayout(changed, l, t, r, b);
        }

        layoutCircle(getChildCount()-1);
    }

    private View getCircle(){
        return new View(getContext()){
            private Paint paint = new Paint();
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.WHITE);
                paint.setFlags(Paint.ANTI_ALIAS_FLAG);
                paint.setAlpha(200);
                canvas.drawPaint(paint);
                paint.setColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright));
                canvas.drawCircle(anchor.centerX(), anchor.centerY(), radius, paint);
            }
        };
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addView(getCircle());
    }

    private void layoutCircle(int circlePosition) {
        View circleView = getChildAt(circlePosition);
        computedWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        computedHeight = getMeasuredHeight();

        circleView.measure(MeasureSpec.makeMeasureSpec(computedWidth, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(computedHeight, MeasureSpec.AT_MOST));

        if (circleView.getVisibility() != GONE) {
            circleView.layout(0, 0, computedWidth, computedHeight);
        }
    }

    @Override
    public void collapse() {
        super.collapse();
        getChildAt(getChildCount()-1).animate()
                .x(-computedWidth)
                .y(anchor.exactCenterY()).setDuration(400).setStartDelay((getChildCount() -1) * 100);
    }

    @Override
    public void expand() {
        super.expand();
        getChildAt(getChildCount()-1).animate()
                .x(0)
                .y(0).setDuration(350).start();
    }
}
