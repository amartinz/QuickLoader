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
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {
    private Preference mPublishTile;

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
    }

    @Override public boolean onPreferenceClick(Preference preference) {
        if (preference == mPublishTile) {
            CompatHelper.publishCustomTile(this);

            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(this, getString(R.string.message_published_tile), Toast.LENGTH_SHORT);
            mToast.show();
            return true;
        }
        return false;
    }
}
