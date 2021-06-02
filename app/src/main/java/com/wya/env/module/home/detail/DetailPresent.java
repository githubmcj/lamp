package com.wya.env.module.home.detail;

import com.wya.env.base.BasePresent;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.net.BaseExt;
import com.wya.env.net.BaseSubscriber;
import com.wya.env.net.api.ResultApi;

public class DetailPresent extends BasePresent<DetailView> {
    private ResultApi resultApi = new ResultApi();
    public void getJson(String json) {
        mView.showLoading();
        BaseExt.ext(resultApi.getJson(json), new BaseSubscriber<LampModel>(mView) {
            @Override
            public void onNext(LampModel lampModel) {
                mView.onJsonResult(lampModel);
            }

            @Override
            public void onComplete() {
            }
        });
    }

}
