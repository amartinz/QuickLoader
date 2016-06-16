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
package alexander.martinz.quickloader.tiles;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import alexander.martinz.quickloader.DownloadDialog;
import alexander.martinz.quickloader.R;
import alexander.martinz.quickloader.SettingsActivity;
import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;
import cyanogenmod.os.Build;
import hugo.weaving.DebugLog;

public class TilePublisher {
    private static final int CUSTOM_TILE_ID = 19283;

    public static boolean isCmSdkAvailable() {
        // Android N offers custom tiles via another approach
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return false;
        }
        return (Build.CM_VERSION.SDK_INT >= Build.CM_VERSION_CODES.APRICOT);
    }

    @DebugLog public static boolean publishCustomTile(Context context) {
        if (!isCmSdkAvailable()) {
            return false;
        }
        context = context.getApplicationContext();

        final Intent tileIntent = new Intent(context, DownloadDialog.class);
        tileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final PendingIntent pendingTileIntent =
                PendingIntent.getActivity(context, 0, tileIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final Intent settingsIntent = new Intent(context, SettingsActivity.class);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final CustomTile customTile = new CustomTile.Builder(context)
                .setOnClickIntent(pendingTileIntent)
                .setOnSettingsClickIntent(settingsIntent)
                .setLabel(R.string.app_name)
                .setContentDescription(R.string.tile_content_description)
                .setIcon(R.drawable.ic_cloud_download_white_24dp)
                .hasSensitiveData(false)
                .shouldCollapsePanel(true)
                .build();

        CMStatusBarManager.getInstance(context).publishTile(CUSTOM_TILE_ID, customTile);
        return true;
    }
}
