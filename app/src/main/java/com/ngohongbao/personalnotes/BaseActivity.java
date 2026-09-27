package com.ngohongbao.personalnotes;

import android.app.Activity;
import android.os.Bundle;

/** Activity nền để mọi màn hình áp dụng theme trước khi onCreate. */
public abstract class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.apply(this);
        super.onCreate(savedInstanceState);
    }
}
