package com.wya.env.module.netdata;

import com.wya.env.base.BaseView;
import com.wya.env.bean.doodle.LampModel;
import com.wya.env.bean.doodle.NetModel;
import com.wya.env.bean.doodle.SaveModel;

import java.util.List;

/**
 * @date: 2018/7/3 13:56
 * @author: Chunjiang Mao
 * @classname: Fragment1View
 * @describe:
 */

public interface NetDataFragmentView extends BaseView {
    void onModelsResult(List<NetModel> data);
    void onUpLoadModelResult();
    void onComplete();
}
