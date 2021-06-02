package com.wya.env.module.home.detail;

import com.wya.env.base.BaseView;
import com.wya.env.bean.doodle.LampModel;

interface DetailView extends BaseView {
    void onJsonResult(LampModel lampModel);
}
