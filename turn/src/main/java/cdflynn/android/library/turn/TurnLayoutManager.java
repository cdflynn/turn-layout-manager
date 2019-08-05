package cdflynn.android.library.turn;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import androidx.annotation.Dimension;
import androidx.annotation.IntDef;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * An extension of {@link LinearLayoutManager}, where each list item can be offset along a circular
 * trajectory.
 */
public class TurnLayoutManager extends LinearLayoutManager {

    private static final int MIN_RADIUS = 0;
    private static final int MIN_PEEK = 0;

    /**
     * Valid gravity settings for a {@link TurnLayoutManager}.  This defines the direction of the center point
     * around which items will rotate.
     */
    @IntDef(value = {
            Gravity.START,
            Gravity.END
    })
    public @interface Gravity {
        int START = android.view.Gravity.START;
        int END = android.view.Gravity.END;
    }

    /**
     * Orientation as defined in {@link RecyclerView}
     */
    @IntDef(value = {
            Orientation.VERTICAL,
            Orientation.HORIZONTAL
    })
    public @interface Orientation {
        int VERTICAL = RecyclerView.VERTICAL;
        int HORIZONTAL = RecyclerView.HORIZONTAL;
    }

    @Gravity
    private int gravity;
    @Dimension
    private int radius;
    @Dimension
    private int peekDistance;
    private boolean rotate;
    private Point center;

    /**
     * Define a new {@link TurnLayoutManager} with the given settings.<br>
     * {@code gravity} and {@code orientation} combine to define the curvature of the turn:
     * <br><br>
     * {@link Gravity#START}<br>
     * {@link Orientation#VERTICAL}
     * <pre>
     *     ┏─────────┓
     *     ┃ x       ┃
     *     ┃  x      ┃
     *     ┃   x     ┃
     *     ┃   x     ┃
     *     ┃   x     ┃
     *     ┃  x      ┃
     *     ┃ x       ┃
     *     ┗─────────┛
     * </pre>
     * <br>
     * {@link Gravity#END}<br>
     * {@link Orientation#VERTICAL}
     * <pre>
     *     ┏─────────┓
     *     ┃       x ┃
     *     ┃      x  ┃
     *     ┃     x   ┃
     *     ┃     x   ┃
     *     ┃     x   ┃
     *     ┃      x  ┃
     *     ┃       x ┃
     *     ┗─────────┛
     * </pre>
     * <p>
     * <br>
     * {@link Gravity#START}<br>
     * {@link Orientation#HORIZONTAL}
     * <pre>
     *     ┏─────────┓
     *     ┃x       x┃
     *     ┃ x     x ┃
     *     ┃   xxx   ┃
     *     ┃         ┃
     *     ┃         ┃
     *     ┃         ┃
     *     ┃         ┃
     *     ┗─────────┛
     * </pre>
     * <p>
     * <br>
     * {@link Gravity#END}<br>
     * {@link Orientation#HORIZONTAL}
     * <pre>
     *     ┏─────────┓
     *     ┃         ┃
     *     ┃         ┃
     *     ┃         ┃
     *     ┃         ┃
     *     ┃   xxx   ┃
     *     ┃ x     x ┃
     *     ┃x       x┃
     *     ┗─────────┛
     * </pre>
     *
     * @param gravity      The {@link Gravity} that will define where the anchor point is for this layout manager.  The
     *                     gravity point is the point around which items orbit.
     * @param orientation  The orientation as defined in {@link RecyclerView}, and enforced by {@link Orientation}
     * @param radius       The radius of the rotation angle, which helps define the curvature of the turn.  This value
     *                     will be clamped to {@code [0, MAX_INT]} inclusive.
     * @param peekDistance The absolute extra distance from the {@link Gravity} edge after which this layout manager will start
     *                     placing items.  This value will be clamped to {@code [0, radius]} inclusive.
     * @param rotate       Should the items rotate as if on a turning surface, or should they maintain
     *                     their angle with respect to the screen as they orbit the center point?
     */
    public TurnLayoutManager(Context context,
                             @Gravity int gravity,
                             @Orientation int orientation,
                             @Dimension int radius,
                             @Dimension int peekDistance,
                             boolean rotate) {
        super(context, orientation, false);
        this.gravity = gravity;
        this.radius = Math.max(radius, MIN_RADIUS);
        this.peekDistance = Math.min(Math.max(peekDistance, MIN_PEEK), radius);
        this.rotate = rotate;
        this.center = new Point();
    }

