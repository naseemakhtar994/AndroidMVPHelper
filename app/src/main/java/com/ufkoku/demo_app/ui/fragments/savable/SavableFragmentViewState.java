package com.ufkoku.demo_app.ui.fragments.savable;

import android.support.annotation.NonNull;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.mvp.viewstate.autosavable.AutoSavable;
import com.ufkoku.mvp.viewstate.autosavable.AutoSavableViewState;

import java.util.ArrayList;

@AutoSavable
public class SavableFragmentViewState extends AutoSavableViewState<ISavableFragment> {

    private transient boolean applied = false;

    private ArrayList<AwesomeEntity> entities;

    public ArrayList<AwesomeEntity> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<AwesomeEntity> entity) {
        this.entities = entity;
    }

    public boolean isApplied() {
        return applied;
    }

    @Override
    public void apply(@NonNull ISavableFragment iSavableFragment) {
        if (entities != null) {
            applied = true;
            iSavableFragment.populateData(entities);
            iSavableFragment.setWaitViewVisible(false);
        }
    }

}
