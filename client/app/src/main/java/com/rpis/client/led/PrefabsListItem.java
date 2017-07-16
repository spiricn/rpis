package com.rpis.client.led;

import com.rpis.service.comm.Prefab;
import com.rpis.service.comm.ServerInfo;

public class PrefabsListItem {
    public PrefabsListItem(Prefab prefab){
        this.prefab = prefab;
    }

    public Prefab prefab = null;
}