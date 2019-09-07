package com.lb.recyclerview_fast_scroller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.lb.lollipopcontactsrecyclerviewfastscroller.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //note : part of the design library sample code was taken from : https://github.com/sitepoint-editors/Design-Demo/

        DesignDemoPagerAdapter adapter = new DesignDemoPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        String url = null;
        switch (item.getItemId()) {
            case R.id.menuItem_all_my_apps:
                url = "https://play.google.com/store/apps/developer?id=AndroidDeveloperLB";
                break;
            case R.id.menuItem_all_my_repositories:
                url = "https://github.com/AndroidDeveloperLB";
                break;
            case R.id.menuItem_current_repository_website:
                url = "https://github.com/AndroidDeveloperLB/LollipopContactsRecyclerViewFastScroller";
                break;
        }
        if (url == null)
            return true;
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        startActivity(intent);
        return true;
    }


    static class DesignDemoPagerAdapter extends FragmentStatePagerAdapter {
        public DesignDemoPagerAdapter(@NonNull final FragmentManager fm, final int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            final RecyclerViewFragment recyclerViewFragment = new RecyclerViewFragment();
            Bundle args = new Bundle();
            args.putInt(RecyclerViewFragment.ITEMS_COUNT, getFragmentItemsCount(position));
            recyclerViewFragment.setArguments(args);
            return recyclerViewFragment;
        }

        private int getFragmentItemsCount(int pos) {
            return (int) Math.pow(4, (getCount() - pos));
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "itemsCount: " + getFragmentItemsCount(position);
        }
    }


    public static class RecyclerViewFragment extends Fragment {
        public static final String ITEMS_COUNT = "ITEMS_COUNT";
        public int numberOfItems;

        @Nullable
        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
            numberOfItems = getArguments().getInt(ITEMS_COUNT);
            View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
            final LargeAdapter adapter = new LargeAdapter(numberOfItems);
            recyclerView.setAdapter(adapter);
            final RecyclerViewFastScroller fastScroller = rootView.findViewById(R.id.fastscroller);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
                @Override
                public void onLayoutCompleted(final State state) {
                    super.onLayoutCompleted(state);
                    final int firstVisibleItemPosition = findFirstVisibleItemPosition();
                    //Log.d("AppLog", "onLayoutCompleted firstVisibleItemPosition:"+firstVisibleItemPosition);
                    final int lastVisibleItemPosition = findLastVisibleItemPosition();
                    int itemsShown = lastVisibleItemPosition - firstVisibleItemPosition + 1;
                    //if all items are shown, hide the fast-scroller
                    fastScroller.setVisibility(adapter.getItemCount() > itemsShown ? View.VISIBLE : View.GONE);
                }
            });
            fastScroller.setRecyclerView(recyclerView);
            fastScroller.setViewsToUse(R.layout.recycler_view_fast_scroller__fast_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);
            //new Handler().postDelayed(new Runnable() {
            //    @Override
            //    public void run() {
            //        adapter.setItemsCount(new Random().nextInt(20));
            //        adapter.notifyDataSetChanged();
            //    }
            //}, 2000L);
            return rootView;
        }
    }
}
