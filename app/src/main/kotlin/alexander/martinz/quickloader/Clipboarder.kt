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

import android.content.ClipboardManager
import android.text.TextUtils
import hugo.weaving.DebugLog

object Clipboarder {
    @DebugLog fun getUrlFromClipboard(clipboardManager: ClipboardManager): String? {
        if (!clipboardManager.hasPrimaryClip()) {
            return null
        }

        val clipData = clipboardManager.primaryClip
        if (clipData.itemCount > 0) {
            val item = clipData.getItemAt(0)
            val clipCharSequence = item.text
            var clipText: String? = clipCharSequence?.toString()

            if (clipText != null) {
                clipText = clipText.trim { it <= ' ' }
                if (!TextUtils.isEmpty(clipText)) {
                    if (clipText.startsWith("http")) {
                        return clipText
                    }
                }
            }
        }
        return null
    }

    @DebugLog fun extractFilename(name: String?): String? {
        if (name == null) {
            return null
        }

        var finalName = name.trim { it <= ' ' }
        if (finalName.isBlank()) {
            return null
        }

        // split the url and get the last part, which should be the filename
        // example https://example.com/images/new/random_image.png -> random_image.png
        val parts = finalName.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (parts.size < 2) {
            return null
        }

        finalName = parts[parts.size - 1]

        // if the last part of the url contains a question mark (?) there is most likely the case
        // that a query is attached to the file name, lets get rid of it
        val index = finalName.indexOf("?")
        if (index != -1) {
            // random_image.png?cdn_timestamp=1058239&session=92990 -> random_image.png
            finalName = finalName.substring(0, index)
        }

        // let's trim it a last time and it should be good to go
        return finalName.trim { it <= ' ' }
    }
}
