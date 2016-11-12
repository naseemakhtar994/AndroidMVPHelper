package com.ufkoku.demo_app.ui.fragments.view;

import com.ufkoku.demo_app.entity.AwesomeEntity;

import java.util.List;

/**
 * Created by Zwei on 12.11.2016.
 */

public interface IFragmentsDataView {

    void populateData(List<AwesomeEntity> entities);

    void setWaitViewVisible(boolean visible);

}
