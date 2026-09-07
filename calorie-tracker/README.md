# Calorie Tracker (Android)

A minimal native Android calorie/macro tracker. No account, no ads, no internet required.
You type the food, pick from an embedded database, and it tracks calories/macros/water with
a 7-day history.

## What it does

- **Setup interview** — asks your age, sex, height, weight, goal, activity, pace, eating
  style, then computes your plan (BMR via Mifflin-St Jeor, TDEE, calorie target, macros,
  water) and saves it.
- **Dashboard** — calorie ring, 3 macro rings, water tracker, 7-day bar chart, food diary.
- **Food logging** — search the embedded food database (~60 common foods with USDA-style
  values), pick a meal, add.
- **Backup / restore** — copy a save code, paste it back later.
- **Light/dark mode** — follows the system setting.

> This app is an offline, local-only tool. It does **not** do AI photo/vision food
> recognition — that needs a backend + API key, which is out of scope here. You log food by
> selecting from the built-in list.

## Requirements to build the APK

- [Android Studio](https://developer.android.com/studio) (Hedgehog or newer, Ladybug recommended)
- Android SDK Platform 34 (Android Studio installs it)
- JDK 17 (bundled with Android Studio)

## Build steps

1. **Open the project** in Android Studio:
   - File → Open → select the `calorie-tracker` folder.
   - Android Studio detects it's a Gradle project and offers to use the Gradle wrapper.
     Accept. (It will download Gradle 8.6 on first sync.)
2. **Let it sync** — wait for "Gradle sync finished" (first sync downloads dependencies,
   can take a few minutes).
3. **Connect a phone** with USB debugging enabled, **or** create an emulator (Device
   Manager → Create device).
4. **Run** → select your device. Android Studio builds and installs the debug APK.

## Getting the raw APK file

To get the actual `.apk` file to share or sideload:

- After a successful build, the APK is at:
  `app/build/outputs/apk/debug/app-debug.apk`
- Or: **Build → Build APK(s) → Build APK**, then click **locate** in the bottom-right
  notification to reveal it in the file manager.

### Sideloading on a phone

The debug APK is signed with the debug key. To install it on a real phone:

1. Copy `app-debug.apk` to the phone (USB, email, or cloud drive).
2. On the phone, allow "Install unknown apps" for your file manager/browser.
3. Open the APK and install. Android may warn it's signed with a debug certificate —
   that's expected for a locally-built app.

For a release-ready APK (proper signing, smaller, optimized), see "Release build" below.

## Release build

For a properly signed, smaller, installable APK:

- **Easiest: the CI workflow already does it.** Push to GitHub and the "Build APK"
  workflow generates a signing keystore and uploads both `app-debug-apk` and the signed
  `app-release-apk` as artifacts. Download the release one to sideload.
- **Locally:** `./gradlew assembleRelease` produces an *unsigned* APK (no keystore
  configured locally) at `app/build/outputs/apk/release/app-release.apk`. To sign locally,
  set the `KEYSTORE_PATH` / `KEYSTORE_PASSWORD` / `KEY_ALIAS` / `KEY_PASSWORD` env vars to
  a keystore you generate with `keytool`, then build again.

> Note: the CI release APK is signed with a self-generated key (fine for sideloading, not
> for the Play Store — Play needs an app-signing key you control). The `app-release-apk`
> artifact is the one to install on your phone.

## Project structure

```
calorie-tracker/
├── app/
│   └── src/main/java/com/example/calorietracker/
│       ├── MainActivity.kt          # navigation + app state
│       ├── data/
│       │   ├── Models.kt            # Plan, FoodLog
│       │   ├── Repository.kt        # Room DB + plan persistence (SharedPreferences)
│       │   └── FoodDatabase.kt      # embedded food list
│       ├── util/PlanCalculator.kt   # BMR/TDEE/macro math
│       └── ui/
│           ├── onboarding/          # setup interview
│           ├── dashboard/           # rings, water, history, backup
│           ├── log/                 # food search + add
│           └── theme/               # light/dark theme
```

## Notes

- Data is stored locally in a Room database on the device. It does not sync anywhere.
- The embedded food list is small (~60 items). Extend it in
  `data/FoodDatabase.kt` — add one line per food.
- The "Quick Add" custom food uses placeholder values (150 cal). It's a stub — extend it
  if you want custom foods with real values.
- Backup codes are plain text in a simple format. Restore parsing is a TODO stub.

## Disclaimer

General guidance only, not medical advice. Consult a physician before starting any diet.
