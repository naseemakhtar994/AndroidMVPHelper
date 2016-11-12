package com.ufkoku.demo_app.ui.activity.retainable;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.mvp_base.viewstate.IViewState;

/**
 * Created by Zwei on 09.11.2016.
 */

public class RetainableViewState implements IViewState<IRetainableActivity> {

    private transient boolean applied = false;

    private AwesomeEntity entity;

    public AwesomeEntity getEntity() {
        return entity;
    }

    public void setEntity(AwesomeEntity entity) {
        this.entity = entity;
    }

    public boolean isApplied() {
        return applied;
    }

    @Override
    public void apply(IRetainableActivity iSavableActivity) {
        if (entity != null){
            applied = true;
            iSavableActivity.populateAwesomeEntity(entity);
            iSavableActivity.setWaitViewVisible(false);
        }
    }

}
