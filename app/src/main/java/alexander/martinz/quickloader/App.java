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
package alexander.martinz.quickloader;

import android.app.Application;

import at.amartinz.universaldebug.UniversalDebug;
import at.amartinz.universaldebug.fabric.FabricConfig;

public class App extends Application {
    @Override public void onCreate() {
        super.onCreate();

        final UniversalDebug universalDebug = new UniversalDebug(this)
                .withDebug(BuildConfig.DEBUG)
                .withTimber(false);

        // disable when developing
        if (!BuildConfig.DEBUG) {
            final FabricConfig fabricConfig = new FabricConfig(universalDebug)
                    .withAnswers()
                    .withCrashlytics();
            universalDebug.withExtension(fabricConfig);
        }

        universalDebug.install();
    }
}
