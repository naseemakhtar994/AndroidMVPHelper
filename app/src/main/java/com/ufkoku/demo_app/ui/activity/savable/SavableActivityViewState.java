package com.ufkoku.demo_app.ui.activity.savable;

import android.os.Bundle;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.mvp.viewstate.autosavable.AutoSavable;
import com.ufkoku.mvp.viewstate.autosavable.SavableActivityViewStateSaver;
import com.ufkoku.mvp_base.viewstate.ISavableViewState;

@AutoSavable
public class SavableActivityViewState implements ISavableViewState<ISavableActivity> {

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
    public void save(Bundle bundle) {
        SavableActivityViewStateSaver.save(this, bundle);
    }

    @Override
    public void restore(Bundle bundle) {
        SavableActivityViewStateSaver.restore(this, bundle);
    }

    @Override
    public void apply(ISavableActivity iSavableActivity) {
        if (entity != null) {
            applied = true;
            iSavableActivity.populateAwesomeEntity(entity);
            iSavableActivity.setWaitViewVisible(false);
        }
    }

}
