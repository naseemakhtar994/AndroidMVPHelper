package com.ufkoku.demo_app.ui.activity.savable;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.mvp.viewstate.autosavable.AutoSavable;
import com.ufkoku.mvp.viewstate.autosavable.AutoSavableViewState;

@AutoSavable
public class SavableViewState extends AutoSavableViewState<ISavableActivity> {

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
    public void apply(ISavableActivity iSavableActivity) {
        if (entity != null){
            applied = true;
            iSavableActivity.populateAwesomeEntity(entity);
            iSavableActivity.setWaitViewVisible(false);
        }
    }

}
