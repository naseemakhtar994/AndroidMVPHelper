package com.ufkoku.demo_app.ui.fragments.retainable;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.fragments.base.IFragmentManager;
import com.ufkoku.demo_app.ui.fragments.savable.SavableFragment;
import com.ufkoku.demo_app.ui.fragments.view.FragmentsDataView;
import com.ufkoku.mvp.retainable.BaseRetainableFragment;

import org.jetbrains.annotations.NotNull;


public class RetainableFragment extends BaseRetainableFragment<IRetainableFragment, RetainableFragmentPresenter, RetainableFragmentViewState> implements IRetainableFragment {

    private FragmentsDataView view;

    //-----------------------------------------------------------------------------------//

    @NotNull
    @Override
    public IRetainableFragment getMvpView() {
        return this;
    }

    @NotNull
    @Override
    public RetainableFragmentViewState createNewViewState() {
        return new RetainableFragmentViewState();
    }

    @NotNull
    @Override
    public RetainableFragmentPresenter createPresenter() {
        return new RetainableFragmentPresenter();
    }

    @Override
    public void onInitialized(RetainableFragmentPresenter retainableFragmentPresenter, RetainableFragmentViewState retainableFragmentViewState) {
        if (!retainableFragmentViewState.isApplied()) {
            if (!retainableFragmentPresenter.isTaskRunning(retainableFragmentPresenter.TASK_FETCH_DATA)) { //task maybe waiting for view, because onDestroy wasn't called
                retainableFragmentPresenter.fetchData();
            }
        }
    }

    //-----------------------------------------------------------------------------------//

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

    //-----------------------------------------------------------------------------------//

    @Override
    public void onAwesomeEntityLoaded(AwesomeEntity entity) {
        RetainableFragmentViewState state = getViewState();
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
