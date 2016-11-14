package com.ufkoku.demo_app.ui.activity.savable;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.activity.view.DataView;
import com.ufkoku.mvp.savable.BaseSavableActivity;

import org.jetbrains.annotations.NotNull;

public class SavableActivity extends BaseSavableActivity<ISavableActivity, SavableActivityPresenter, SavableActivityViewState> implements ISavableActivity {

    private DataView view;

    //------------------------------------------------------------------------------------//

    @Override
    public void createView() {
        view = (DataView) getLayoutInflater().inflate(R.layout.app_data_view, null);
        setContentView(view);
    }

    @NotNull
    @Override
    public ISavableActivity getMvpView() {
        return this;
    }

    @NotNull
    @Override
    public SavableActivityViewState createNewViewState() {
        return new SavableActivityViewState();
    }

    @NotNull
    @Override
    public SavableActivityPresenter createPresenter() {
        return new SavableActivityPresenter();
    }

    @Override
    public void onInitialized(SavableActivityPresenter savablePresenter, SavableActivityViewState savableViewState) {
        if (!savableViewState.isApplied()){
            savablePresenter.fetchData();
        }
    }

    //------------------------------------------------------------------------------------//

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view = null;
    }

    //------------------------------------------------------------------------------------//

    @Override
    public void onAwesomeEntityLoaded(AwesomeEntity entity) {
        SavableActivityViewState state = getViewState();
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
