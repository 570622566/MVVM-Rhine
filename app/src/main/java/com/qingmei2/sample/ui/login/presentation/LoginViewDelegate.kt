package com.qingmei2.sample.ui.login.presentation

import com.qingmei2.rhine.base.viewdelegate.IViewDelegate
import com.qingmei2.rhine.ext.livedata.toFlowable

@SuppressWarnings("CheckResult")
class LoginViewDelegate(
        val viewModel: LoginViewModel,
        private val navigator: LoginNavigator
) : IViewDelegate {

    init {
        viewModel.userInfo
                .toFlowable()
                .subscribe { it ->
                    navigator.toMain()
                }
    }

    fun login() = viewModel.login()

}