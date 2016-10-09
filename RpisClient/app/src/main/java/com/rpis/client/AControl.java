package com.rpis.client;

import com.rpis.service.comm.IRpisService;

public abstract class AControl extends ATab {
    public AControl(IRpisService service) {
        super(service);
    }
}
