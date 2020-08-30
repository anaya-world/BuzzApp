package com.example.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.ModelClasses.ConnectionEvent;
import com.example.myapplication.Utils.PreferenceUtils;
import com.example.myapplication.Utils.WaitingDialog;
import com.sendbird.android.SendBird;
import com.sendbird.syncmanager.SendBirdSyncManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();

       // registerConnectionHandler();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    protected String getConnectionHandlerId() {
        return "CONNECTION_HANDLER_MAIN_ACTIVITY";
    }

    private void registerConnectionHandler() {
        SendBird.addConnectionHandler(getConnectionHandlerId(), new SendBird.ConnectionHandler() {
            @Override
            public void onReconnectStarted() {
                SendBirdSyncManager.getInstance().pauseSync();
            }

            @Override
            public void onReconnectSucceeded() {
                SendBirdSyncManager.getInstance().resumeSync();
            }

            @Override
            public void onReconnectFailed() {
            }
        });
    }

    // Shows or hides the ProgressBar
    protected void showProgressBar(boolean show) {
        if (show) {
            WaitingDialog.show(this);
        } else {
            WaitingDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

      //  SendBird.removeConnectionHandler(getConnectionHandlerId());
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConnectionEvent event) {
        if (!event.isConnected() && PreferenceUtils.getConnected()) {
            Toast.makeText(this, "retrying...", Toast.LENGTH_SHORT).show();
        }
    }}
