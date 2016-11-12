package com.ufkoku.demo_app.ui.activity.retainable;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.activity.view.DataView;
import com.ufkoku.mvp.retainable.BaseRetainableActivity;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Zwei on 09.11.2016.
 */

public class RetainableActivity extends BaseRetainableActivity<IRetainableActivity, RetainablePresenter, RetainableViewState> implements IRetainableActivity {

    private DataView view;

    //---------------------------------------------------------------------------------//

    @Override
    public void createView() {
        view = (DataView) getLayoutInflater().inflate(R.layout.app_data_view, null);
        setContentView(view);
    }

    @NotNull
    @Override
    public IRetainableActivity getMvpView() {
        return this;
    }

    @NotNull
    @Override
    public RetainableViewState createNewViewState() {
        return new RetainableViewState();
    }

    @NotNull
    @Override
    public RetainablePresenter createPresenter() {
        return new RetainablePresenter();
    }


    @Override
    public void onInitialized(RetainablePresenter demoRetainablePresenter, RetainableViewState demoRetainableViewState) {
        if (!demoRetainableViewState.isApplied()){
            if (!demoRetainablePresenter.isTaskRunning(RetainablePresenter.TASK_FETCH_DATA)){
                demoRetainablePresenter.fetchData();
            }
        }
    }

    //---------------------------------------------------------------------------------//

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view = null;
    }

    //---------------------------------------------------------------------------------//

    @Override
    public void onAwesomeEntityLoaded(AwesomeEntity entity) {
        RetainableViewState state = getViewState();
        if (state != null){
            state.setEntity(entity);
        }
        populateAwesomeEntity(entity);
    }

    @Override
    public void populateAwesomeEntity(AwesomeEntity entity) {
        if (view != null){
            view.setWaitViewVisible(false);
            view.populateAwesomeEntity(entity);
        }
    }

    @Override
    public void setWaitViewVisible(boolean visible) {
        if (view != null){
            view.setWaitViewVisible(visible);
        }
    }
}
