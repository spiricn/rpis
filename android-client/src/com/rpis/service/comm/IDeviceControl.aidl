package com.rpis.service.comm;

import com.rpis.service.comm.RpisResult;


interface IDeviceControl {
	RpisResult shutdown();
	
	RpisResult reboot();
}