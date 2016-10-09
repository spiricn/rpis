package com.rpis.service.comm;

import com.rpis.service.comm.RpisResult;
import com.rpis.service.comm.Color;
import com.rpis.service.comm.ServerInfo;


interface IServerScanCallback {
	boolean onServerFound(in ServerInfo server);
}
