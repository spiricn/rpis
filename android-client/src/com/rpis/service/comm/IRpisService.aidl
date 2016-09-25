package com.rpis.service.comm;

import com.rpis.service.comm.ILedControl;
import com.rpis.service.comm.IDeviceControl;
import com.rpis.service.comm.RpisResult;
import com.rpis.service.comm.IServerScanCallback;
import com.rpis.service.comm.IRpisServer;
import com.rpis.service.comm.ServerInfo;

interface IRpisService {
	RpisResult scan(in IServerScanCallback callback);
	
	IRpisServer connect(in ServerInfo server);
}
