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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;
import cyanogenmod.os.Build;
import hugo.weaving.DebugLog;

public class CompatHelper {
    private static final int CUSTOM_TILE_ID = 197283;

    @DebugLog public static boolean publishCustomTile(final Context context) {
        if (Build.CM_VERSION.SDK_INT < Build.CM_VERSION_CODES.APRICOT) {
            return false;
        }

        final Intent tileIntent = new Intent(context, DownloadDialog.class);
        tileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final PendingIntent pendingTileIntent = PendingIntent.getActivity(context, 0,
                tileIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final CustomTile customTile = new CustomTile.Builder(context)
                .setOnClickIntent(pendingTileIntent)
                .setContentDescription(R.string.app_name)
                .setLabel(R.string.app_name)
                .setIcon(R.mipmap.ic_launcher)
                .hasSensitiveData(false)
                .shouldCollapsePanel(true)
                .build();

        CMStatusBarManager.getInstance(context).publishTile(CUSTOM_TILE_ID, customTile);
        return true;
    }
}
