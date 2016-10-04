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
package alexander.martinz.quickloader.services

import alexander.martinz.quickloader.Clipboarder
import alexander.martinz.quickloader.DownloadDialog
import alexander.martinz.quickloader.R
import alexander.martinz.quickloader.common.PersistentService
import alexander.martinz.quickloader.common.getPref
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.IBinder

class ClipboardService : PersistentService() {
    companion object {
        fun start(context: Context) {
            val appContext = context.applicationContext
            val intent = Intent(appContext, ClipboardService::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            appContext.startService(intent)
        }

        fun stop(context: Context) {
            val appContext = context.applicationContext
            val intent = Intent(appContext, ClipboardService::class.java)
            appContext.stopService(intent)
        }

        fun startIfNeeded(context: Context) {
            val shouldStart = context.getPref(R.string.key_detect_clipboard_changes, false)
            if (shouldStart) {
                start(context)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).addPrimaryClipChangedListener(primaryClipChangedListener)
    }

    override fun onDestroy() {
        (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).removePrimaryClipChangedListener(primaryClipChangedListener)
        super.onDestroy()
    }

    val primaryClipChangedListener: ClipboardManager.OnPrimaryClipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
        val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val urlFromClipboard = Clipboarder.getUrlFromClipboard(clipBoardManager)
        if (urlFromClipboard == null || urlFromClipboard.isBlank()) {
            return@OnPrimaryClipChangedListener
        }

        val intent = Intent(this.applicationContext, DownloadDialog::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
