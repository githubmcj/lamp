package com.wya.env.module.home.fragment;

import com.wya.env.base.BaseView;
import com.wya.env.bean.doodle.SaveModel;

import java.util.List;

/**
 * @date: 2018/7/3 13:56
 * @author: Chunjiang Mao
 * @classname: Fragment1View
 * @describe:
 */

public interface HomeFragmentView extends BaseView {
    void onModelsResult(List<SaveModel> data);
    void onUpLoadModelResult();
}
