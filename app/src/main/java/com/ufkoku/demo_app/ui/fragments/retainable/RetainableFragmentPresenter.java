package com.ufkoku.demo_app.ui.fragments.retainable;

import android.support.annotation.NonNull;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.fragments.savable.ISavableFragment;
import com.ufkoku.mvp.presenter.BaseAsyncPresenter;
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
 * Created by Zwei on 10.11.2016.
 */

public class RetainableFragmentPresenter extends BaseAsyncRxPresenter<IRetainableFragment> {

    public static Integer TASK_FETCH_DATA = 1;

    @NotNull
    @Override
    protected ExecutorService createExecutor() {
        return new ScheduledThreadPoolExecutor(1);
    }

    public void fetchData() {
        notifyTaskAdded(TASK_FETCH_DATA);
        Observable.create(new BaseAsyncRxPresenter.UiWaitingOnSubscribe<ArrayList<AwesomeEntity>>(this) {
            @Override
            public void call(@NonNull BaseAsyncRxPresenter.UiWaitingOnSubscriber<ArrayList<AwesomeEntity>> uiWaitingOnSubscriber) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ArrayList<AwesomeEntity> entities = new ArrayList<>(100);
                if (new Random().nextBoolean()) {
                    for (int i = 0; i < 100; i++) {
                        entities.add(new AwesomeEntity(i));
                    }
                } else {
                    for (int i = 99; i >= 0; i--) {
                        entities.add(new AwesomeEntity(i));
                    }
                }

                uiWaitingOnSubscriber.onNext(entities);
                uiWaitingOnSubscriber.onCompleted();
            }
        })
                .subscribeOn(getScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<AwesomeEntity>>() {

                    @Override
                    public void onCompleted() {
                        notifyTaskFinished(TASK_FETCH_DATA);
                    }

                    @Override
                    public void onError(Throwable e) {
                        notifyTaskFinished(TASK_FETCH_DATA);
                    }

                    @Override
                    public void onNext(ArrayList<AwesomeEntity> entities) {
                        IRetainableFragment activity = getView();
                        if (activity != null) {
                            activity.onDataLoaded(entities);
                        }
                    }
                });
    }

}
