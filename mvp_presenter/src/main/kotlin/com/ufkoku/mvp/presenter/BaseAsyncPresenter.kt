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

package com.ufkoku.mvp.presenter

import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import com.ufkoku.mvp_base.view.IMvpView
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseAsyncPresenter<T : IMvpView> : BasePresenter<T>(), IAsyncPresenter<T> {

    protected var executor: ExecutorService? = null

    private val runningTasks: MutableList<Int> = Collections.synchronizedList(LinkedList())

    /**
     * This variable is true if view detached, but screen is continuing it work,
     * for example recreation of view on screen rotation
     * */
    protected val waitForView = AtomicBoolean(false)

    /**
     * Variable is used in method waitForViewIfNeeded().
     * */
    protected val lockObject = Object()

    protected abstract fun createExecutor(): ExecutorService

    override fun onAttachView(view: T) {
        super.onAttachView(view)
        if (executor == null) {
            executor = createExecutor()
        }

        synchronized(lockObject) {
            waitForView.set(false)
            try {
                lockObject.notifyAll()
            } catch (ignored: IllegalMonitorStateException) {

            }
        }
    }

    override fun onDetachView() {
        super.onDetachView()
        waitForView.set(true)
    }

    override fun cancel() {
        if (executor != null) {
            executor!!.shutdownNow()
            executor = null
        }

        synchronized(lockObject) {
            waitForView.set(false)
            try {
                lockObject.notifyAll()
            } catch (ignored: IllegalMonitorStateException) {

            }
        }

        runningTasks.clear()
    }

    /**
     * You can call this method, before populating result (from worker thread).
     * If waitForView is true, it will call wait() on lockObject.
     * lockObject.notifyAll() will be called after onAttachView().
     * */
    fun waitForViewIfNeeded() {
        synchronized(lockObject) {
            if (waitForView.get()) {
                try {
                    lockObject.wait()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun notifyTaskAdded(task: Int) {
        runningTasks.add(task)
    }

    fun notifyTaskFinished(task: Int) {
        runningTasks.remove(task)
    }

    fun isTaskRunning(task: Int): Boolean {
        return runningTasks.contains(task)
    }

    fun hasRunningTasks(): Boolean {
        return runningTasks.size > 0
    }

}
