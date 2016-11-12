package com.ufkoku.demo_app.ui.fragments.savable;

import android.support.annotation.NonNull;

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
        Observable.create(new BaseAsyncRxPresenter.UiWaitinOnSubscribe<ArrayList<AwesomeEntity>>(this) {
            @Override
            public void call(@NonNull BaseAsyncRxPresenter.UiWaitingOnSubscriber<ArrayList<AwesomeEntity>> uiWaitingOnSubscriber) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Random random = new Random();
                ArrayList<AwesomeEntity> entities = new ArrayList<>(500);
                for (int i = 0; i < 500; i++){
                    entities.add(new AwesomeEntity(random.nextInt()));
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
                        runningTasks.remove(TASK_FETCH_DATA);
                    }

                    @Override
                    public void onError(Throwable e) {
                        runningTasks.remove(TASK_FETCH_DATA);
                    }

                    @Override
                    public void onNext(ArrayList<AwesomeEntity> entities) {
                        ISavableFragment activity = getView();
                        if (activity != null) {
                            activity.onDataLoaded(entities);
                        }
                    }
                });
    }

}
