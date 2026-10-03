# Shadowplay RP Launcher — Final clean launcher foundation

This is a clean, original Android launcher project made for Shadowplay RP. It uses the supplied Shadowplay artwork as the launcher background and does **not** bundle or rebrand proprietary Black Russia native libraries/code from the reference APK.

## What is included
- Shadowplay RP branding
- Mobile-friendly server selector
- Local `assets/servers.json` fallback
- Optional remote HTTPS server JSON
- online/max/status/color/x2 fields
- Persistent selected-server setting
- News screen
- Update-ready screen
- Settings screen
- Game package setting
- Launcher → game Intent extras
- Safe fallback when remote server JSON fails
- Plain Java Android Views with no extra UI libraries, making it easier to build on a phone

## Server JSON
```json
{
  "version": 1,
  "defaultServer": "main",
  "servers": [
    {
      "key": "main",
      "name": "Shadowplay RP",
      "host": "your.server.host",
      "port": 7777,
      "online": 123,
      "max": 1000,
      "color": "#A855F7",
      "x2": false,
      "status": "online"
    }
  ]
}
```

## Build on phone
Use a Gradle-compatible Android IDE with JDK 17 and Android SDK 35. Open this folder and build the `app` module. Android Gradle Plugin 8.6.1 and Gradle 8.7 are configured.

## Important game integration
The launcher sends these extras to the selected game's launch Activity:
- `shadowplay_server_key`
- `shadowplay_server_name`
- `shadowplay_server_host`
- `shadowplay_server_port`
- `shadowplay_server_color`
- `shadowplay_server_x2`
- `shadowplay_launcher_version`

The launcher cannot make a native client change its connection address by itself. The Shadowplay client must read these extras and pass the host/port into its native networking layer. The current reference JNI had a hard-coded connection, so that client-side part still needs to be changed separately.

Default game package: `com.shadowplay.game`. Change it in Settings when the final Shadowplay client package is known.


## Phone build

See `PHONE_BUILD.md`. This project was cleaned for mobile Gradle/Android IDE workflows and includes `build-phone.sh`.
