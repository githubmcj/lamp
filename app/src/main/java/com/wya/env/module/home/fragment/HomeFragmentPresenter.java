package com.wya.env.module.home.fragment;

import com.wya.env.base.BasePresent;
import com.wya.env.bean.BaseResult;
import com.wya.env.bean.doodle.SaveModel;
import com.wya.env.net.BaseExt;
import com.wya.env.net.BaseSubscriber;
import com.wya.env.net.api.ResultApi;
import com.wya.env.util.ResultStatusUtil;
import com.wya.utils.utils.LogUtil;

import java.util.List;

/**
 * @date: 2018/7/3 13:56
 * @author: Chunjiang Mao
 * @classname: Fragment1Presenter
 * @describe:
 */

public class HomeFragmentPresenter extends BasePresent<HomeFragmentView> {

    private ResultApi resultApi = new ResultApi();

    /**
     * 獲取模板
     */
    public void getSaveModels() {
        //业务逻辑的处理
//        mView.showLoading();
        BaseExt.ext(resultApi.getSaveModelsApi(), new BaseSubscriber<BaseResult<List<SaveModel>>>(mView) {
            @Override
            public void onNext(BaseResult<List<SaveModel>> result) {
                if (ResultStatusUtil.resultStatus(mView, result.code, result.msg, result.success)) {
                    mView.onModelsResult(result.data);
                }
            }
        });
    }

    /**
     * 上传模板
     *
     * @param content
     */
    public void upLoadModel(String content) {
        mView.showLoading();
        LogUtil.e(content);
        BaseExt.ext(resultApi.saveModelApi(content), new BaseSubscriber<BaseResult<Object>>(mView) {
            @Override
            public void onNext(BaseResult<Object> loginInfoBaseResult) {
                if (ResultStatusUtil.resultStatus(mView, loginInfoBaseResult.code, loginInfoBaseResult.msg, loginInfoBaseResult.success)) {
                    mView.onUpLoadModelResult();
                }
            }

            @Override
            public void onComplete() {
            }
        });
    }

}

