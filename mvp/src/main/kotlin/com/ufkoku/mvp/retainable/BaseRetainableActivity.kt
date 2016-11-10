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

package com.ufkoku.mvp.retainable

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import com.ufkoku.mvp_base.presenter.IPresenter
import com.ufkoku.mvp_base.view.IMvpView
import com.ufkoku.mvp_base.viewstate.IViewState

abstract class BaseRetainableActivity<V : IMvpView, P : IPresenter<V>, VS : IViewState<V>> : AppCompatActivity() {

    companion object {
        val STATE_FRAGMENT_TAG = "com.ufkoku.uicontrol.BaseActivity.StateFragment"
    }

    private var stateFragment: StateFragment<V, P, VS>? = null

    var presenter: P?
        get() {
            return stateFragment?.presenter
        }
        private set(value) {
            stateFragment?.presenter = value
        }

    var viewState: VS?
        get() {
            return stateFragment?.viewState
        }
        private set(value) {
            stateFragment?.viewState = value
        }

    //-----------------------------------------------------------------------------------------//

    /**
     * Call setContentView hear and init all UI variables
     * */
    abstract fun createView()

    abstract fun getMvpView(): V

    /**
     * Creating viewState
     * */
    abstract fun createNewViewState(): VS

    /**
     * Creating presenter
     * */
    abstract fun createPresenter(): P

    /**
     * This is methods is called when ui, view state and presenter are initialized, and viewState.apply() method called
     * */
    abstract fun onInitialized(presenter: P, viewState: VS)

    //-----------------------------------------------------------------------------------------//

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stateFragment = supportFragmentManager.findFragmentByTag(STATE_FRAGMENT_TAG) as StateFragment<V, P, VS>?
        if (stateFragment == null) {
            stateFragment = StateFragment()
            supportFragmentManager.beginTransaction()
                    .add(stateFragment, STATE_FRAGMENT_TAG)
                    .commit()
        }

        if (viewState == null) {
            viewState = createNewViewState()
        }

        if (presenter == null) {
            presenter = createPresenter()
        }

        createView()

        viewState!!.apply(getMvpView())
        presenter!!.onAttachView(getMvpView())

        onInitialized(presenter!!, viewState!!)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter!!.onDetachView()
        if (isFinishing && presenter is IAsyncPresenter<*>) {
            (presenter!! as IAsyncPresenter<*>).cancel()
        }
    }

    //-----------------------------------------------------------------------------------------//

    open class StateFragment<V : IMvpView, P : IPresenter<V>, VS : IViewState<V>> : Fragment() {

        var presenter: P? = null

        var viewState: VS? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }

    }

}