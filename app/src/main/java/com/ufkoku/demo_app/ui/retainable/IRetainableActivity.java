package com.ufkoku.demo_app.ui.retainable;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.view.IDataView;
import com.ufkoku.mvp_base.view.IMvpView;

/**
 * Created by Zwei on 09.11.2016.
 */

public interface IRetainableActivity extends IMvpView, IDataView {

    void onAwesomeEntityLoaded(AwesomeEntity entity); //called from Presenter

}