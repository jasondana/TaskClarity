package com.reductivetech.taskclarity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.reductivetech.taskclarity.nav.NavDrawerItem;
import com.reductivetech.taskclarity.nav.NavDrawerListAdapter;
import com.reductivetech.taskclarity.page.NewPageDialogFragment;
import com.reductivetech.taskclarity.nav.TabbedFragment;
import com.reductivetech.taskclarity.page.NewTaskDialogFragment;
import com.reductivetech.taskclarity.page.TaskFragment;
import com.reductivetech.taskclarity.settings.SettingsActivity;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements TaskFragment.OnUpdateListener, TabbedFragment.OnUpdateListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    // Navigation Drawer
    DrawerLayout _drawerLayout;
    ListView _drawerList;
    ActionBarDrawerToggle _drawerToggle;

    // Navigation Drawer Menu
    String[] _navMenuTitles;
    TypedArray _navMenuIcons;
    ArrayList<NavDrawerItem> _navDrawerItems;

    // Navigation Drawer Constants
    final static int NAV_ADD_PAGE = 0;
    final static int NAV_DEL_PAGE = 1;
    final static int NAV_SETTINGS = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
        initDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (_drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.add_task:
                addTask();
                return true;
            case R.id.add_page:
                addPage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        // Do not save fragments so orientation change functions properly
        // super.onSaveInstanceState(outState);
    }

    void initDrawer() {
        _navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        _navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        _navDrawerItems = new ArrayList<NavDrawerItem>();
        _navDrawerItems.add(
                new NavDrawerItem(
                        _navMenuTitles[NAV_SETTINGS],
                        _navMenuIcons.getResourceId(NAV_SETTINGS, -1))
        );

        _navMenuIcons.recycle();

        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        _drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,  GravityCompat.START);

        _drawerList = (ListView) findViewById(R.id.left_drawer);
        _drawerList.setAdapter(new NavDrawerListAdapter(getApplicationContext(), _navDrawerItems));
        _drawerList.setOnItemClickListener(new DrawerItemClickListener());

        _drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                _drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
            }
        };
        _drawerLayout.setDrawerListener(_drawerToggle);

        ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            Log.e(TAG, "Action bar is null");
            return;
        }

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        _drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        _drawerToggle.onConfigurationChanged(newConfig);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        switch (position) {
            case NAV_SETTINGS:
                openSettings();
                break;
            default:
                break;
        }
        _drawerList.setItemChecked(position, true);
        _drawerLayout.closeDrawer(_drawerList);
    }

    void addTask() {
        TabbedFragment tabbedFragment =
                (TabbedFragment) getSupportFragmentManager().findFragmentById(R.id.tabbed_fragment);
        TabbedFragment.Page page = tabbedFragment.getCurrentPage();
        DialogFragment dialog = new NewTaskDialogFragment(page.id);
        dialog.show(getSupportFragmentManager(), "NewTaskDialogFragments");
    }

    void addPage() {
        DialogFragment dialog = new NewPageDialogFragment();
        dialog.show(getSupportFragmentManager(), "NewPageDialogFragment");
    }

    void deletePage() {
        TabbedFragment tabbedFragment =
                (TabbedFragment) getSupportFragmentManager().findFragmentById(R.id.tabbed_fragment);
        // TODO: Are you sure? dialog
        tabbedFragment.delete();
    }

    void openSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    public void onTaskAdded() {
        TabbedFragment tabbedFragment =
                (TabbedFragment) getSupportFragmentManager().findFragmentById(R.id.tabbed_fragment);
        tabbedFragment.update();
    }

    @Override
    public void onPageAdded(long id, String page) {
        TabbedFragment tabbedFragment =
                (TabbedFragment) getSupportFragmentManager().findFragmentById(R.id.tabbed_fragment);
        tabbedFragment.add(id, page);
    }

}
