package com.wya.env.net.tpc.entity.basemsg;

import com.wya.env.net.tpc.entity.basemsg.IResponse;

/**
 * Author：Alex
 * Date：2019/12/7
 */
public abstract class SuperCallbackResponse implements IResponse {

    public abstract String getCallbackId();

    public abstract void setCallbackId(String callbackId);

}
