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
package alexander.martinz.quickloader.tiles;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.TileService;

import alexander.martinz.quickloader.DownloadDialog;

@TargetApi(Build.VERSION_CODES.N)
public class AwesomeTileService extends TileService {
    public AwesomeTileService() { }

    @Override public void onClick() {
        final Intent tileIntent = new Intent(this, DownloadDialog.class);
        tileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityAndCollapse(tileIntent);
    }
}
