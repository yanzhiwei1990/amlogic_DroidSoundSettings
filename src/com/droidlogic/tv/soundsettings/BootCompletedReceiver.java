package com.droidlogic.tv.soundsettings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
//import android.os.Process;
import android.widget.Toast;
import android.util.Log;
import android.content.ContentResolver;

import com.droidlogic.app.SystemControlManager;

public class BootCompletedReceiver extends BroadcastReceiver {
    static final String TAG = "DvReceiver";
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive (Context context, Intent intent) {
        Log.d(TAG, "onReceive = " + intent);
        if (intent.getAction().equalsIgnoreCase(ACTION)) {
            SystemControlManager sm = new SystemControlManager(context);
            if (sm.getPropertyBoolean("ro.vendor.platform.has.tvuimode", false)) {
                initAudioEffectService(context);
            } else {
                SoundParameterSettingManager sound = new SoundParameterSettingManager(context);
                sound.initParameterAfterBoot();
            }
        }
    }

    private void initAudioEffectService(Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "initAudioEffectService = " + context);
                AudioEffectsSettingManagerService.startActionStartup(context);
            }
        }).start();
    }
}


