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

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat

object NotificationHelper {
    private val NOTIFICATION_ID = 1972831
    private val REQ_CODE = 19924

    fun showPersistentNotification(context: Context) {
        val nm = NotificationManagerCompat.from(context)

        val builder = NotificationCompat.Builder(context)
        builder.setContentTitle(context.getString(R.string.app_name))
        builder.setContentText(context.getString(R.string.notification_content))
        builder.setSmallIcon(R.drawable.ic_cloud_download_white_24dp)
        builder.setOngoing(true)
        builder.setColor(ContextCompat.getColor(context, R.color.accent))
        builder.setPriority(NotificationCompat.PRIORITY_MIN)

        val intent = Intent(context, DownloadDialog::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val pendingIntent = PendingIntent.getActivity(context, REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)

        val notification = builder.build()
        nm.cancel(NOTIFICATION_ID)
        nm.notify(NOTIFICATION_ID, notification)
    }

    fun cancelPersistentNotification(context: Context) {
        val nm = NotificationManagerCompat.from(context)
        nm.cancel(NOTIFICATION_ID)
    }
}
