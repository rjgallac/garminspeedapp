To build and test your "Garmin Speed App" on a physical Android device, I recommend following the standard professional workflow used in Android development. Since you are already working within Android Studio, most of the heavy lifting is automated.
Here is the best way to approach this:
1. Prepare Your Physical Device
   Before you can run the app, you must enable Developer Options and USB Debugging on your phone:
   •
   Enable Developer Options: Go to Settings > About Phone and tap Build Number seven times until you see a message saying "You are now a developer!"
   •
   Enable USB Debugging: Go to Settings > System > Developer Options and toggle on USB Debugging.
2. Choose Your Connection Method
   There are two primary ways to connect your phone to Android Studio:
   A. Wired Connection (Most Stable)
   •
   Connect your phone to your computer using a high-quality USB cable.
   •
   When prompted on your phone, select "Allow USB debugging" (check "Always allow from this computer").
   •
   Pros: Extremely stable, fast deployment, and handles large builds more reliably.
   B. Wireless Debugging (Most Convenient)
   If you are on the same Wi-Fi network as your computer and running Android 11 or higher:
   •
   Go to Developer Options > Wireless debugging and enable it.
   •
   In Android Studio, go to the device dropdown menu and select "Pair Devices Using Wi-Fi".
   •
   Follow the QR code or pairing code instructions.
   •
   Pros: No cables required; allows you to move around freely while testing UI/UX.
3. The Build & Run Process
   Once your device is recognized by Android Studio (you'll see its name in the top toolbar):
1.
Select the App Module: Ensure :app is selected in the run configuration dropdown.
2.
Click "Run" (Green Play Icon): This triggers a gradle assembleDebug task followed by an installation of the APK onto your phone.
3.
Monitor Logcat: Use the Logcat window at the bottom of Android Studio to see real-time logs, crashes, and print statements from your app while it's running on the device.
Summary Recommendation
Feature
Recommended Approach
Why?
Connection
USB Debugging
It is much faster for initial setup and less prone to network latency during builds.
Build Type
Debug Variant
The debug build is optimized for development, allowing for debugging, hot swapping, and faster iteration.
Testing Focus
Physical Device
For a "Speed App," testing on real hardware is crucial to ensure performance, GPS accuracy, and sensor integration behave as expected under real-world conditions.
Pro Tip: If you find yourself frequently reinstalling the app, consider using "Apply Changes" (the icon with a small lightning bolt next to the Run button) in Android Studio. It allows you to push code changes to your running device without restarting the entire application.