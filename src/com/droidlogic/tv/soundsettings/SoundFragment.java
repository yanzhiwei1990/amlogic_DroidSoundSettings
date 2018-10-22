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

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.text.TextUtils;
import android.util.Log;

import com.droidlogic.app.OutputModeManager;

import com.droidlogic.tv.soundsettings.R;

public class SoundFragment extends LeanbackPreferenceFragment implements Preference.OnPreferenceChangeListener {
    public static final String TAG = "SoundFragment";
    private static final String KEY_DRCMODE_PASSTHROUGH = "drc_mode";
    private static final String KEY_DIGITALSOUND_PASSTHROUGH = "digital_sound";
    private static final String KEY_DTSDRCMODE_PASSTHROUGH = "dtsdrc_mode";
    private static final String KEY_DTSDRCCUSTOMMODE_PASSTHROUGH = "dtsdrc_custom_mode";

    //audio out switch
    private static final String KEY_BOX_LINEOUT = "box_lineout";
    private static final String KEY_BOX_HDMI = "box_hdmi";
    private static final String KEY_TV_SPEAKER = "tv_speaker";
    private static final String KEY_TV_ARC = "tv_arc";

    private OutputModeManager mOutputModeManager = null;
    private SoundParameterSettingManager mSoundParameterSettingManager = null;
    private OptionParameterManager mOptionParameterManager = null;

    public static SoundFragment newInstance() {
        return new SoundFragment();
    }

    private boolean CanDebug() {
        return OptionParameterManager.CanDebug();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        mOutputModeManager = ((TvSettingsActivity)getActivity()).getOutputModeManager();
        mSoundParameterSettingManager = ((TvSettingsActivity)getActivity()).getSoundParameterSettingManager();
		mOptionParameterManager = ((TvSettingsActivity)getActivity()).getOptionParameterManager();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.sound, null);

        final ListPreference drcmodePref = (ListPreference) findPreference(KEY_DRCMODE_PASSTHROUGH);
        final ListPreference digitalsoundPref = (ListPreference) findPreference(KEY_DIGITALSOUND_PASSTHROUGH);
        final ListPreference dtsdrccustommodePref = (ListPreference) findPreference(KEY_DTSDRCCUSTOMMODE_PASSTHROUGH);
        final ListPreference dtsdrcmodePref = (ListPreference) findPreference(KEY_DTSDRCMODE_PASSTHROUGH);

        //audio out switch
        final ListPreference boxlineout = (ListPreference) findPreference(KEY_BOX_LINEOUT);
        final ListPreference boxhdmi= (ListPreference) findPreference(KEY_BOX_HDMI);
        final ListPreference tvspeaker = (ListPreference) findPreference(KEY_TV_SPEAKER);
        final ListPreference tvarc = (ListPreference) findPreference(KEY_TV_ARC);

