package cat.xlagunas.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by xlagunas on 1/07/16.
 */
public class CustomLayout extends RelativeLayout implements View.OnClickListener {

    protected Rect anchor;
    private boolean initialized = false;
    private boolean isExpanded = true;

    private final static double MAXIMUM_ANGLE = Math.PI / 2;
    private double angleOffset = 0, extraOffset;
    private Rect[] computedPositions;

    protected double angle;
    protected int computedWidth, computedHeight;
    protected float radius;


    public CustomLayout(Context context) {
        super(context);
    }

    public CustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CustomLayout, 0, 0);
        try {
            radius = ta.getDimension(R.styleable.CustomLayout_radius, -1);
            extraOffset = ta.getFloat(R.styleable.CustomLayout_angle_offset, 0);
        } finally {
            ta.recycle();
        }
    }

    @TargetApi(21)
    public CustomLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getChildAt(0).setOnClickListener(this);
    }

    protected int getSubElementsCount(){
        return getChildCount() - 1;
    }

    protected int getSubElementsPosition(int subElement){
        return subElement +1;
    }

    private void initMeasurements() {
        angle = MAXIMUM_ANGLE / (getSubElementsCount() - 1);
        computedPositions = new Rect[getSubElementsCount()];
        radius = (radius == -1) ?  getWidth() - anchor.width() * 0.5f : radius;
        initialized = true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutAnchor();

        for (int i=0;i<getSubElementsCount();i++){
            layoutSubElements(i);
        }
    }



    private void layoutAnchor() {
        View anchorView = getChildAt(0);
        computedWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        computedHeight = getMeasuredHeight();

        anchorView.measure(MeasureSpec.makeMeasureSpec(computedWidth, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(computedHeight, MeasureSpec.AT_MOST));



        if (anchorView.getVisibility() != GONE) {
            LayoutParams st =
                    (LayoutParams) anchorView.getLayoutParams();

            anchor = new Rect(st.leftMargin,
                    getMeasuredHeight() - st.topMargin - st.height -st.bottomMargin,
                    st.width + st.leftMargin -st.rightMargin,
                    getMeasuredHeight() - st.bottomMargin - st.topMargin);

            switch (getGravity()) {
                case Gravity.CENTER_HORIZONTAL:
                case Gravity.CENTER:
                    anchor.left = computedWidth / 2 - st.width / 2;
                    anchor.right = computedWidth / 2 + st.width / 2;
                    angleOffset = Math.PI / 4;
                    break;
                case Gravity.RIGHT:
                case Gravity.END:
                    anchor.left = computedWidth -st.width;
                    anchor.right = computedWidth;
                    angleOffset = Math.PI / 2;
                    break;
            }

            anchorView.layout(anchor.left, anchor.top, anchor.right, anchor.bottom);
        }

    }

    private void layoutSubElements(int childrenPosition) {
        View view = getChildAt(getSubElementsPosition(childrenPosition));

        if (!initialized){
            initMeasurements();
        }

        //MEASURE THE CURRENT VIEW
        view.measure(MeasureSpec.makeMeasureSpec(computedWidth, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(computedHeight, MeasureSpec.AT_MOST));

        LayoutParams st =
                (LayoutParams) view.getLayoutParams();

        //calculated position of the element
        double xPos = radius * Math.cos((childrenPosition)*angle +angleOffset + extraOffset);
        double yPos = radius * Math.sin((childrenPosition)*angle +angleOffset + extraOffset);

        int left    =    (int) (xPos +anchor.exactCenterX() - st.width/2);
        int right   =    left + st.width;

        int top     =    (int) (anchor.exactCenterY() -yPos -st.height/2);
        int bottom  =    top + st.height;

        Rect rect = new Rect(left, top, right, bottom);
        computedPositions[childrenPosition] = rect;


        view.layout(rect.left, rect.top, rect.right, rect.bottom);

    }

    @Override
    public void onClick(View v) {
        if (isExpanded){
            collapse();
        } else {
            expand();
        }

        isExpanded = !isExpanded;
    }

    public void expand() {
        getChildAt(0).animate().rotation(45*4).setDuration(200);

        for (int i = 0; i < getSubElementsCount(); i++) {
            final View child = getChildAt(getSubElementsPosition(i));
            child.animate().x(computedPositions[i].left).y(computedPositions[i].top).setStartDelay(((getSubElementsCount() -i) +1) * 100).withStartAction(new Runnable() {
                @Override
                public void run() {
                    child.setVisibility(VISIBLE);
                }
            });
        }
    }

    public void collapse() {
        getChildAt(0).animate().rotation(-45*3).setDuration(200);

        for (int i = getSubElementsCount()-1; i >= 0; i--) {
            final View child = getChildAt(getSubElementsPosition(i));
            Rect anchorRect = new Rect(anchor.left, anchor.top, anchor.right, anchor.bottom);
            Rect viewRect = new Rect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());

            child.animate()
                    .translationXBy(anchorRect.exactCenterX() - viewRect.exactCenterX())
                    .translationYBy(anchorRect.exactCenterY() - viewRect.exactCenterY())
                    .setStartDelay(i * 100).withEndAction(new Runnable() {
                @Override
                public void run() {
                    child.setVisibility(INVISIBLE);
                }
            });
        }
    }
}
