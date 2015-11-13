/*
 * Copyright 2015 Alexander Martinz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package alexander.martinz.quickloader;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.StringRes;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {
    private Preference mPublishTile;

    private Preference mPublishNotif;
    private Preference mCancelNotif;

    private Toast mToast;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        mPublishTile = findPreference(getString(R.string.key_publish_tile));
        if (CompatHelper.isCmSdkAvailable()) {
            mPublishTile.setOnPreferenceClickListener(this);
        } else {
            getPreferenceScreen().removePreference(mPublishTile);
        }

        mPublishNotif = findPreference(getString(R.string.key_publish_notification));
        mPublishNotif.setOnPreferenceClickListener(this);

        mCancelNotif = findPreference(getString(R.string.key_cancel_notification));
        mCancelNotif.setOnPreferenceClickListener(this);
    }

    @Override public boolean onPreferenceClick(Preference preference) {
        if (preference == mPublishTile) {
            CompatHelper.publishCustomTile(this);
            showToast(R.string.message_published_tile);
            return true;
        } else if (preference == mPublishNotif) {
            NotificationHelper.showPersistentNotification(this);
            showToast(R.string.message_published_notification);
            return true;
        } else if (preference == mCancelNotif) {
            NotificationHelper.cancelPersistentNotification(this);
            showToast(R.string.message_cancel_notification);
            return true;
        }
        return false;
    }

    private void showToast(@StringRes int textResId) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, getString(textResId), Toast.LENGTH_SHORT);
        mToast.show();
    }
}
