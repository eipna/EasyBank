package com.eipna.easybank.ui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.eipna.easybank.data.Database;
import com.eipna.easybank.util.NotificationUtil;
import com.eipna.easybank.util.PreferenceUtil;

public abstract class BaseActivity extends AppCompatActivity {

    protected Database databaseInstance;
    protected PreferenceUtil preferenceUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseInstance = new Database(BaseActivity.this);
        preferenceUtil = new PreferenceUtil(BaseActivity.this);

        NotificationUtil.createNotificationChannels(BaseActivity.this);
    }
}