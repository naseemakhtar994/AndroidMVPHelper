package com.ufkoku.demo_app.ui.fragments.savable;

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


public class SavableFragmentPresenter extends BaseAsyncRxPresenter<ISavableFragment> {

    private List<Integer> runningTasks = Collections.synchronizedList(new ArrayList<Integer>());

    public static Integer TASK_FETCH_DATA = 1;

    @NotNull
    @Override
    protected ExecutorService createExecutor() {
        return new ScheduledThreadPoolExecutor(1);
    }

    public boolean isTaskRunning(Integer task) {
        return runningTasks.contains(task);
    }

    public void fetchData() {
        runningTasks.add(TASK_FETCH_DATA);
        Observable.create(new BaseAsyncRxPresenter.UiWaitinOnSubscribe<AwesomeEntity>(this) {
            @Override
            public void call(BaseAsyncRxPresenter.UiWaitingOnSubscriber<AwesomeEntity> uiWaitingOnSubscriber) {
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
                        runningTasks.remove(TASK_FETCH_DATA);
                    }

                    @Override
                    public void onError(Throwable e) {
                        runningTasks.remove(TASK_FETCH_DATA);
                    }

                    @Override
                    public void onNext(AwesomeEntity entity) {
                        ISavableFragment activity = getView();
                        if (activity != null) {
                            activity.onAwesomeEntityLoaded(entity);
                        }
                    }
                });
    }

}
