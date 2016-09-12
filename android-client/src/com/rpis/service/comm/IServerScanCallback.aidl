package com.rpis.service.comm;

import com.rpis.service.comm.RpisResult;
import com.rpis.service.comm.Color;


interface IServerScanCallback {
	boolean onServerFound(String address, int port);
}
