package com.ufkoku.demo_app.ui.fragments.savable;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.fragments.base.IFragmentManager;
import com.ufkoku.demo_app.ui.fragments.retainable.RetainableFragment;
import com.ufkoku.demo_app.ui.fragments.view.FragmentsDataView;
import com.ufkoku.mvp.savable.BaseSavableFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SavableFragment extends BaseSavableFragment<ISavableFragment, SavableFragmentPresenter, SavableFragmentViewState> implements ISavableFragment {

    private FragmentsDataView view;

    //----------------------------------------------------------------------------------------//

    @NotNull
    @Override
    public ISavableFragment getMvpView() {
        return this;
    }

    @NotNull
    @Override
    public SavableFragmentViewState createNewViewState() {
        return new SavableFragmentViewState();
    }

    @NotNull
    @Override
    public SavableFragmentPresenter createPresenter() {
        return new SavableFragmentPresenter();
    }

    @Override
    public void onInitialized(SavableFragmentPresenter savableFragmentPresenter, SavableFragmentViewState savableViewState) {
        if (!savableViewState.isApplied()) {
            if (!savableFragmentPresenter.isTaskRunning(SavableFragmentPresenter.TASK_FETCH_DATA)) { //task maybe waiting for view, because onDestroy wasn't called
                savableFragmentPresenter.fetchData();
            }
        }
    }

    //----------------------------------------------------------------------------------------//

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (FragmentsDataView) inflater.inflate(R.layout.fragment_data_view, container, false);
        view.setListener(new FragmentsDataView.ViewListener() {
            @Override
            public void onRetainableClicked() {
                Context context = getContext();
                if (context instanceof IFragmentManager){
                    ((IFragmentManager) context).setFragment(new RetainableFragment());
                }
            }

            @Override
            public void onSavableClicked() {
                Context context = getContext();
                if (context instanceof IFragmentManager){
                    ((IFragmentManager) context).setFragment(new SavableFragment());
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }

    //----------------------------------------------------------------------------------------//

    @Override
    public void onDataLoaded(ArrayList<AwesomeEntity> entities) {
        SavableFragmentViewState state = getViewState();
        if (state != null){
            state.setEntities(entities);
        }
        populateData(entities);
    }

    @Override
    public void populateData(List<AwesomeEntity> entity) {
        if (view != null){
            view.setWaitViewVisible(false);
            view.populateData(entity);
        }
    }

    @Override
    public void setWaitViewVisible(boolean visible) {
        if (view != null){
            view.setWaitViewVisible(visible);
        }
    }


}
