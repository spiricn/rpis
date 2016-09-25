package com.rpis.service.comm;

import com.rpis.service.comm.RpisResult;
import com.rpis.service.comm.Color;


interface ILedControl {
	RpisResult powerOn();
	
	RpisResult powerOff();
	
	boolean isPoweredOn();
	
	Color getColor();
	
	RpisResult setColor(in Color color);
}
