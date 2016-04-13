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

import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import alexander.martinz.quickloader.tiles.TilePublisher;

public class SettingsActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .commit();
    }

    public static final class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
        private Preference mPublishTile;
        private Preference mPublishTileN;

        private Preference mPublishNotif;
        private Preference mCancelNotif;

        private Toast mToast;

        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            mPublishTile = findPreference(getString(R.string.key_publish_tile));
            if (TilePublisher.isCmSdkAvailable()) {
                mPublishTile.setOnPreferenceClickListener(this);
            } else {
                getPreferenceScreen().removePreference(mPublishTile);
            }

            mPublishTileN = findPreference(getString(R.string.key_publish_tile_n));
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                getPreferenceScreen().removePreference(mPublishTileN);
            }

            mPublishNotif = findPreference(getString(R.string.key_publish_notification));
            mPublishNotif.setOnPreferenceClickListener(this);

            mCancelNotif = findPreference(getString(R.string.key_cancel_notification));
            mCancelNotif.setOnPreferenceClickListener(this);
        }

        @Override public boolean onPreferenceClick(Preference preference) {
            if (preference == mPublishTile) {
                TilePublisher.publishCustomTile(getActivity());
                showToast(R.string.message_published_tile);
                return true;
            } else if (preference == mPublishNotif) {
                NotificationHelper.showPersistentNotification(getActivity());
                showToast(R.string.message_published_notification);
                return true;
            } else if (preference == mCancelNotif) {
                NotificationHelper.cancelPersistentNotification(getActivity());
                showToast(R.string.message_cancel_notification);
                return true;
            }
            return false;
        }

        private void showToast(@StringRes int textResId) {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(getActivity(), getString(textResId), Toast.LENGTH_SHORT);
            mToast.show();
        }
    }
}
