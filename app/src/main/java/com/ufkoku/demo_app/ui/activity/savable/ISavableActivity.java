package com.ufkoku.demo_app.ui.activity.savable;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.activity.view.IDataView;
import com.ufkoku.mvp_base.view.IMvpView;



public interface ISavableActivity extends IMvpView, IDataView {

    void onAwesomeEntityLoaded(AwesomeEntity entity); //called from Presenter

}
