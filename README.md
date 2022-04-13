# Share to Reels
## Android Sample App

This is an Android Sample app with an integration to Facebook Share to Reels. Once you run the app, you will reach a screen where you can upload a video from your gallery. Then, you have two options to choose:

- Share to reels
- Share to reels with sticker

The difference between them is that in the share to reels with sticker functionality, the video is uploaded to reels with an embedded sticker.

The integration code is available under `app/src/main/java/com/example/android_share_to_reels/MainActivity.java`. There, you can find two buttons btnShareToReels and btnShareToReelsWithSticker. Each one of them has an onclick event attached. You can find the code for the integration with Share to Reels (Facebook) there.

> :warning: You will need to enable **FacebookSdk** for your project. Follow the instructions on this [link](https://developers.facebook.com/docs/android/getting-started/) for that.

## Required software

In order to run the sample app you will need to install some required software, as follows:

- Android Studio;
- Android AutoUpgrader;
- Android NDK;
- Java Runtime AutoUpgrader;
- Adopt OpenJDK 8.

## Add your app id

In order to run the app, you will need to update the code and add your Facebook Developer App Id. If you don't have an app, check out this [link](https://developers.facebook.com/docs/development/).

Update the following files:
- MainActivity.java
- strings.xml

You will need to fill in your app id in the `"YOUR_APP_ID"` strings.

## Running the project

1. Open the sample app project on Android Studio;
3. Enable dev mode on your Android phone*;
4. Connect your phone to your Mac or PC using a USB cable;
5. Your phone should appear on the devices tab in Android Studio;
6. Select your phone and click on the play button. The app should load on your phone.

> **Enabling dev mode on your Android phone**
>- Open the Settings App;
>- Scroll down to About Phone;
>- Tap Software Information;
>- Tap Build number seven times;
>- Enter your pin or password;
>- Go back to the initial screen of the Settings App and the Developer Options tab should be there;
>- Access the Developer Options on your phone and enable USB debugging.

## iOS Sample App

If you want to see the iOS integration, you can access [this project](https://github.com/fbsamples/share_to_reels_ios).

## License
Share to Reels Android is Meta Platform Policy licensed, as found in the LICENSE file.
