QuickLoader
=====

This is a sample app to demonstrate how to implement an app as candidate for "Power Button Actions"
in NamelessRom.

Power Button Actions allow the user to choose a custom action, when he/she/it presses
"Power + Volume up" or "Power + Volume down"

In our example (QuickLoader) we will start an activity, which displays an dialog, to enter an URL
and optionally a file name and download the file to the device.

Usecase
---

You are browsing the internet with your favourite browser, eg [Chrome Beta][1] and see a
funny cat video.
The video opens in a new tab but there is no option to download.

Now QuickLoader aids you! Just copy the URL, press "Power + Volume down / up", paste the URL and
press "Download".

Really easy and open sourcy, right?

Implementation
---

- You need to add the following permission to your manifest

```xml
    <uses-permission android:name="android.permission.LAUNCH_WITH_POWER_CHORD" />
```

- Then you will need to add the following intent filter to your activity

```xml
    <intent-filter>
        <action android:name="android.intent.action.ACTION_POWER_CHORD" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
```

License
---

    Copyright 2015 Alexander Martinz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

---

[1]: https://play.google.com/store/apps/details?id=com.chrome.beta
