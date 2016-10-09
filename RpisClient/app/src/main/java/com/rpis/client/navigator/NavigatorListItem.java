package com.rpis.client.navigator;

import com.rpis.client.AControl;
import com.rpis.service.comm.ServerInfo;

public class NavigatorListItem{
    public NavigatorListItem(String name, AControl control){
        this.control = control;
        this.name = name;
    }

    public AControl control;
    public String name = "";
}