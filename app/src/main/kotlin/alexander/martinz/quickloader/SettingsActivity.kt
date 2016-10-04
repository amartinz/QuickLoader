/*
 * Copyright 2015 - 2016 Alexander Martinz
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

package alexander.martinz.quickloader

import alexander.martinz.quickloader.services.ClipboardService
import alexander.martinz.quickloader.tiles.TilePublisher
import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        fragmentManager.beginTransaction().replace(R.id.fragment_container, SettingsFragment()).commit()

        ClipboardService.startIfNeeded(this)
    }

    class SettingsFragment : PreferenceFragment(), Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private var mOpenQuickLoader: Preference? = null

        private var mPublishTile: Preference? = null
        private var mPublishTileN: Preference? = null

        private var mPublishNotif: Preference? = null
        private var mCancelNotif: Preference? = null

        private var mClipboardDetectChanges: SwitchPreference? = null

        private var mToast: Toast? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)

            mOpenQuickLoader = findPreference(getString(R.string.key_open_quickloader))
            mOpenQuickLoader?.onPreferenceClickListener = this

            mPublishTile = findPreference(getString(R.string.key_publish_tile))
            if (TilePublisher.isCmSdkAvailable) {
                mPublishTile?.onPreferenceClickListener = this
            } else {
                preferenceScreen.removePreference(mPublishTile)
            }

            mPublishTileN = findPreference(getString(R.string.key_publish_tile_n))
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                preferenceScreen.removePreference(mPublishTileN)
            }

            mPublishNotif = findPreference(getString(R.string.key_publish_notification))
            mPublishNotif?.onPreferenceClickListener = this

            mCancelNotif = findPreference(getString(R.string.key_cancel_notification))
            mCancelNotif?.onPreferenceClickListener = this

            mClipboardDetectChanges = findPreference(getString(R.string.key_detect_clipboard_changes)) as SwitchPreference
            mClipboardDetectChanges?.onPreferenceChangeListener = this
        }

        override fun onPreferenceClick(preference: Preference): Boolean {
            if (preference === mOpenQuickLoader) {
                val context = activity.applicationContext
                val intent = DownloadDialog.createIntent(context)
                startActivity(intent)
                return true
            } else if (preference === mPublishTile) {
                TilePublisher.publishCustomTile(activity)
                showToast(R.string.message_published_tile)
                return true
            } else if (preference === mPublishNotif) {
                NotificationHelper.showPersistentNotification(activity)
                showToast(R.string.message_published_notification)
                return true
            } else if (preference === mCancelNotif) {
                NotificationHelper.cancelPersistentNotification(activity)
                showToast(R.string.message_cancel_notification)
                return true
            }
            return false
        }

        override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
            if (preference == mClipboardDetectChanges) {
                val enabled = newValue as Boolean
                if (enabled) {
                    ClipboardService.start(context)
                } else {
                    ClipboardService.stop(context)
                }
            }
            return true
        }

        private fun showToast(@StringRes textResId: Int) {
            mToast?.cancel()
            mToast = Toast.makeText(activity, getString(textResId), Toast.LENGTH_SHORT)
            mToast?.show()
        }
    }
}
