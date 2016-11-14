/*
 * Copyright 2016 Ufkoku (https://github.com/Ufkoku/AndroidMVPHelper)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ufkoku.mvp.presenter.rx

import com.ufkoku.mvp.presenter.BaseAsyncPresenter
import com.ufkoku.mvp_base.view.IMvpView
import rx.*
import rx.schedulers.Schedulers

abstract class BaseAsyncRxPresenter<T : IMvpView> : BaseAsyncPresenter<T>() {

    var scheduler: Scheduler? = null

    override fun onAttachView(view: T) {
        super.onAttachView(view)
        if (scheduler == null) {
            scheduler = Schedulers.from(executor)
        }
    }

    override fun cancel() {
        super.cancel()
        if (scheduler != null) {
            scheduler = null
        }
    }

    abstract class UiWaitingOnSubscribe<T>(val presenter: BaseAsyncPresenter<*>) : Observable.OnSubscribe<T> {

        final override fun call(t: Subscriber<in T>?) {
            call(UiWaitingOnSubscriber(t!!, presenter))
        }

        abstract fun call(subscriber: UiWaitingOnSubscriber<T>)

    }

    class UiWaitingOnSubscriber<T>(val subscriber: Subscriber<in T>, val presenter: BaseAsyncPresenter<*>) : Observer<T>, Subscription {

        override fun isUnsubscribed(): Boolean {
            return subscriber.isUnsubscribed
        }

        override fun unsubscribe() {
            subscriber.unsubscribe()
        }

        override fun onNext(t: T) {
            presenter.waitForViewIfNeeded()
            subscriber.onNext(t)
        }

        override fun onCompleted() {
            presenter.waitForViewIfNeeded()
            subscriber.onCompleted()
        }

        override fun onError(e: Throwable?) {
            presenter.waitForViewIfNeeded()
            subscriber.onError(e)
        }

    }

}
