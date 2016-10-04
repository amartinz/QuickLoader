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
package alexander.martinz.quickloader.common

import android.content.Context
import android.preference.PreferenceManager

fun Context.getPref(keyId: Int, defaultValue: String = ""): String {
    val key = getString(keyId)
    return PreferenceManager.getDefaultSharedPreferences(this).getString(key, defaultValue)
}

fun Context.setPref(keyId: Int, value: String?) {
    val key = getString(keyId)
    PreferenceManager.getDefaultSharedPreferences(this).edit().putString(key, value).apply()
}

fun Context.getPref(keyId: Int, defaultValue: Boolean = false): Boolean {
    val key = getString(keyId)
    return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(key, defaultValue)
}

fun Context.setPref(keyId: Int, value: Boolean) {
    val key = getString(keyId)
    PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(key, value).apply()
}

fun Context.getPref(keyId: Int, defaultValue: Int = -1): Int {
    val key = getString(keyId)
    return PreferenceManager.getDefaultSharedPreferences(this).getInt(key, defaultValue)
}

fun Context.setPref(keyId: Int, value: Int) {
    val key = getString(keyId)
    PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(key, value).apply()
}

object Contexter {

}
