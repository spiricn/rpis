package com.rpis.client;

import android.annotation.SuppressLint;
import android.content.AbstractThreadedSyncAdapter;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterViewFlipper;
import android.widget.ViewFlipper;

import com.rpis.client.led.LedFragment;
import com.rpis.client.locator.LocatorFragment;
import com.rpis.client.navigator.NavigatorFragment;
import com.rpis.service.comm.IRpisServer;
import com.rpis.service.comm.IRpisService;
import com.rpis.service.comm.ServerInfo;
import com.rpis.service.comm.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPager = (ViewPager)findViewById(R.id.mainPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mLocator = new ServiceLocator();

        mLocator.connect(this, new ServiceLocator.IListener() {
            @Override
            public void onServiceConnected(final IRpisService service) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        start(service);
                    }
                });
            }

            @Override
            public void onServiceDisconnected() {

            }
        });
    }

    private void start(final IRpisService service){
        mService = service;

        addPage(mLocatorPage = new LocatorFragment(mService, new LocatorFragment.IListener() {
            @Override
            public void onServerSelected(ServerInfo server) {
                try {
                    mServer  = mService.connect(server);
                } catch (RemoteException e) {
                    // TODo
                    e.printStackTrace();
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(ATab tab : mPages){
                            tab.selectServer(mServer);
                        }
                    }
                });

                setPage(mNavigatorPage);
            }
        }));

        List<AControl> controlList = new ArrayList<AControl>();

        controlList.add(new LedFragment(mService));

        addPage(mNavigatorPage = new NavigatorFragment(mService, controlList, new NavigatorFragment.IListener() {
            @Override
            public void onControlSelected(AControl control) {
                setPage((ATab)control);
            }
        }));

        for(AControl ctrl : controlList){
            addPage((ATab)ctrl);
        }

        mPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), mPages));

        setPage(mLocatorPage);
    }

    private void addPage(ATab page){
        int index = mPages.size();

        page.setIndex(index);

        mPages.add(page);
    }

    private void setPage(ATab page){
        if(mCurrentPage != null){
            mCurrentPage.onPageHide();
        }

        mPager.setCurrentItem(page.getIndex());

        page.onPageShow();

        mCurrentPage = page;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_servers){
            setPage(mLocatorPage);;
        }
        else if(id == R.id.nav_controls){
            setPage(mNavigatorPage);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private ViewPager mPager;
    private IRpisService mService;
    private IRpisServer mServer;
    private ServiceLocator mLocator;
    private List<ATab> mPages = new ArrayList<ATab>();
    private ATab mCurrentPage = null;

    private LocatorFragment mLocatorPage;
    private NavigatorFragment mNavigatorPage;
}
