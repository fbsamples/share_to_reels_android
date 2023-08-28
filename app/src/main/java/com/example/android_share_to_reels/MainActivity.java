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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.facebook.FacebookSdk;

public class MainActivity extends AppCompatActivity {
    private VideoView videoToShare;
    private Button btnLoadVideo, btnShareToFBReels, btnShareToFBReelsWithSticker,
            btnShareToIGReels, btnShareToIGReelsWithSticker;
    private ArrayList<Button> buttons;
    private Uri targetUri;
    private String appID;

    final int videoRequestCode = 42;
    final String fbReelsIntentName = "com.facebook.reels.SHARE_TO_REEL";
    final String igReelsIntentName = "com.instagram.share.ADD_TO_REEL";
    final String fbPackageName = "com.facebook.katana";
    final String igPackageName = "com.instagram.android";
    final String fbAppName = "Facebook";
    final String igAppName = "Instagram";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Activity activity = this;

        appID = getString(R.string.facebook_app_id);
        getTargetElements();
        buttons = new ArrayList<>(Arrays.asList(
                btnLoadVideo, btnShareToFBReels, btnShareToFBReelsWithSticker,
                btnShareToIGReels, btnShareToIGReelsWithSticker));
        loadInitialState();
        Uri stickerAssetUri = createExternalURIFromResource(R.raw.sticker, "sticker.png");

        btnLoadVideo.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent. CATEGORY_OPENABLE);
            intent.setType("video/*");
            activity.startActivityForResult(intent, videoRequestCode);
        });

        btnShareToFBReels.setOnClickListener(view -> {
            Intent intent = buildFBReelsIntent();

            if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
                activity.startActivityForResult(intent, 0);
            } else {
                showFailureMessage(activity, fbAppName);
            }
        });

        btnShareToFBReelsWithSticker.setOnClickListener(view -> {
            Intent intent = buildFBReelsIntent();

            defineStickerURI(intent, stickerAssetUri);
            giveUriPermissions(activity, intent, stickerAssetUri);

            if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
                activity.startActivityForResult(intent, 0);
            } else {
                showFailureMessage(activity, fbAppName);
            }
        });

        btnShareToIGReels.setOnClickListener(view -> {
            Intent intent = buildIGReelsIntent();

            if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
                activity.startActivityForResult(intent, 0);
            } else {
                showFailureMessage(activity, igAppName);
            }
        });

        btnShareToIGReelsWithSticker.setOnClickListener(view -> {
            Intent intent = buildIGReelsIntent();

            defineStickerURI(intent, stickerAssetUri);
            giveUriPermissions(activity, intent, stickerAssetUri);

            if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
                activity.startActivityForResult(intent, 0);
            } else {
                showFailureMessage(activity, igAppName);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadVideoPreview(targetUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && data != null && requestCode == videoRequestCode) {
            targetUri = data.getData();
            loadVideoPreview(targetUri);
            for (Button b: buttons) {
                changeBtnStatus(b, true);
            }
        } else {
            loadVideoPreview(targetUri);
        }
    }

    private void getTargetElements() {
        videoToShare = findViewById(R.id.videoToShare);
        btnLoadVideo = findViewById(R.id.btnLoadVideo);
        btnShareToFBReels = findViewById(R.id.btnShareToFBReels);
        btnShareToFBReelsWithSticker = findViewById(R.id.btnShareToFBReelsWithSticker);
        btnShareToIGReels = findViewById(R.id.btnShareToIGReels);
        btnShareToIGReelsWithSticker = findViewById(R.id.btnShareToIGReelsWithSticker);
    }

    private void loadInitialState() {
        for (Button b: buttons) {
            changeBtnStatus(b, true);
        }
        loadSampleVideo();
    }

    private Intent buildFBReelsIntent() {
        Intent intent = getInitialIntent(fbReelsIntentName);
        intent.setPackage(fbPackageName);

        intent.putExtra("com.facebook.platform.extra.APPLICATION_ID", appID);
        this.grantUriPermission(fbPackageName, targetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        giveUriPermissions(this, intent, targetUri);

        return intent;
    }

    private Intent buildIGReelsIntent() {
        Intent intent = getInitialIntent(igReelsIntentName);
        intent.setPackage(igPackageName);

        intent.putExtra("source_application", this.getPackageName());
        intent.putExtra("com.instagram.platform.extra.APPLICATION_ID", appID);
        intent.putExtra(Intent.EXTRA_STREAM, targetUri);
        this.grantUriPermission(igPackageName, targetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        giveUriPermissions(this, intent, targetUri);

        return intent;
    }

    private Intent getInitialIntent(String intentName) {
        Intent intent = new Intent(intentName);
        defineVideoURI(intent);

        return intent;
    }

    private void defineStickerURI(Intent intent, Uri stickerAssetUri) {
        intent.putExtra("interactive_asset_uri", stickerAssetUri);
    }

    private void giveUriPermissions(Activity activity, Intent intent, Uri uri) {
        List<ResolveInfo> resInfoList =
                activity
                        .getPackageManager()
                        .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            activity.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    private void defineVideoURI(Intent intent) {
        intent.setDataAndType(targetUri, "video/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    private void loadVideoPreview(Uri path) {
        videoToShare.setVideoURI(path);
        videoToShare.pause();
        videoToShare.seekTo(1);
    }

    private void loadSampleVideo() {
        targetUri = createExternalURIFromResource(R.raw.sample_video, "sample_video.MOV");

        loadVideoPreview(targetUri);
    }

    private void showFailureMessage(Activity activity, String appName) {
        String message = "Please ensure that the " + appName + " app is installed.";
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
            return FileProvider
                    .getUriForFile(getApplicationContext(),
                            "com.example.android_share_to_reels.fileprovider", file);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create share content", ex);
        }
    }

    public static void copy(int resourceId, File dst) throws IOException {
        InputStream in = FacebookSdk.getApplicationContext()
                .getResources().openRawResource(resourceId);
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