        drcmodePref.setValue(mSoundParameterSettingManager.getDrcModePassthroughSetting());
        drcmodePref.setOnPreferenceChangeListener(this);
        digitalsoundPref.setValueIndex(mSoundParameterSettingManager.getDigitalAudioFormat());
        digitalsoundPref.setOnPreferenceChangeListener(this);
        dtsdrcmodePref.setValue(mOptionParameterManager.getDtsDrcScale());
        dtsdrcmodePref.setOnPreferenceChangeListener(this);
        boolean tvFlag = mOptionParameterManager.isDroidlogicTvFeature();
        if (!mOptionParameterManager.getSurportDolby()) {
            drcmodePref.setVisible(false);
            Log.d(TAG, "platform doesn't support dolby");
        }
        if (!mOptionParameterManager.getSurportDts()) {
            dtsdrcmodePref.setVisible(false);
            dtsdrccustommodePref.setVisible(false);
            Log.d(TAG, "platform doesn't support dts");
        } else if (mOptionParameterManager.getSurportDtsDrcCustom()) {
            dtsdrcmodePref.setVisible(false);
        } else {
            dtsdrccustommodePref.setVisible(false);
        }
        if (tvFlag) {
            boxlineout.setVisible(false);
            boxhdmi.setVisible(false);
            tvspeaker.setValueIndex(mSoundParameterSettingManager.getSpeakerAudioStatus());
            tvspeaker.setOnPreferenceChangeListener(this);
            tvspeaker.setVisible(false);//tv hide this as setting conflict
            tvarc.setValueIndex(mSoundParameterSettingManager.getArcAudioStatus());
            tvarc.setOnPreferenceChangeListener(this);
            tvarc.setVisible(false);//tv hide this as setting conflict
        } else {
            tvspeaker.setVisible(false);
            tvarc.setVisible(false);
            boxlineout.setValueIndex(mSoundParameterSettingManager.getLineOutAudioStatus());
            boxlineout.setOnPreferenceChangeListener(this);
            boxhdmi.setValueIndex(mSoundParameterSettingManager.getHdmiAudioStatus());
            boxhdmi.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (TextUtils.equals(preference.getKey(), KEY_DRCMODE_PASSTHROUGH)) {
            final String selection = (String) newValue;
            switch (selection) {
            case SoundParameterSettingManager.DRC_OFF:
                mOutputModeManager.enableDobly_DRC(false);
                mOutputModeManager.setDoblyMode(OutputModeManager.LINE_DRCMODE);
                mSoundParameterSettingManager.setDrcModePassthroughSetting(OutputModeManager.IS_DRC_OFF);
                break;
            case SoundParameterSettingManager.DRC_LINE:
                mOutputModeManager.enableDobly_DRC(true);
                mOutputModeManager.setDoblyMode(OutputModeManager.LINE_DRCMODE);
                mSoundParameterSettingManager.setDrcModePassthroughSetting(OutputModeManager.IS_DRC_LINE);
                break;
            case SoundParameterSettingManager.DRC_RF:
                mOutputModeManager.enableDobly_DRC(false);
                mOutputModeManager.setDoblyMode(OutputModeManager.RF_DRCMODE);
                mSoundParameterSettingManager.setDrcModePassthroughSetting(OutputModeManager.IS_DRC_RF);
                break;
            default:
                throw new IllegalArgumentException("Unknown drc mode pref value");
            }
            return true;
        } else if (TextUtils.equals(preference.getKey(), KEY_DIGITALSOUND_PASSTHROUGH)) {
            final int selection = Integer.parseInt((String)newValue);
            switch (selection) {
            case OutputModeManager.DIGITAL_PCM:
            case OutputModeManager.DIGITAL_AUTO:
                mSoundParameterSettingManager.setDigitalAudioFormat(selection);
                break;
            default:
                throw new IllegalArgumentException("Unknown digital audio format value");
            }
            return true;
        } else if (TextUtils.equals(preference.getKey(), KEY_DTSDRCMODE_PASSTHROUGH)) {
            final String selection = (String) newValue;
            mOutputModeManager.setDtsDrcScale(selection);
            return true;
        } else if (TextUtils.equals(preference.getKey(), KEY_BOX_LINEOUT)) {
            final int selection = Integer.parseInt((String)newValue);
            mSoundParameterSettingManager.enableLineOutAudio(selection == OutputModeManager.BOX_LINE_OUT_ON);
        } else if (TextUtils.equals(preference.getKey(), KEY_BOX_HDMI)) {
            final int selection = Integer.parseInt((String)newValue);
            mSoundParameterSettingManager.enableHdmiAudio(selection == OutputModeManager.BOX_HDMI_ON);
        } else if (TextUtils.equals(preference.getKey(), KEY_TV_SPEAKER)) {
            final int selection = Integer.parseInt((String)newValue);
            mSoundParameterSettingManager.enableSpeakerAudio(selection == OutputModeManager.TV_SPEAKER_ON);
        } else if (TextUtils.equals(preference.getKey(), KEY_TV_ARC)) {
            final int selection = Integer.parseInt((String)newValue);
            mSoundParameterSettingManager.enableArcAudio(selection == OutputModeManager.TV_ARC_ON);
        }
        return true;
    }
}
