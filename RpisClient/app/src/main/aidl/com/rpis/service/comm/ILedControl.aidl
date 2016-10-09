package com.rpis.service.comm;

import com.rpis.service.comm.RpisResult;
import com.rpis.service.comm.Color;
import com.rpis.service.comm.Prefab;


interface ILedControl {
	RpisResult powerOn();
	
	RpisResult powerOff();
	
	boolean isPoweredOn();
	
	Color getColor();
	
	RpisResult setColor(in Color color);

	RpisResult stopPrefab();

	RpisResult runPrefab(int id);

	Prefab[] getPrefabs();
}
