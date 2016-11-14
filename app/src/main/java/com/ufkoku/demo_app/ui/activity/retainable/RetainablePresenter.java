package com.ufkoku.demo_app.ui.activity.retainable;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.mvp.presenter.rx.BaseAsyncRxPresenter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Zwei on 09.11.2016.
 */

public class RetainablePresenter extends BaseAsyncRxPresenter<IRetainableActivity> {

    public static Integer TASK_FETCH_DATA = 1;

    @NotNull
    @Override
    protected ExecutorService createExecutor() {
        return new ScheduledThreadPoolExecutor(1);
    }

    public void fetchData() {
        notifyTaskAdded(TASK_FETCH_DATA);
        Observable.create(new BaseAsyncRxPresenter.UiWaitingOnSubscribe<AwesomeEntity>(this) {
            @Override
            public void call(UiWaitingOnSubscriber<AwesomeEntity> uiWaitingOnSubscriber) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                uiWaitingOnSubscriber.onNext(new AwesomeEntity(new Random().nextInt()));
                uiWaitingOnSubscriber.onCompleted();
            }
        })
                .subscribeOn(getScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AwesomeEntity>() {
                    @Override
                    public void onCompleted() {
                        notifyTaskFinished(TASK_FETCH_DATA);
                    }

                    @Override
                    public void onError(Throwable e) {
                        notifyTaskFinished(TASK_FETCH_DATA);
                    }

                    @Override
                    public void onNext(AwesomeEntity entity) {
                        IRetainableActivity activity = getView();
                        if (activity != null) {
                            activity.onAwesomeEntityLoaded(entity);
                        }
                    }
                });
    }

}
