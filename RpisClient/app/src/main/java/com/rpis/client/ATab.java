package com.rpis.client;

import android.support.v4.app.Fragment;

import com.rpis.service.comm.IRpisServer;
import com.rpis.service.comm.IRpisService;

public abstract class ATab extends Fragment{
    public ATab(IRpisService service){
        mService = service;
        mIndex = -1;
    }

    public void setIndex(int index){
        mIndex = index;
    }

    public abstract String getName();

    public void onServerSelected(){
    }

    public void onServerDeselected(){
    }

    public void selectServer(IRpisServer server){
        mSelectedServer = server;

        onServerSelected();
    }

    public void deselectServer(){
        onServerDeselected();

        mSelectedServer = null;
    }

    protected  IRpisService getService() {
        return mService;
    }

    protected IRpisServer getServer(){
        return  mSelectedServer;
    }


    public void onPageShow(){
    }

    public void onPageHide(){
    }

    public int getIndex(){
        return  mIndex;
    }

    private IRpisServer mSelectedServer;
    private IRpisService mService;
    private int mIndex;
}
