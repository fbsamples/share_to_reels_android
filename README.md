# Share to Reels

## Android Sample App

This is an Android sample app with integrations for sharing to Facebook and Instagram Reels. After running the app, you will reach a screen where you can upload a video from your phone's gallery app.

You can choose to share with or without a sticker. If you choose one of the sticker options, the media file is uploaded with an embedded sticker.

The integration code is available under `app/src/main/java/com/example/android_share_to_reels/MainActivity.java`. There, you can find buttons for sharing to Reels. Each button has an onClick event listener. You can find the code for the Share to Reels integration there.

> :warning: You will need to enable **FacebookSdk** for your project. Follow the instructions on this [link](https://developers.facebook.com/docs/android/getting-started/) for that.

## Required Software

In order to run the sample app you will need to install some required software, as follows:

- Android Studio
- Android AutoUpgrader
- Android NDK
- Java Runtime AutoUpgrader
- Adopt OpenJDK 8

## Add Your App ID and Client Token

In order to run the app, you will need to update the code and add your Facebook Developer App ID and Client Token. If you don't have an app, check out this [link](https://developers.facebook.com/docs/development/).

Update the following file(s):
- app/src/main/res/values/strings.xml

You will need to replace `YOUR_APP_ID` with your app's ID and `YOUR_CLIENT_TOKEN` with your app's client token. Your app's ID can be found [here](https://developers.facebook.com/apps). The client token can be found in your app's dashboard by following these steps:
1. Sign into your developer account.
2. On the Apps page, select an app to open the dashboard for that app.
3. On the Dashboard, navigate to Settings > Advanced > Security > Client token.

## Running the Project

1. Open the sample app project on Android Studio.
2. Enable dev mode on your *Android phone*.
3. Connect your phone to your Mac or PC using a USB cable.
4. Your phone should appear on the devices tab in Android Studio.
5. Select your phone and click on the play button. The app should load on your phone.

> **Enabling Dev Mode on Your Android Phone**
>- Open the Settings App.
>- Scroll down to About Phone.
>- Tap Software Information.
>- Tap Build number seven times.
>- Enter your pin or password.
>- Go back to the initial screen of the Settings App and the Developer Options tab should be there.
>- Access the Developer Options on your phone and enable USB debugging.

## iOS Sample App

If you want to see the iOS integration, you can access [this project](https://github.com/fbsamples/share_to_reels_ios).

## License
Share to Reels Android is Meta Platform Policy licensed, as found in the LICENSE file.
