package com.wya.env.module.doodle;

import com.wya.env.base.BasePresent;
import com.wya.env.bean.BaseResult;
import com.wya.env.net.BaseExt;
import com.wya.env.net.BaseSubscriber;
import com.wya.env.net.api.ResultApi;
import com.wya.env.util.ResultStatusUtil;
import com.wya.utils.utils.LogUtil;

/**
 * @date: 2018/7/3 13:56
 * @author: Chunjiang Mao
 * @classname: DoodleFragmentPresenter
 * @describe:
 */

public class DoodleFragmentPresenter extends BasePresent<DoodleFragmentView> {
    private ResultApi resultApi = new ResultApi();

    /**
     * 保存模板
     *
     * @param content
     */
    public void saveModel(String content) {
        mView.showLoading();
        LogUtil.e(content);
        BaseExt.ext(resultApi.saveModelApi(content), new BaseSubscriber<BaseResult<Object>>(mView) {
            @Override
            public void onNext(BaseResult<Object> loginInfoBaseResult) {
                if (ResultStatusUtil.resultStatus(mView, loginInfoBaseResult.code, loginInfoBaseResult.msg, loginInfoBaseResult.success)) {
                    mView.onSaveResult();
                }
            }
        });
    }
}
