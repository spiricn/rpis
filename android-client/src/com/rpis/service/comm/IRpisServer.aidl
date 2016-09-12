package com.rpis.service.comm;

import com.rpis.service.comm.ILedControl;
import com.rpis.service.comm.IDeviceControl;

interface IRpisServer {
	ILedControl getLedControl();
	
	IDeviceControl getDeviceControl();
	
	String getAddress();
	
	int getPort();
}
