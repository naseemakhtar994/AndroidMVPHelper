package com.ufkoku.demo_app.ui.fragments.retainable;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.fragments.view.IFragmentsDataView;
import com.ufkoku.mvp_base.view.IMvpView;

import java.util.ArrayList;


public interface IRetainableFragment extends IMvpView, IFragmentsDataView {

    void onDataLoaded(ArrayList<AwesomeEntity> entities);

}