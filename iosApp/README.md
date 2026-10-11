# LEDGER iOS App & IPA Build Guide

This directory contains the native iOS / SwiftUI version of the **LEDGER** application.

---

## Method 1: Automatic Cloud Build (No Mac Required)

1. Use the **Push to GitHub** feature in Google AI Studio (top right / settings menu) or push this repository to GitHub.
2. Go to your repository on GitHub and click the **Actions** tab.
3. The **"Build iOS IPA"** workflow will automatically run on a GitHub macOS runner.
4. When finished (usually ~2 minutes), open the run summary and download the **`Ledger-iOS-IPA`** artifact containing `Ledger-iOS.ipa`.

---

## Method 2: Sideloading the IPA on your iPhone

Once you have `Ledger-iOS.ipa`:

1. **Using Sideloadly (Windows / Mac)**:
   - Download [Sideloadly](https://sideloadly.io/).
   - Plug your iPhone into your computer.
   - Drag and drop `Ledger-iOS.ipa` into Sideloadly.
   - Enter your Apple ID and click **Start**.
   - On your iPhone, go to **Settings > General > VPN & Device Management** and tap **Trust**.

2. **Using AltStore**:
   - Install AltStore on your device.
   - Send `Ledger-iOS.ipa` to your iPhone (AirDrop, iCloud, or Files).
   - In AltStore, go to **My Apps > '+'** and select `Ledger-iOS.ipa`.

3. **Using TrollStore** (if on iOS 14.0 - 16.6.1):
   - Open `Ledger-iOS.ipa` directly in TrollStore to install instantly without revokes or 7-day limits.

---

## Method 3: Opening in Xcode (Mac)

1. Open Terminal and run:
   ```bash
   open iosApp/iosApp.xcodeproj
   ```
2. In Xcode:
   - Select your target device or iOS Simulator.
   - Under **Signing & Capabilities**, select your Team (free personal team works).
   - Press **Cmd + R** to run directly on your iPhone.
   - To export an `.ipa`: Go to **Product > Archive > Distribute App**.
