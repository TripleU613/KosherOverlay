
# KosherOverlay Service

**KosherOverlay** is a lightweight Android foreground service that places a translucent "Kosher Certified" logo on top of all apps — great for system-level certification, branding, kiosk mode, or compliance.

---

## 🕊️ Features

* Persistent **system overlay** with a translucent image
* Runs as a **foreground service** with notification
* Automatically **restarts** if killed
* Touch-invisible and non-blocking
* Positioned neatly in the bottom-right corner

---

## 📲 How It Works

1. Launches as a foreground service to avoid system restrictions.
2. Uses the `WindowManager` API to create a non-interactive overlay.
3. Displays a semi-transparent kosher logo (`R.drawable.kosher_logo`) in a small square (default: 64dp).
4. Automatically restarts if the service is destroyed.

---

## 🧩 Setup

**Image Resource**
Add your logo to `res/drawable` as `kosher_logo.png`.

**Overlay Permission**
Make sure your app requests `SYSTEM_ALERT_WINDOW` permission in the manifest and at runtime (Android 6+):

```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```

**Foreground Notification Icon**
Replace `R.mipmap.ic_launcher` with your desired icon.

---

## 📐 Customization

* Change position via `params.gravity = Gravity.BOTTOM or Gravity.END`
* Adjust size with `imageSizeDp`
* Modify transparency via `overlayImageView.alpha = 0.2f`

---

## 🛠 Requirements

* Android 8.0+ (for `TYPE_APPLICATION_OVERLAY`)
* Overlay permission granted manually by the user
