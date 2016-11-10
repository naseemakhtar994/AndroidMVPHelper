package com.ufkoku.demo_app.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.ui.fragments.base.IFragmentManager;
import com.ufkoku.demo_app.ui.fragments.retainable.RetainableFragment;
import com.ufkoku.demo_app.ui.fragments.savable.SavableFragment;


public class FragmentsActivity extends AppCompatActivity implements IFragmentManager {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);
        findViewById(R.id.savable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SavableFragment());
            }
        });
        findViewById(R.id.retainable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new RetainableFragment());
            }
        });
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

}
