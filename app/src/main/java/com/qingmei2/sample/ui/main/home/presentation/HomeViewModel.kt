package com.qingmei2.sample.ui.main.home.presentation

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import com.qingmei2.rhine.base.viewstate.SimpleViewState
import com.qingmei2.rhine.ext.lifecycle.bindLifecycle
import com.qingmei2.rhine.ext.livedata.toFlowable
import com.qingmei2.sample.base.BaseViewModel
import com.qingmei2.sample.http.RxSchedulers
import com.qingmei2.sample.http.entity.QueryUser
import com.qingmei2.sample.ui.main.home.data.HomeRepository
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

class HomeViewModel(
        private val repo: HomeRepository
) : BaseViewModel() {

    val query: MutableLiveData<String> = MutableLiveData()
    val error: MutableLiveData<Throwable> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val result: MutableLiveData<QueryUser> = MutableLiveData()

    override fun onCreate(lifecycleOwner: LifecycleOwner) {
        super.onCreate(lifecycleOwner)
        query.toFlowable()
                .flatMap { fetchUserInfo(it) }
                .startWith(SimpleViewState.idle())
                .bindLifecycle(this)
                .subscribe { state ->
                    when (state) {
                        is SimpleViewState.Loading -> applyState(isLoading = true)
                        is SimpleViewState.Idle -> applyState(isLoading = false)
                        is SimpleViewState.Error -> applyState(isLoading = false, error = state.error)
                        is SimpleViewState.Result -> applyState(isLoading = false, userInfo = state.result)
                    }
                }
    }

    private fun fetchUserInfo(username: String): Flowable<SimpleViewState<QueryUser>> =
            repo.fetchUserInfo(username)
                    .map { SimpleViewState.result(it) }
                    .startWith(SimpleViewState.loading())

    private fun applyState(isLoading: Boolean, userInfo: QueryUser? = null, error: Throwable? = null) {
        this.loading.postValue(isLoading)
        this.result.postValue(userInfo)
        this.error.postValue(error)
    }
}