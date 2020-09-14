package com.wya.env.module.register;

import com.wya.env.base.BaseView;
import com.wya.env.bean.login.LoginInfo;

public interface RegisterView extends BaseView {
    void onRegisterResult(LoginInfo loginInfo);
}
