package com.lb.recyclerview_fast_scroller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lb.lollipopcontactsrecyclerviewfastscroller.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //note : part of the design library sample code was taken from : https://github.com/sitepoint-editors/Design-Demo/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(R.id.coordinator), "I'm a Snackbar", Snackbar.LENGTH_LONG).setAction("Action", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Snackbar Action", Toast.LENGTH_LONG).show();
                    }
                }).show();
            }
        });

        DesignDemoPagerAdapter adapter = new DesignDemoPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressWarnings("deprecation")
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

        public DesignDemoPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            final RecyclerViewFragment recyclerViewFragment = new RecyclerViewFragment();
            recyclerViewFragment.numberOfItems = getFragmentItemsCount(position);
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
        public int numberOfItems;

        @Nullable
        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
            final LargeAdapter adapter = new LargeAdapter(numberOfItems);
            recyclerView.setAdapter(adapter);
            final RecyclerViewFastScroller fastScroller = (RecyclerViewFastScroller) rootView.findViewById(R.id.fastscroller);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
                @Override
                public void onLayoutChildren(final RecyclerView.Recycler recycler, final RecyclerView.State state) {
                    super.onLayoutChildren(recycler, state);
                    //TODO if the items are filtered, considered hiding the fast scroller here
                    final int firstVisibleItemPosition = findFirstVisibleItemPosition();
                    if (firstVisibleItemPosition != 0) {
                        // this avoids trying to handle un-needed calls
                        if (firstVisibleItemPosition == -1)
                            //not initialized, or no items shown, so hide fast-scroller
                            fastScroller.setVisibility(View.GONE);
                        return;
                    }
                    final int lastVisibleItemPosition = findLastVisibleItemPosition();
                    int itemsShown = lastVisibleItemPosition - firstVisibleItemPosition + 1;
                    //if all items are shown, hide the fast-scroller
                    fastScroller.setVisibility(adapter.getItemCount() > itemsShown ? View.VISIBLE : View.GONE);
                }
            });
            fastScroller.setRecyclerView(recyclerView);
            fastScroller.setViewsToUse(R.layout.recycler_view_fast_scroller__fast_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);
            return rootView;
        }
    }
}
