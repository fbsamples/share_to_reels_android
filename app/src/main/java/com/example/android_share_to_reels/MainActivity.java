/* Copyright (c) Meta Platforms, Inc. and affiliates.
* All rights reserved.
*
* This source code is licensed under the license found in the
* LICENSE file in the root directory of this source tree.
*/

package com.example.android_share_to_reels;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.facebook.FacebookSdk;

public class MainActivity extends AppCompatActivity {
    private Button btnShareToReels, btnShareToReelsWithSticker;
    private VideoView videoToShare;
    private Uri targetUri;

    final int imageRequestCode = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Activity activity = this;
        final String appID = "YOUR_APP_ID";

        // Get target elements
        videoToShare = findViewById(R.id.videoToShare);
        Button btnLoadVideo = findViewById(R.id.btnLoadVideo);
        btnShareToReels = findViewById(R.id.btnShareToReels);
        btnShareToReelsWithSticker = findViewById(R.id.btnShareToReelsWithSticker);

        // Define initial state
        changeBtnStatus(btnShareToReels, false);
        changeBtnStatus(btnShareToReelsWithSticker, false);
        loadSampleVideo();

        btnLoadVideo.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent. CATEGORY_OPENABLE);
            intent.setType("video/mp4");
            activity.startActivityForResult(intent, imageRequestCode);
        });

        btnShareToReels.setOnClickListener(view -> {
            // Instantiate implicit intent with SHARE_TO_REEL action
            Intent intent = new Intent("com.facebook.reels.SHARE_TO_REEL");

            // Set Application ID
            intent.putExtra("com.facebook.platform.extra.APPLICATION_ID", appID);

            // Define and video asset URI
            intent.setDataAndType(targetUri, "video/mp4");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Instantiate activity and verify it will resolve implicit intent
            activity.grantUriPermission("com.facebook.katana", targetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
                activity.startActivityForResult(intent, 0);
            } else {
                showFailureMessage(activity);
            }
        });

        btnShareToReelsWithSticker.setOnClickListener(view -> {
            // Instantiate implicit intent with SHARE_TO_REEL action
            Intent intent = new Intent("com.facebook.reels.SHARE_TO_REEL");

            // Set Application ID
            intent.putExtra("com.facebook.platform.extra.APPLICATION_ID", appID);

            // Define video URI
            intent.setDataAndType(targetUri, "video/mp4");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Define sticker URI
            Uri stickerAssetUri = createExternalURIFromResource(R.raw.sticker, "sticker.png");
            intent.putExtra("top_background_color", "#71b280");
            intent.putExtra("bottom_background_color", "#71b280");

            intent.putExtra("interactive_asset_uri", stickerAssetUri);

            // Give permissions
            List<ResolveInfo> resInfoList =
                    activity
                            .getPackageManager()
                            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                activity.grantUriPermission(packageName, stickerAssetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            // Start activity
            if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
              activity.startActivityForResult(intent, 2);
            } else {
                showFailureMessage(activity);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && data != null && requestCode == imageRequestCode) {
            targetUri = data.getData();
            loadVideoPreview(targetUri);
            changeBtnStatus(btnShareToReels, true);
            changeBtnStatus(btnShareToReelsWithSticker, true);
        } else {
            loadVideoPreview(targetUri);
        }
    }

    private void loadVideoPreview(Uri path) {
        videoToShare.setVideoURI(path);
        videoToShare.pause();
        videoToShare.seekTo(1);
    }

    private void loadSampleVideo() {
        String path = "android.resource://" + getPackageName() + "/" + R.raw.sample_video;
        Uri sampleVideoUri = Uri.parse(path);
        loadVideoPreview(sampleVideoUri);
    }

    private void showFailureMessage(Activity activity) {

        String message = "Please ensure that the facebook app is installed";
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    private void changeBtnStatus(Button button, Boolean enabled) {
        button.setEnabled(enabled);
        button.setClickable(enabled);

        if (enabled) {
            button.setAlpha(1f);
        } else {
            button.setAlpha(0.5f);
        }
    }

    private Uri createExternalURIFromResource(int resId, String filename) {
        try {
            File parent = new File(FacebookSdk.getApplicationContext().getFilesDir(), "share");
            parent.mkdir();
            File file = new File(parent, filename);
            copy(resId, file);
            return FileProvider.getUriForFile(getApplicationContext(), "com.example.android_share_to_reels.fileprovider", file);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create share content", ex);
        }
    }

    public static void copy(int resourceId, File dst) throws IOException {
        InputStream in = FacebookSdk.getApplicationContext().getResources().openRawResource(resourceId);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

}
