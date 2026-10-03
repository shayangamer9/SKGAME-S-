#!/system/bin/sh
set -e
cd "$(dirname "$0")"

if command -v gradle >/dev/null 2>&1; then
  GRADLE=gradle
elif [ -x "./gradlew" ]; then
  GRADLE=./gradlew
else
  echo "Gradle was not found. Open this project in AndroidIDE/Code on the Go and let it configure Gradle."
  exit 1
fi

"$GRADLE" --version
"$GRADLE" :app:assembleDebug

echo "APK: app/build/outputs/apk/debug/app-debug.apk"
