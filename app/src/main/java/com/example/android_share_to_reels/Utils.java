package com.example.android_share_to_reels;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.core.content.FileProvider;

import com.facebook.FacebookSdk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {
    public static final String fbPackageName = "com.facebook.katana";
    public static final String igPackageName = "com.instagram.android";
    private static final String INTERACTIVE_ASSET_URI = "interactive_asset_uri";
    private static final String FILE_PROVIDER = "com.example.android_share_to_reels.fileprovider";

    public static void startActivityWithSharedContent(Activity activity, Intent intent, String appName) {
        if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
            activity.startActivityForResult(intent, 0);
        } else {
            showFailureMessage(activity, appName);
        }
    }

    public static void giveUriPermissions(Activity activity, Intent intent, Uri uri, String appPackageName) {
        activity.grantUriPermission(appPackageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        List<ResolveInfo> resInfoList =
                activity
                        .getPackageManager()
                        .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            activity.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    public static void configureStickerURI(Activity activity, Intent intent, Uri stickerAssetUri, String appPackageName) {
        intent.putExtra(INTERACTIVE_ASSET_URI, stickerAssetUri);
        giveUriPermissions(activity, intent, stickerAssetUri, appPackageName);
    }

    public static Intent getInitialIntent(String intentName) {
        Intent intent = new Intent(intentName);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return intent;
    }

    public static Intent getVideoUploaderIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");

        return intent;
    }

    public static Intent getImageUploaderIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        return intent;
    }

    public static Uri getSampleVideo(Context context) {
        return createExternalURIFromResource(context, R.raw.sample_video, "sample_video.MOV");
    }

    public static Uri getSampleImage(Context context) {
        return createExternalURIFromResource(context, R.raw.sample_image, "sample_image.jpg");
    }

    public static Uri getSampleSticker(Context context) {
        return createExternalURIFromResource(context, R.raw.sticker, "sticker.png");
    }

    public static void loadVideoPreview(VideoView videoToShare, Uri path) {
        videoToShare.setVideoURI(path);
        videoToShare.pause();
        videoToShare.seekTo(1);
    }

    public static void loadImagePreview(ImageView imageToShare, ContentResolver contentResolver, Context context, Uri path) {
        Uri resizedImageUri = Utils.getResizedImageUri(contentResolver,
                context, path,
                imageToShare.getLayoutParams().width, imageToShare.getLayoutParams().height);
        imageToShare.setImageURI(resizedImageUri);
    }

    public static void changeButtonStatus(Button button, Boolean enabled) {
        button.setEnabled(enabled);
        button.setClickable(enabled);

        if (enabled) {
            button.setAlpha(1f);
        } else {
            button.setAlpha(0.5f);
        }
    }

    private static void showFailureMessage(Activity activity, String appName) {
        String message = "Please ensure that the " + appName + " app is installed.";
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    private static Uri createExternalURIFromResource(Context context, int resId, String filename) {
        try {
            File parent = new File(FacebookSdk.getApplicationContext().getFilesDir(), "share");
            parent.mkdir();
            File file = new File(parent, filename);
            copy(resId, file);
            return FileProvider.getUriForFile(context, FILE_PROVIDER, file);
        } catch (IOException error) {
            throw new RuntimeException("failed to create share content: ", error);
        }
    }

    private static void copy(int resourceId, File dst) throws IOException {
        InputStream in = FacebookSdk.getApplicationContext().getResources().openRawResource(resourceId);
        OutputStream out = new FileOutputStream(dst);

        // transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    private static Uri getResizedImageUri(ContentResolver contentResolver, Context context, Uri imageUri, int width, int height) {
        try {
            InputStream imageStreamForBitmap = contentResolver.openInputStream(imageUri);
            InputStream imageStreamForExif = contentResolver.openInputStream(imageUri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStreamForBitmap);

            selectedImage = Utils.resizeBitmap(selectedImage, width,
                    height, imageStreamForExif);
            Uri newImageUri = Utils.getImageUriFromBitmap(context, selectedImage);

            return newImageUri;
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return null;
        }
    }

    private static Bitmap resizeBitmap(Bitmap image, int maxWidth, int maxHeight, InputStream inputStream) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();

            float ratioBitmap = (float) width / (float) height;
            float maxWidthFloat = (float) maxWidth;
            float maxHeightFloat = (float) maxHeight;
            float ratioMax = maxWidthFloat / maxHeightFloat;

            int finalWidth;
            int finalHeight;

            if (ratioMax > ratioBitmap) {
                finalWidth = (int) (maxHeightFloat * ratioBitmap);
                finalHeight = maxHeight;
            } else {
                finalWidth = maxWidth;
                finalHeight = (int) (maxWidthFloat / ratioBitmap);
            }

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return Bitmap.createBitmap(resizedBitmap, 0, 0, finalWidth, finalHeight, getImageMatrix(inputStream), true);
        }
        return image;
    }

    private static Uri getImageUriFromBitmap(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";

        File cacheDir = context.getCacheDir();
        File imageFile = new File(cacheDir, imageFileName);

        try {
            FileOutputStream fo = new FileOutputStream(imageFile);
            fo.write(bytes.toByteArray());
            fo.flush();
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return FileProvider.getUriForFile(context, FILE_PROVIDER, imageFile);
    }

    private static Matrix getImageMatrix(InputStream inputStream) {
        Matrix matrix = new Matrix();
        try {
            ExifInterface exif = new ExifInterface(inputStream);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            matrix.postRotate(getRotationAngle(orientation));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return matrix;
    }

    private static int getRotationAngle(int orientation) {
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }
}
