package com.lb.lollipop_contacts_recyclerview_fast_scroller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lb.lollipopcontactsrecyclerviewfastscroller.R;

import static android.support.v7.widget.RecyclerView.OnScrollListener;

public class FastScroller extends LinearLayout
  {
  private static final int BUBBLE_ANIMATION_DURATION=100;
  private static final int TRACK_SNAP_RANGE=5;

  private TextView bubble;
  private View handle;
  private RecyclerView recyclerView;
  private final ScrollListener scrollListener=new ScrollListener();
  private int height;

  private ObjectAnimator currentAnimator=null;

  public FastScroller(final android.content.Context context,final android.util.AttributeSet attrs,final int defStyleAttr)
    {
    super(context,attrs,defStyleAttr);
    initialise(context);
    }

  public FastScroller(final android.content.Context context)
    {
    super(context);
    initialise(context);
    }

  public FastScroller(final android.content.Context context,final android.util.AttributeSet attrs)
    {
    super(context,attrs);
    initialise(context);
    }

  private void initialise(Context context)
    {
    setOrientation(HORIZONTAL);
    setClipChildren(false);
    LayoutInflater inflater=LayoutInflater.from(context);
    inflater.inflate(R.layout.recycler_view_fast_scroller__fast_scroller,this,true);
    bubble=(TextView)findViewById(R.id.fastscroller_bubble);
    handle=findViewById(R.id.fastscroller_handle);
    bubble.setVisibility(INVISIBLE);
    }

  @Override
  protected void onSizeChanged(int w,int h,int oldw,int oldh)
    {
    super.onSizeChanged(w,h,oldw,oldh);
    height=h;
    }

  @Override
  public boolean onTouchEvent(@NonNull MotionEvent event)
    {
    final int action=event.getAction();
    switch(action)
      {
      case MotionEvent.ACTION_DOWN:
        if(event.getX()<handle.getX())
          return false;
        handle.setSelected(true);
      case MotionEvent.ACTION_MOVE:
        setPosition(event.getY());
        if(currentAnimator!=null)
          currentAnimator.cancel();
        if(bubble.getVisibility()==INVISIBLE)
          showBubble();
        setRecyclerViewPosition(event.getY());
        return true;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        handle.setSelected(false);
        hideBubble();
        return true;
      }
    return super.onTouchEvent(event);
    }

  public void setRecyclerView(RecyclerView recyclerView)
    {
    this.recyclerView=recyclerView;
    recyclerView.setOnScrollListener(scrollListener);
    }

  private void setRecyclerViewPosition(float y)
    {
    if(recyclerView!=null)
      {
      int itemCount=recyclerView.getAdapter().getItemCount();
      float proportion;
      if(handle.getY()==0)
        proportion=0f;
      else if(handle.getY()+handle.getHeight()>=height-TRACK_SNAP_RANGE)
        proportion=1f;
      else
        proportion=y/(float)height;
      int targetPos=getValueInRange(0,itemCount-1,(int)(proportion*(float)itemCount));
      recyclerView.scrollToPosition(targetPos);
      String bubbleText=((BubbleTextGetter)recyclerView.getAdapter()).getTextToShowInBubble(targetPos);
      bubble.setText(bubbleText);
      }
    }

  private int getValueInRange(int min,int max,int value)
    {
    int minimum=Math.max(min,value);
    return Math.min(minimum,max);
    }

  private void setPosition(float y)
    {
    int bubbleHeight=bubble.getHeight();
    int handleHeight=handle.getHeight();
    handle.setY(getValueInRange(0,height-handleHeight,(int)(y-handleHeight/2)));
    bubble.setY(getValueInRange(0,height-bubbleHeight-handleHeight/2,(int)(y-bubbleHeight)));
    }

  private void showBubble()
    {
    AnimatorSet animatorSet=new AnimatorSet();
    bubble.setVisibility(VISIBLE);
    if(currentAnimator!=null)
      currentAnimator.cancel();
    currentAnimator=ObjectAnimator.ofFloat(bubble,"alpha",0f,1f).setDuration(BUBBLE_ANIMATION_DURATION);
    currentAnimator.start();
    }

  private void hideBubble()
    {
    if(currentAnimator!=null)
      currentAnimator.cancel();
    currentAnimator=ObjectAnimator.ofFloat(bubble,"alpha",1f,0f).setDuration(BUBBLE_ANIMATION_DURATION);
    currentAnimator.addListener(new AnimatorListenerAdapter()
    {
    @Override
    public void onAnimationEnd(Animator animation)
      {
      super.onAnimationEnd(animation);
      bubble.setVisibility(INVISIBLE);
      currentAnimator=null;
      }

    @Override
    public void onAnimationCancel(Animator animation)
      {
      super.onAnimationCancel(animation);
      bubble.setVisibility(INVISIBLE);
      currentAnimator=null;
      }
    });
    currentAnimator.start();
    }

  private class ScrollListener extends OnScrollListener
    {
    @Override
    public void onScrolled(RecyclerView rv,int dx,int dy)
      {
      View firstVisibleView=recyclerView.getChildAt(0);
      int firstVisiblePosition=recyclerView.getChildPosition(firstVisibleView);
      int visibleRange=recyclerView.getChildCount();
      int lastVisiblePosition=firstVisiblePosition+visibleRange;
      int itemCount=recyclerView.getAdapter().getItemCount();
      int position;
      if(firstVisiblePosition==0)
        position=0;
      else if(lastVisiblePosition==itemCount-1)
        position=itemCount-1;
      else
        position=firstVisiblePosition;
      float proportion=(float)position/(float)itemCount;
      setPosition(height*proportion);
      }
    }
  }
