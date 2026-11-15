# XENDORA v0.1.0 — Android Receiver + Browser Sender

**Share Beyond Limits.** By Sayem Uddin Samir

## Ki kaj kore (v0.1.0)

- Android phone-e "Receiver" chalu korle ekta LAN server khule.
- Same Wi-Fi-te thaka je kono device (Android, Windows, Mac, Linux, iPhone) browser theke file pathate parbe.
- Receiver-e 6-digit one-time code thake, 2 minute por expire hoy.
- Prottek file receiver-e Accept/Reject korte hoy. Automatic accept hoy na.
- File streaming hoy, pura file RAM-e load hoy na.
- File `Download/XENDORA` folder-e save hoy. Filename sanitize hoy, executable kono kichhu automatic run hoy na.
- Server-side SHA-256 hisab kora hoy.

## APK kivabe pabben

1. GitHub-e ekta new repository banan.
2. Ei folder-er shob file repository-te upload korun (ba `git push` korun).
3. `Actions` tab-e giye "Build XENDORA APK" run-er shesh hole `XENDORA-v0.1.0-debug` artifact theke APK download korun.
4. Permanent download link-er jonno tag push korun:
   `git tag v0.1.0 && git push origin v0.1.0`
   Tarpor `Releases` page-e `app-debug.apk` pabben. Oi link-tai share korben.

## Install

1. Phone-er Settings → "Install unknown apps" theke browser ba file manager-ke allow korun.
2. APK install korun. Play Protect warning ashle "More details" → "Install anyway".
3. Minimum Android 10 (API 29) dorkar.

## Byabohar

1. Dui device same Wi-Fi-te thakuk.
2. Receiver phone-e XENDORA khulen, "Receiver chalu korun" chapun. Screen-e address ar 6-digit code dekhabe.
3. Sender device-er browser-e address likhun, jemon `http://192.168.1.25:8787`.
4. Code din, file select korun, "Pathan" chapun.
5. Receiver phone-e Accept/Reject korun.
6. File `Downloads/XENDORA` folder-e save hobe.

Ekbar file pathanor por code badle jay. Porer file-er jonno notun code lagbe.

## Security status (sotyo kotha)

**Ache:** one-time 6-digit code, 2 minute expiry, 5 bar bhul code-e 60 second lock, receiver approval, filename sanitize, path traversal block, SHA-256 hisab, adha file hole delete, streaming transfer.

**Nei, ekhono:** connection encrypted na. v0.1 plain HTTP. Eki Wi-Fi-te thaka keu traffic dekhle code ar file dekhte parbe. Public ba untrusted Wi-Fi-te byabohar korben na. Next version-e HTTPS (self-signed certificate + fingerprint pinning) ashbe.

**Browser sender** HTTP context-e SHA-256 check korte pare na (browser-er crypto API HTTP-te available na). Tai browser theke pathano file-er size milano hoy, kintu hash check hoy na. Android-er native sender-e hash check ashbe.

## Ki ekhono nai

QR code scan/generate, auto device discovery, Wi-Fi Direct ba hotspot mode, Bluetooth, internet/remote transfer (WebRTC/TURN), Windows/macOS/Linux client, iOS, multiple/folder transfer, pause/resume, background service (app open thakte hobe), in-app sender, TLS.

## Locally build (Android Studio)

Android Studio diye folder-ti khulun, `Build → Build APK(s)`. Output: `app/build/outputs/apk/debug/app-debug.apk`.

## Release signing

Release APK-te signing key ei repo-te nei, kokhono rakha uchit na. Nijer keystore banan:

`keytool -genkeypair -v -keystore xendora-release.jks -alias xendora -keyalg RSA -keysize 2048 -validity 10000`

Tarpor GitHub-er `Settings → Secrets` e keystore ar password rakhen, ar workflow-e signing config jog korben. Keystore file-ti kokhono git-e commit korben na.
