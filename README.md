# KosherOverlay Service

**KosherOverlay** is a lightweight Android foreground service that places a translucent "Kosher Certified" logo on top of all apps — great for system-level certification, branding, kiosk mode, or compliance.

---

## 🕊️ Features

- Persistent **system overlay** with a translucent image
- Runs as a **foreground service** with notification
- Automatically **restarts** if killed
- Touch-invisible and non-blocking
- Positioned neatly in the bottom-right corner

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

```

**Foreground Notification Icon**  
Replace `R.mipmap.ic_launcher` with your desired icon.

**Privileged Permissions**  
A provided permissions file must be pushed to `/system/etc/permissions.xml` to grant the necessary privileged permissions for the KosherOverlay service when installed as a system app. This step is required if you deploy the app in `/system/priv-app` and need privileged capabilities.

---

## 📐 Customization

- Change position via `params.gravity = Gravity.BOTTOM or Gravity.END`
- Adjust size with `imageSizeDp`
- Modify transparency via `overlayImageView.alpha = 0.2f`

---

## 🛠 Requirements

- Android 8.0+ (for `TYPE_APPLICATION_OVERLAY`)
- Overlay permission granted manually by the user
- If installed as a system app, the permissions file must be present in `/system/etc/permissions.xml`


## ⚠️🚫 Project Usage Notice

> 🛠️ **This project is intended for private, non-commercial use only.**  
> 📦 **Redistribution, resale, or inclusion in any commercial bundle is strictly forbidden.**  
>  
> 🔒 Licensed under the [GNU General Public License v3.0 (GPL-3.0)](https://www.gnu.org/licenses/gpl-3.0.html) —  
> you are free to use, study, and modify this project **for personal purposes only**.
>  
> 💸 **Using this code in a paid product, reselling it, or profiting from it in any way violates the license.**

📚 Related Resources:  
- [GPL License Summary](https://choosealicense.com/licenses/gpl-3.0/)  
- [GPL FAQ (gnu.org)](https://www.gnu.org/licenses/gpl-faq.html)  
- [Full License Text](https://www.gnu.org/licenses/gpl-3.0.txt)
