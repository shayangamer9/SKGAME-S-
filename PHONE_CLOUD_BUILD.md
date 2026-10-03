# Build Shadowplay RP Launcher from your phone

This project is prepared for a GitHub Actions cloud build, so you do **not** need Android Studio, AndroidIDE, or a laptop.

## Phone steps

1. Create/sign in to a GitHub account.
2. Create a new repository, for example `ShadowplayRP-Launcher`.
3. Upload the **contents** of this project into the repository. Make sure `.github/workflows/build.yml` is included.
4. Open the repository's **Actions** tab.
5. Select **Build Shadowplay RP Launcher**.
6. Tap **Run workflow**.
7. Wait for the build to finish.
8. Open the completed workflow run and download the artifact named **Shadowplay-RP-Launcher-debug**.
9. Extract the artifact and install `app-debug.apk`.

The workflow installs Java 17, Gradle 8.7, Android API 35/build tools, and builds the debug APK in GitHub's cloud runner.

## Important

This builds the launcher APK only. The Shadowplay native game client must still be updated to consume the selected server host/port passed by the launcher. The launcher cannot change a hard-coded native connection by itself.
