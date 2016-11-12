package com.ufkoku.demo_app.ui.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.entity.AwesomeEntity;

public class DataView extends FrameLayout implements IDataView {

    private TextView tvData;
    private View vWaitView;

    //-------------------------------------------------------------------------------//

    public DataView(Context context) {
        super(context);
    }

    public DataView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //-------------------------------------------------------------------------------//

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        tvData = (TextView) findViewById(R.id.text);
        vWaitView = findViewById(R.id.waitView);
    }

    //-------------------------------------------------------------------------------//

    @Override
    public void populateAwesomeEntity(AwesomeEntity entity) {
        tvData.setText(entity.getImportantDataField() + "");
    }

    @Override
    public void setWaitViewVisible(boolean visible) {
        vWaitView.setVisibility(visible ? VISIBLE : GONE);
    }
}
