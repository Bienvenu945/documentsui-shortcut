# DocumentsUI Shortcut

[![License: Apache-2.0](https://img.shields.io/badge/License-Apache--2.0-blue.svg)](LICENSE)

DocumentsUI Shortcut is a tiny Android launcher that opens the system Files / DocumentsUI app.

It is only a shortcut. It does not request any permission, read files, access the network, keep a background service running, or implement a file manager of its own.

## Features

- Opens Android's built-in Files / DocumentsUI app, preferring browser mode over file picker mode.
- Long-press shortcuts to **Download**, **Documents**, **DCIM** and **Android/data**. If the device's DocumentsUI cannot open a folder directly, it falls back to the storage root.
- Quick Settings tile (the system binds it only while the panel is open).
- Original vector icon with Android 13+ themed icon support.
- Requires Android 8.0 or later. Application ID: `com.documentsui.shortcut`

## Android/data and Android/obb

Android 11 and later restrict ordinary apps from accessing `Android/data` and `Android/obb` through the system file picker. This app only launches the system DocumentsUI app; whether those directories are visible depends on Android and the device vendor's DocumentsUI implementation.

## Customize

- **App name:** `app/src/main/res/values/strings.xml` (`app_name`, plus the translations in `values-xx/`).
- **Colors:** `app/src/main/res/values/colors.xml` (`brand_start`, `brand_end`) restyles the icon and shortcut icons.
- **Shortcut folders:** edit `app/src/main/res/xml/shortcuts.xml` and the `FOLDERS` whitelist in `MainActivity.java`. Launchers usually show 4 static shortcuts.

## Build

Requirements:

- JDK 17
- Android SDK with platform 36 (set `ANDROID_HOME`, or `sdk.dir` in `local.properties`)
- Internet access on the first build (Gradle 8.14 and the Android Gradle Plugin are downloaded)

```bash
chmod +x gradlew          # Linux / macOS, if needed
./gradlew assembleDebug   # installable debug APK
./gradlew assembleRelease # unsigned release APK
```

On Windows use `.\gradlew.bat` instead of `./gradlew`.

### Build online (no local setup)

Push this project to a GitHub repository (files at the repository root). The workflow in `.github/workflows/build.yml` builds the debug APK on GitHub's servers: open the **Actions** tab, pick the latest run (or start it with **Run workflow**) and download the `DocumentsUI-Shortcut-debug` artifact.

Outputs:

```text
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release-unsigned.apk
```

Sign the release APK with your own key:

```bash
zipalign -p 4 app-release-unsigned.apk aligned.apk
apksigner sign --ks my-key.jks aligned.apk
```

## Release

Use your own signing key for GitHub releases or F-Droid builds. Do not commit `keystore/`, `local.properties`, `build/`, generated APKs, or signing artifacts.

```bash
git tag v1.1.0
git push origin main --tags
```

## Icon

The icon, shortcut icons and tile icon are original vector drawables. See [NOTICE](NOTICE) for the history of the previous AOSP-derived icon.

## License

This project is licensed under the [Apache License 2.0](LICENSE).
