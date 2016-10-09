package com.rpis.client.locator;

import com.rpis.service.comm.ServerInfo;

public class ServerListItem{
    public ServerListItem(String name, ServerInfo server){
        this.server = server;
        this.name = name;
    }

    public ServerInfo server;
    public String name = "";
}