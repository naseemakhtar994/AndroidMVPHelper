package com.ufkoku.demo_app.ui.fragments.savable;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.view.IDataView;
import com.ufkoku.mvp_base.view.IMvpView;


public interface ISavableFragment extends IMvpView, IDataView {

    void onAwesomeEntityLoaded(AwesomeEntity entity); //called from Presenter

}
