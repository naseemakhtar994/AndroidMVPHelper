package com.ufkoku.demo_app.ui.activity.view;

import com.ufkoku.demo_app.entity.AwesomeEntity;

public interface IDataView {

    void populateAwesomeEntity(AwesomeEntity entity);

    void setWaitViewVisible(boolean visible);

}
