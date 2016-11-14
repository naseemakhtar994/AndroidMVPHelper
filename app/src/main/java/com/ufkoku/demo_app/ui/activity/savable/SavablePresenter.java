package com.ufkoku.demo_app.ui.activity.savable;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.mvp.presenter.rx.BaseAsyncRxPresenter;

import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class SavablePresenter extends BaseAsyncRxPresenter<ISavableActivity> {

    @NotNull
    @Override
    protected ExecutorService createExecutor() {
        return new ScheduledThreadPoolExecutor(1);
    }

    public void fetchData() {
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

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(AwesomeEntity entity) {
                        ISavableActivity activity = getView();
                        if (activity != null) {
                            activity.onAwesomeEntityLoaded(entity);
                        }
                    }
                });
    }

}
