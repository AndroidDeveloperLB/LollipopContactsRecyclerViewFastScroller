package com.lb.recyclerview_fast_scroller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lb.recyclerview_fast_scroller.RecyclerViewFastScroller.BubbleTextGetter;

import java.util.ArrayList;
import java.util.List;

public final class LargeAdapter extends RecyclerView.Adapter<LargeAdapter.ViewHolder> implements BubbleTextGetter {
    private final List<String> items;

    public LargeAdapter(int numberOfItems) {
        List<String> items = new ArrayList<>();
        java.util.Random r = new java.util.Random();
        for (int i = 0; i < numberOfItems; i++)
            items.add(((char) ('A' + r.nextInt('Z' - 'A'))) + " " + Integer.toString(i));
        java.util.Collections.sort(items);
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String text = items.get(position);
        holder.setText(text);
    }

    @Override
    public String getTextToShowInBubble(final int pos) {
        return Character.toString(items.get(pos).charAt(0));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        private ViewHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(android.R.id.text1);
        }

        public void setText(CharSequence text) {
            textView.setText(text);
        }
    }
}
