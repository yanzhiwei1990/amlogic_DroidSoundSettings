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
import android.media.AudioManager;
import android.app.ActivityManager;
import android.provider.Settings;
import android.content.SharedPreferences;
import android.content.ContentResolver;

import com.droidlogic.tv.soundsettings.R;

import com.droidlogic.app.SystemControlManager;
import com.droidlogic.app.OutputModeManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.SimpleTimeZone;

public class SoundParameterSettingManager {

    public static final String TAG = "SoundParameterSettingManager";

    private Resources mResources;
    private Context mContext;
    private AudioManager mAudioManager;
    private OutputModeManager mOutputModeManager;

    public SoundParameterSettingManager (Context context) {
        mContext = context;
        mResources = mContext.getResources();
        mAudioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        mOutputModeManager = ((TvSettingsActivity)context).getOutputModeManager();
    }

    private boolean CanDebug() {
        return OptionParameterManager.CanDebug();
    }

    // 0 1 ~ off on
    public int getVirtualSurroundStatus() {
        final int itemPosition =  Settings.Global.getInt(mContext.getContentResolver(),
                OutputModeManager.VIRTUAL_SURROUND, OutputModeManager.VIRTUAL_SURROUND_OFF);
        if (CanDebug()) Log.d(TAG, "getVirtualSurroundStatus = " + itemPosition);
        return itemPosition;
    }

    public int getSoundOutputStatus () {
        final int itemPosition =  Settings.Global.getInt(mContext.getContentResolver(),
                OutputModeManager.SOUND_OUTPUT_DEVICE, OutputModeManager.SOUND_OUTPUT_DEVICE_SPEAKER);
        if (CanDebug()) Log.d(TAG, "getSoundOutputStatus = " + itemPosition);
        return itemPosition;
    }

    public void setVirtualSurround (int mode) {
        if (CanDebug()) Log.d(TAG, "setVirtualSurround = " + mode);
        mOutputModeManager.setVirtualSurround(mode);
        Settings.Global.putInt(mContext.getContentResolver(), OutputModeManager.VIRTUAL_SURROUND, mode);
    }

    public void setSoundOutputStatus (int mode) {
        if (CanDebug()) Log.d(TAG, "setSoundOutputStatus = " + mode);
        mOutputModeManager.setSoundOutputStatus(mode);
        Settings.Global.putInt(mContext.getContentResolver(), OutputModeManager.SOUND_OUTPUT_DEVICE, mode);
        //need to add control in framework code
        /*Settings.Global.putInt(mContext.getContentResolver(),
                Settings.Global.HDMI_SYSTEM_AUDIO_STATUS_ENABLED,
                mode == OutputModeManager.SOUND_OUTPUT_DEVICE_ARC ? OutputModeManager.TV_ARC_ON : OutputModeManager.TV_ARC_OFF);*/
    }

    public void setDigitalAudioFormat (int mode) {
        if (CanDebug()) Log.d(TAG, "setDigitalAudioFormat = " + mode);
        switch (mode) {
            case OutputModeManager.DIGITAL_PCM:
            case OutputModeManager.DIGITAL_AUTO:
                setDigitalAudioFormatMode(mode);
                break;
            default:
                setDigitalAudioFormatMode(OutputModeManager.DIGITAL_PCM);
        }
    }

    public void setDigitalAudioFormatMode(int mode) {
        mOutputModeManager.setDigitalAudioFormatOut(mode);
        Settings.Global.putInt(mContext.getContentResolver(),
                OutputModeManager.DIGITAL_AUDIO_FORMAT, mode);
    }

    public int getDigitalAudioFormat() {
        final int value = Settings.Global.getInt(mContext.getContentResolver(),
                OutputModeManager.DIGITAL_AUDIO_FORMAT, OutputModeManager.DIGITAL_PCM);
        if (CanDebug()) Log.d(TAG, "getDigitalAudioFormat value = " + value);
        return value;
    }

    public void enableLineOutAudio(boolean mode) {
        if (CanDebug()) Log.d(TAG, "enableLineOutAudio mode = " + mode);
        mOutputModeManager.enableBoxLineOutAudio(mode);
        Settings.Global.putInt(mContext.getContentResolver(),
                OutputModeManager.BOX_LINE_OUT, mode ? OutputModeManager.BOX_LINE_OUT_ON : OutputModeManager.BOX_LINE_OUT_OFF);
    }

