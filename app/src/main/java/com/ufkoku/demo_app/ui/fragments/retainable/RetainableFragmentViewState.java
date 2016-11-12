package com.ufkoku.demo_app.ui.fragments.retainable;

import android.support.annotation.NonNull;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.fragments.savable.ISavableFragment;
import com.ufkoku.mvp_base.viewstate.IViewState;

import java.util.ArrayList;

/**
 * Created by Zwei on 10.11.2016.
 */

public class RetainableFragmentViewState implements IViewState<IRetainableFragment> {

    private transient boolean applied = false;

    private ArrayList<AwesomeEntity> entities;

    public ArrayList<AwesomeEntity> getEntity() {
        return entities;
    }

    public void setEntity(ArrayList<AwesomeEntity> entity) {
        this.entities = entity;
    }

    public boolean isApplied() {
        return applied;
    }

    @Override
    public void apply(@NonNull IRetainableFragment iRetainableFragment) {
        if (entities != null) {
            applied = true;
            iRetainableFragment.populateData(entities);
            iRetainableFragment.setWaitViewVisible(false);
        }
    }

}
