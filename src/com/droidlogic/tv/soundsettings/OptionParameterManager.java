/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.droidlogic.tv.soundsettings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.provider.Settings;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.droidlogic.app.SystemControlManager;
import com.droidlogic.app.OutputModeManager;

import com.droidlogic.tv.soundsettings.R;

public class OptionParameterManager {

    public static final String TAG = "OptionParameterManager";
	public static final boolean DEBUG = true;
    public static final String KEY_MENU_TIME = "menu_time";//DroidLogicTvUtils.KEY_MENU_TIME;
    public static final int DEFUALT_MENU_TIME = 1;//DroidLogicTvUtils.DEFUALT_MENU_TIME;

    private Resources mResources;
    private Context mContext;
    private SystemControlManager mSystemControlManager;

    public OptionParameterManager (Context context) {
        mContext = context;
        mResources = mContext.getResources();
        mSystemControlManager = ((TvSettingsActivity)mContext).getSystemControlManager();
    }

    public static boolean CanDebug() {
        return DEBUG;
    }

    public String getDtsDrcScale() {
        return mSystemControlManager.getPropertyString("persist.vendor.sys.dtsdrcscale", OutputModeManager.DEFAULT_DRC_SCALE);
    }

    public boolean getSurportDolby() {
       return mSystemControlManager.getPropertyBoolean("ro.vendor.platform.support.dolby", false);
    }

    public boolean getSurportDts() {
       return mSystemControlManager.getPropertyBoolean("ro.vendor.platform.support.dts", false);
    }

    public boolean getSurportDtsDrcCustom() {
       return mSystemControlManager.getPropertyBoolean("persist.vendor.sys.dtsdrccustom", false);
    }

    public boolean isDroidlogicTvFeature() {
       return mSystemControlManager.getPropertyBoolean("ro.vendor.platform.has.tvuimode", false);
    }

    
}
