# Shadowplay RP Launcher — Phone Build

This project is prepared for building on Android with a mobile Android IDE.

## Recommended workflow

1. Extract this ZIP to internal storage.
2. Open the extracted **ShadowplayRP_Launcher_Final** folder as an Android/Gradle project in your phone IDE.
3. Allow the IDE to install/configure the Android SDK components it requests.
4. Use **JDK 17** for the Gradle/Android build.
5. Sync the project.
6. Build `:app:assembleDebug`.
7. Install `app/build/outputs/apk/debug/app-debug.apk`.

## If the IDE asks for Gradle

Use the Gradle version declared by `gradle/wrapper/gradle-wrapper.properties` (8.7). Do not randomly change the Android Gradle Plugin version.

The included `build-phone.sh` can also run the debug build when a `gradle` command is available in the phone terminal.

## Android SDK

The project targets Android API 35 and supports Android 7.0/API 24 and newer.

If the IDE reports a missing SDK package, install the exact package it names. The launcher does not require any third-party native libraries.

## Important

This is the launcher source project. The final Shadowplay game client must be installed separately. The launcher passes these Intent extras to the selected game package:

- `shadowplay_server_key`
- `shadowplay_server_name`
- `shadowplay_server_host`
- `shadowplay_server_port`
- `shadowplay_server_color`
- `shadowplay_server_x2`
- `shadowplay_launcher_version`

The native Shadowplay client must read these values and use the selected host/port. The launcher alone cannot change a hard-coded native connection address.