    /**
     * Create a {@link TurnLayoutManager} with default settings for gravity, orientation, and rotation.
     */
    public TurnLayoutManager(Context context,
                             @Dimension int radius,
                             @Dimension int peekDistance) {
        this(context, Gravity.START, Orientation.VERTICAL, radius, peekDistance, false);
    }

    public void setRadius(int radius) {
        this.radius = Math.max(radius, MIN_RADIUS);
        requestLayout();
    }

    public void setPeekDistance(int peekDistance) {
        this.peekDistance = Math.min(Math.max(peekDistance, MIN_PEEK), radius);
        requestLayout();
    }

    public void setGravity(@Gravity int gravity) {
        this.gravity = gravity;
        requestLayout();
    }

    public void setRotate(boolean rotate) {
        this.rotate = rotate;
        requestLayout();
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int by = super.scrollVerticallyBy(dy, recycler, state);
        setChildOffsetsVertical(gravity, radius, center, peekDistance);
        return by;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int by = super.scrollHorizontallyBy(dx, recycler, state);
        setChildOffsetsHorizontal(gravity, radius, center, peekDistance);
        return by;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        this.center = deriveCenter(gravity, getOrientation(), radius, peekDistance, center);
        setChildOffsets(gravity, getOrientation(), radius, center, peekDistance);
    }

    /**
     * Accounting for the settings of {@link Gravity} and {@link Orientation}, find the center point
     * around which this layout manager should arrange list items.  Place the resulting coordinates
     * into {@code out}, to avoid reallocation.
     */
    private Point deriveCenter(@Gravity int gravity,
                               int orientation,
                               @Dimension int radius,
                               @Dimension int peekDistance,
                               Point out) {
        final int gravitySign = gravity == Gravity.START ? -1 : 1;
        final int distanceMultiplier = gravity == Gravity.START ? 0 : 1;
        int x, y;
        switch (orientation) {
            case Orientation.HORIZONTAL:
                y = (distanceMultiplier * getHeight()) + gravitySign * (Math.abs(radius - peekDistance));
                x = getWidth() / 2;
                break;
            case Orientation.VERTICAL:
            default:
                y = getHeight() / 2;
                x = (distanceMultiplier * getWidth()) + gravitySign * (Math.abs(radius - peekDistance));
                break;
        }
        out.set(x, y);
        return out;
    }

    /**
     * Find the absolute horizontal distance by which a view at {@code viewY} should offset
     * to align with the circle {@code center} with {@code radius}, accounting for {@code peekDistance}.
     */
    private double resolveOffsetX(double radius, double viewY, Point center, int peekDistance) {
        final double opposite = Math.abs(center.y - viewY);
        final double radiusSquared = radius * radius;
        final double oppositeSquared = opposite * opposite;
        final double adjacentSideLength = Math.sqrt(radiusSquared - oppositeSquared);
        return adjacentSideLength - radius + peekDistance;
    }

    /**
     * Find the absolute vertical distance by which a view at {@code viewX} should offset to
     * align with the circle {@code center} with {@code radius}, account for {@code peekDistance}.
     */
    private double resolveOffsetY(double radius, double viewX, Point center, int peekDistance) {
        final double adjacent = Math.abs(center.x - viewX);
        final double radiusSquared = radius * radius;
        final double adjacentSquared = adjacent * adjacent;
        final double oppositeSideLength = Math.sqrt(radiusSquared - adjacentSquared);
        return oppositeSideLength - radius + peekDistance;
    }

