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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class BootupReceiver extends BroadcastReceiver {
    public BootupReceiver() { }

    @Override public void onReceive(final Context context, Intent intent) {
        AsyncTask.execute(new Runnable() {
            @Override public void run() {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                if (prefs.getBoolean(context.getString(R.string.key_publish_notification_bootup), false)) {
                    NotificationHelper.showPersistentNotification(context);
                }

                CompatHelper.publishCustomTile(context);
            }
        });
    }
}
