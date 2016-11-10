package com.ufkoku.demo_app.ui.fragments.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.ui.view.DataView;


public class FragmentsDataView extends DataView {

    private ViewListener listener;

    public FragmentsDataView(Context context) {
        super(context);
    }

    public FragmentsDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FragmentsDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViewById(R.id.retainable).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onRetainableClicked();
                }
            }
        });
        findViewById(R.id.savable).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onSavableClicked();
                }
            }
        });
    }

    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public interface ViewListener {

        void onRetainableClicked();

        void onSavableClicked();

    }

}