    public int getLineOutAudioStatus() {
        final int value = Settings.Global.getInt(mContext.getContentResolver(),
                OutputModeManager.BOX_LINE_OUT, OutputModeManager.BOX_LINE_OUT_OFF);
        if (CanDebug()) Log.d(TAG, "getLineOutAudioStatus value = " + value);
        return value;
    }

    public void enableHdmiAudio(boolean mode) {
        if (CanDebug()) Log.d(TAG, "enableHdmiAudio mode = " + mode);
        mOutputModeManager.enableBoxHdmiAudio(mode);
        Settings.Global.putInt(mContext.getContentResolver(),
                OutputModeManager.BOX_HDMI, mode ? OutputModeManager.BOX_HDMI_ON : OutputModeManager.BOX_HDMI_OFF);
    }

    public int getHdmiAudioStatus() {
        final int value = Settings.Global.getInt(mContext.getContentResolver(),
                OutputModeManager.BOX_HDMI, OutputModeManager.BOX_HDMI_ON);
        if (CanDebug()) Log.d(TAG, "getHdmiAudioStatus value = " + value);
        return value;
    }

    public void enableSpeakerAudio(boolean mode) {
        if (CanDebug()) Log.d(TAG, "enableSpeakerAudio mode = " + mode);
        mOutputModeManager.enableTvSpeakerAudio(mode);
        Settings.Global.putInt(mContext.getContentResolver(),
                OutputModeManager.TV_SPEAKER, mode ? OutputModeManager.TV_SPEAKER_ON : OutputModeManager.TV_SPEAKER_OFF);
    }

    public int getSpeakerAudioStatus() {
        final int value = Settings.Global.getInt(mContext.getContentResolver(),
                OutputModeManager.TV_SPEAKER, OutputModeManager.TV_SPEAKER_OFF);
        if (CanDebug()) Log.d(TAG, "getSpeakerAudioStatus value = " + value);
        return value;
    }

    public void enableArcAudio(boolean mode) {
        if (CanDebug()) Log.d(TAG, "enableArcAudio mode = " + mode);
        mOutputModeManager.enableTvArcAudio(mode);
        Settings.Global.putInt(mContext.getContentResolver(),
                OutputModeManager.TV_ARC, mode ? OutputModeManager.TV_ARC_ON : OutputModeManager.TV_ARC_OFF);
        //need to add control in framework code
        /*Settings.Global.putInt(mContext.getContentResolver(),
                Settings.Global.HDMI_SYSTEM_AUDIO_STATUS_ENABLED, mode ? OutputModeManager.TV_ARC_ON : OutputModeManager.TV_ARC_OFF);*/
    }

    public int getArcAudioStatus() {
        final int value = Settings.Global.getInt(mContext.getContentResolver(),
                OutputModeManager.TV_ARC, OutputModeManager.TV_ARC_OFF);
        if (CanDebug()) Log.d(TAG, "enableArcAudio value = " + value);
        return value;
    }

    public void setDrcModePassthroughSetting(int newVal) {
        Settings.Global.putInt(mContext.getContentResolver(),
                OutputModeManager.DRC_MODE, newVal);
    }

    public static boolean getSoundEffectsEnabled(ContentResolver contentResolver) {
        return Settings.System.getInt(contentResolver, Settings.System.SOUND_EFFECTS_ENABLED, 1) != 0;
    }

    public static final String DRC_OFF = "off";
    public static final String DRC_LINE = "line";
    public static final String DRC_RF = "rf";

    public String getDrcModePassthroughSetting() {
        final int value = Settings.Global.getInt(mContext.getContentResolver(),
                OutputModeManager.DRC_MODE, OutputModeManager.IS_DRC_LINE);

        switch (value) {
        case OutputModeManager.IS_DRC_OFF:
            return DRC_OFF;
        case OutputModeManager.IS_DRC_LINE:
        default:
            return DRC_LINE;
        case OutputModeManager.IS_DRC_RF:
            return DRC_RF;
        }
    }

    public void initParameterAfterBoot() {
        Log.d(TAG, "initParameterAfterBoot");
        setDigitalAudioFormat(getDigitalAudioFormat());
        mOutputModeManager.initSoundParametersAfterBoot();
    }

    public void resetParameter() {
        Log.d(TAG, "resetParameter");
        mOutputModeManager.resetSoundParameters();
    }
}

