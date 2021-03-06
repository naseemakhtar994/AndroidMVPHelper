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

package com.ufkoku.mvp.savable

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import com.ufkoku.mvp_base.presenter.IPresenter
import com.ufkoku.mvp_base.view.IMvpView
import com.ufkoku.mvp_base.viewstate.ISavableViewState

abstract class BaseSavableFragment<V : IMvpView, P : IPresenter<V>, VS : ISavableViewState<V>> : Fragment() {

    protected var presenter: P? = null

    protected var viewState: VS? = null

    //-----------------------------------------------------------------------------------------//

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

    //---------------------------------------------------------------------------------------//

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewState = createNewViewState()
        if (savedInstanceState != null) {
            viewState!!.restore(savedInstanceState)
        }

        presenter = createPresenter()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewState!!.apply(getMvpView())
        presenter!!.onAttachView(getMvpView())

        onInitialized(presenter!!, viewState!!)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        if (outState != null) {
            viewState!!.save(outState)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        presenter?.onDetachView()

        if (presenter is IAsyncPresenter<*>) {
            (presenter as IAsyncPresenter<*>).cancel()
        }

        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter = null
        viewState = null
    }

}