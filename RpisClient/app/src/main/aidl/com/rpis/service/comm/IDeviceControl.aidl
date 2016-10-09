package com.rpis.service.comm;

import com.rpis.service.comm.RpisResult;


interface IDeviceControl {
	RpisResult shutdown(int delayMs);

	RpisResult reboot(int delayMs);

	RpisResult stopServer(int delayMs);

	RpisResult ping();
}