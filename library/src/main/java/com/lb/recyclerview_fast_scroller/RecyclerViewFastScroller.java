package com.lb.recyclerview_fast_scroller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewFastScroller extends LinearLayout {
    private static final int BUBBLE_ANIMATION_DURATION = 100;
    private static final int TRACK_SNAP_RANGE = 5;

    private TextView bubble;
    private View handle;
    private RecyclerView recyclerView;
    private int height;
    private boolean isInitialized = false;
    private ObjectAnimator currentAnimator = null;

    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
            updateBubbleAndHandlePosition();
        }
    };

    public interface BubbleTextGetter {
        String getTextToShowInBubble(int pos);
    }

    public RecyclerViewFastScroller(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public RecyclerViewFastScroller(final Context context) {
        super(context);
        init(context);
    }

    public RecyclerViewFastScroller(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected void init(Context context) {
        if (isInitialized)
            return;
        isInitialized = true;
        setOrientation(HORIZONTAL);
        setClipChildren(false);
    }

    public void setViewsToUse(@LayoutRes int layoutResId, @IdRes int bubbleResId, @IdRes int handleResId) {
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(layoutResId, this, true);
        bubble = findViewById(bubbleResId);
        if (bubble != null)
            bubble.setVisibility(INVISIBLE);
        handle = findViewById(handleResId);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        updateBubbleAndHandlePosition();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < handle.getX() - ViewCompat.getPaddingStart(handle))
                    return false;
                if (currentAnimator != null)
                    currentAnimator.cancel();
                if (bubble != null && bubble.getVisibility() == INVISIBLE)
                    showBubble();
                handle.setSelected(true);
            case MotionEvent.ACTION_MOVE:
                final float y = event.getY();
                setBubbleAndHandlePosition(y);
                setRecyclerViewPosition(y);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handle.setSelected(false);
                hideBubble();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void setRecyclerView(final RecyclerView recyclerView) {
        if (this.recyclerView != recyclerView) {
            if (this.recyclerView != null)
                this.recyclerView.removeOnScrollListener(onScrollListener);
            this.recyclerView = recyclerView;
            if (this.recyclerView == null)
                return;
            recyclerView.addOnScrollListener(onScrollListener);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(onScrollListener);
            recyclerView = null;
        }
    }

    private void setRecyclerViewPosition(float y) {
        if (recyclerView != null) {
            final int itemCount = recyclerView.getAdapter().getItemCount();
            float proportion;
            if (handle.getY() == 0)
                proportion = 0f;
            else if (handle.getY() + handle.getHeight() >= height - TRACK_SNAP_RANGE)
                proportion = 1f;
            else
                proportion = y / (float) height;
            final int targetPos = getValueInRange(0, itemCount - 1, (int) (proportion * (float) itemCount));
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(targetPos, 0);
            final String bubbleText = ((BubbleTextGetter) recyclerView.getAdapter()).getTextToShowInBubble(targetPos);
            if (bubble != null) {
                bubble.setText(bubbleText);
                if (TextUtils.isEmpty(bubbleText)) {
                    hideBubble();
                } else if (bubble.getVisibility() == View.INVISIBLE) {
                    showBubble();
                }
            }
        }
    }

    private int getValueInRange(int min, int max, int value) {
        int minimum = Math.max(min, value);
        return Math.min(minimum, max);
    }

    private void updateBubbleAndHandlePosition() {
        if (bubble == null || handle.isSelected())
            return;

        final int verticalScrollOffset = recyclerView.computeVerticalScrollOffset();
        final int verticalScrollRange = recyclerView.computeVerticalScrollRange();
        float proportion = (float) verticalScrollOffset / ((float) verticalScrollRange - height);
        setBubbleAndHandlePosition(height * proportion);
    }

    private void setBubbleAndHandlePosition(float y) {
        final int handleHeight = handle.getHeight();
        handle.setY(getValueInRange(0, height - handleHeight, (int) (y - handleHeight / 2)));
        if (bubble != null) {
            int bubbleHeight = bubble.getHeight();
            bubble.setY(getValueInRange(0, height - bubbleHeight - handleHeight / 2, (int) (y - bubbleHeight)));
        }
    }

    private void showBubble() {
        if (bubble == null)
            return;
        bubble.setVisibility(VISIBLE);
        if (currentAnimator != null)
            currentAnimator.cancel();
        currentAnimator = ObjectAnimator.ofFloat(bubble, "alpha", 0f, 1f).setDuration(BUBBLE_ANIMATION_DURATION);
        currentAnimator.start();
    }

    private void hideBubble() {
        if (bubble == null)
            return;
        if (currentAnimator != null)
            currentAnimator.cancel();
        currentAnimator = ObjectAnimator.ofFloat(bubble, "alpha", 1f, 0f).setDuration(BUBBLE_ANIMATION_DURATION);
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bubble.setVisibility(INVISIBLE);
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                bubble.setVisibility(INVISIBLE);
                currentAnimator = null;
            }
        });
        currentAnimator.start();
    }
}