    /**
     * Traffic method to divert calls based on {@link Orientation}.
     *
     * @see #setChildOffsetsVertical(int, int, Point, int)
     * @see #setChildOffsetsHorizontal(int, int, Point, int)
     */
    private void setChildOffsets(@Gravity int gravity,
                                 int orientation,
                                 @Dimension int radius,
                                 Point center,
                                 int peekDistance) {
        if (orientation == VERTICAL) {
            setChildOffsetsVertical(gravity, radius, center, peekDistance);
        } else if (orientation == HORIZONTAL) {
            setChildOffsetsHorizontal(gravity, radius, center, peekDistance);
        }
    }

    /**
     * Set the bumper offsets on child views for {@link Orientation#VERTICAL}
     */
    private void setChildOffsetsVertical(@Gravity int gravity,
                                         @Dimension int radius,
                                         Point center,
                                         int peekDistance) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int xOffset = (int) resolveOffsetX(radius, child.getY() + child.getHeight() / 2.0f, center, peekDistance);
            final int x = gravity == Gravity.START ? xOffset + getMarginStart(layoutParams)
                    : getWidth() - xOffset - child.getWidth() - getMarginStart(layoutParams);
            child.layout(x, child.getTop(), child.getWidth() + x, child.getBottom());
            setChildRotationVertical(gravity, child, radius, center);
        }
    }

    /**
     * Given that the is {@link Orientation#VERTICAL}, apply rotation if rotation is enabled.
     */
    private void setChildRotationVertical(@Gravity int gravity, View child, int radius, Point center) {
        if (!rotate) {
            child.setRotation(0);
            return;
        }
        boolean childPastCenter = (child.getY() + child.getHeight() / 2) > center.y;
        float directionMult;
        if (gravity == Gravity.END) {
            directionMult = childPastCenter ? -1 : 1;
        } else {
            directionMult = childPastCenter ? 1 : -1;
        }
        final float opposite = Math.abs(child.getY() + child.getHeight() / 2.0f - center.y);
        child.setRotation((float) (directionMult * Math.toDegrees(Math.asin(opposite / radius))));
    }

    /**
     * Set bumper offsets on child views for {@link Orientation#HORIZONTAL}
     */
    private void setChildOffsetsHorizontal(@Gravity int gravity,
                                           @Dimension int radius,
                                           Point center,
                                           int peekDistance) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int yOffset = (int) resolveOffsetY(radius, child.getX() + child.getWidth() / 2.0f, center, peekDistance);
            final int y = gravity == Gravity.START ? yOffset + getMarginStart(layoutParams)
                    : getHeight() - yOffset - child.getHeight() - getMarginStart(layoutParams);

            child.layout(child.getLeft(), y, child.getRight(), child.getHeight() + y);
            setChildRotationHorizontal(gravity, child, radius, center);
        }
    }

    /**
     * Given that the orientation is {@link Orientation#HORIZONTAL}, apply rotation if enabled.
     */
    private void setChildRotationHorizontal(@Gravity int gravity, View child, int radius, Point center) {
        if (!rotate) {
            child.setRotation(0);
            return;
        }
        boolean childPastCenter = (child.getX() + child.getWidth() / 2) > center.x;
        float directionMult;
        if (gravity == Gravity.END) {
            directionMult = childPastCenter ? 1 : -1;
        } else {
            directionMult = childPastCenter ? -1 : 1;
        }
        final float opposite = Math.abs(child.getX() + child.getWidth() / 2.0f - center.x);
        child.setRotation((float) (directionMult * Math.toDegrees(Math.asin(opposite / radius))));
    }
    
    /**
     * @see android.view.ViewGroup.MarginLayoutParams#getMarginStart()
     */
    private int getMarginStart(ViewGroup.MarginLayoutParams layoutParams) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return layoutParams.getMarginStart();
        }
        return layoutParams.leftMargin;
    }
}
