package com.ufkoku.demo_app.ui.fragments.retainable;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.fragments.savable.ISavableFragment;
import com.ufkoku.mvp_base.viewstate.IViewState;

/**
 * Created by Zwei on 10.11.2016.
 */

public class RetainableFragmentViewState implements IViewState<IRetainableFragment> {

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
    public void apply(IRetainableFragment iSavableActivity) {
        if (entity != null) {
            applied = true;
            iSavableActivity.populateAwesomeEntity(entity);
            iSavableActivity.setWaitViewVisible(false);
        }
    }

}
