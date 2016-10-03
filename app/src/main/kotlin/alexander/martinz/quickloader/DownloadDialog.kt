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

import alexander.martinz.quickloader.tiles.TilePublisher
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.tbruyelle.rxpermissions.RxPermissions
import hugo.weaving.DebugLog

class DownloadDialog : Activity() {
    private var mDialog: AlertDialog? = null

    private var mUrl: EditText? = null
    private var mFileName: EditText? = null
    private var mAutoDetect: Switch? = null

    private var mToast: Toast? = null

    private val mUrlTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(cs: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {
            if (mAutoDetect == null || !mAutoDetect!!.isChecked) {
                return
            }

            val name = charSequence?.toString()
            val finalName = extractFilename(name)
            if (finalName != null) {
                mFileName!!.setText(finalName)
            }
        }

        override fun afterTextChanged(editable: Editable) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @SuppressLint("InflateParams") val v = layoutInflater.inflate(R.layout.dialog_download, null, false)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        mUrl = v.findViewById(R.id.et_download_url) as EditText
        mUrl?.addTextChangedListener(mUrlTextWatcher)
        mFileName = v.findViewById(R.id.et_download_file_name) as EditText

        mAutoDetect = v.findViewById(R.id.switch_auto_detect) as Switch
        mAutoDetect?.setOnCheckedChangeListener { compoundButton, checked ->
            mFileName?.isEnabled = !checked
            if (mUrl != null) {
                val urlEditable = mUrl?.text
                val url = urlEditable?.toString()
                if (!TextUtils.isEmpty(url)) {
                    mUrlTextWatcher.onTextChanged(url, 0, 0, url?.length ?: 0)
                }
            }
            preferences!!.edit().putBoolean(getString(R.string.key_auto_detect), checked).apply()
        }
        mAutoDetect?.isChecked = preferences.getBoolean(getString(R.string.key_auto_detect), true)

        val autoExtract = v.findViewById(R.id.switch_auto_extract) as Switch
        autoExtract.isChecked = preferences.getBoolean(getString(R.string.key_auto_extract), true)
        autoExtract.setOnCheckedChangeListener { compoundButton, checked ->
            if (checked) {
                extractUrlFromClipboard()
            }

            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(getString(R.string.key_auto_extract), checked).apply()
        }

        val tvAutoDetect = v.findViewById(R.id.tv_auto_detect) as TextView
        tvAutoDetect.setOnClickListener { mAutoDetect?.toggle() }

        val startDownload = v.findViewById(R.id.start_download) as Button
        startDownload.setOnClickListener(View.OnClickListener {
            if (!checkStoragePermission()) {
                return@OnClickListener
            }

            showDownloadDialog()
        })

        val configure = v.findViewById(R.id.configure) as Button
        configure.setOnClickListener {
            val intent = Intent(this@DownloadDialog, SettingsActivity::class.java)
            startActivity(intent)
        }

        val builder = AlertDialog.Builder(this)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setTitle(R.string.download)
        builder.setCancelable(true)
        builder.setView(v)

        mDialog?.dismiss()
        mDialog = builder.create()
        mDialog?.setOnDismissListener { finish() }
        mDialog?.show()

        AsyncTask.execute { TilePublisher.publishCustomTile(this@DownloadDialog) }
    }

    override fun onResume() {
        super.onResume()

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        // check if the user has already copied any url and insert it
        val shouldAutoExtract = preferences.getBoolean(getString(R.string.key_auto_extract), true)
        if (shouldAutoExtract && mUrl != null) {
            extractUrlFromClipboard()
        }
    }

    private fun checkStoragePermission(): Boolean {
        val rxPermissions = RxPermissions.getInstance(this@DownloadDialog)
        if (!rxPermissions.isGranted(Manifest.permission.READ_EXTERNAL_STORAGE) || !rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe { granted ->
                if (granted!!) {
                    showDownloadDialog()
                } else {
                    showToast(getString(R.string.toast_permission_grant), true)

                    val settingsUri = Uri.parse(String.format("package:%s", packageName))
                    val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, settingsUri)
                    i.addCategory(Intent.CATEGORY_DEFAULT)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    try {
                        startActivity(i)
                    } catch (ignored: Exception) {
                    }

                }
            }
            return false
        }
        return true
    }

    private fun showDownloadDialog() {
        val url = getText(mUrl)
        if (url == null || TextUtils.isEmpty(url)) {
            showToast(getString(R.string.please_enter_url), false)
            return
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            showToast(getString(R.string.url_start_with_http), false)
            return
        }

        val name = getText(mFileName)
        if (TextUtils.isEmpty(name)) {
            showToast(getString(R.string.filename_must_not_be_empty), false)
            return
        }

        val fileName = name?.replace(" ", "_") ?: ""

        val builder = AlertDialog.Builder(this@DownloadDialog)
        builder
                .setTitle(R.string.verify_url_title)
                .setMessage(getString(R.string.verify_url_message, url))
                .setNegativeButton(android.R.string.cancel) { dialogInterface, i -> dialogInterface.dismiss() }
                .setPositiveButton(android.R.string.ok) { dialogInterface, i ->
                    startDownload(url, fileName, mDialog)
                    dialogInterface.dismiss()
                }

        val dialog = builder.create()
        dialog.show()
    }

    private fun extractUrlFromClipboard(): Boolean {
        val urlFromClipboard = urlFromClipboard
        if (urlFromClipboard != null && !TextUtils.isEmpty(urlFromClipboard)) {
            mUrl?.setText(urlFromClipboard)
            mUrlTextWatcher.onTextChanged(urlFromClipboard, 0, 0, urlFromClipboard.length)
            return true
        }
        return false
    }

    private fun startDownload(url: String, fileName: String, alertDialog: AlertDialog?) {
        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle(fileName)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setVisibleInDownloadsUi(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .allowScanningByMediaScanner()

        dm.enqueue(request)

        alertDialog?.dismiss()
        showToast(getString(R.string.started_download_toast, fileName), true)
    }

    private fun showToast(text: String, isLong: Boolean) {
        showToast(text, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
    }

    private fun showToast(text: String, length: Int) {
        mToast?.cancel()
        mToast = Toast.makeText(this, text, length)
        mToast?.show()
    }

    private fun getText(editText: EditText?): String? {
        if (editText == null || editText.text == null) {
            return null
        }

        val content = editText.text.toString()
        return if (TextUtils.isEmpty(content)) null else content.trim { it <= ' ' }
    }

    private val urlFromClipboard: String?
        @DebugLog get() {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboardManager.hasPrimaryClip()) {
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
            }
            return null
        }

    companion object {
        fun createIntent(context: Context): Intent {
            val intent = Intent(context, DownloadDialog::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            return intent
        }

        @DebugLog fun extractFilename(name: String?): String? {
            if (name == null) {
                return null
            }

            var finalName = name.trim { it <= ' ' }
            if (TextUtils.isEmpty(finalName)) {
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

}
