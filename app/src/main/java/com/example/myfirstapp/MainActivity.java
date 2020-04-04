package com.example.myfirstapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public static final String EXTRA_HOUR = "com.example.myfirstapp.HOUR";
    private static final String TAG = MainActivity.class.getSimpleName();
    private Session session = null;
    private boolean mUserRequestedInstall = true;
    private boolean installRequested;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        installRequested = true;
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
////        // ARCore requires camera permission to operate.
////        if (!CameraPermissionHelper.hasCameraPermission(this)) {
////            CameraPermissionHelper.requestCameraPermission(this);
////            return;
////        }
////
////        // Make sure Google Play Services for AR is installed and up to date.
////        try {
////            switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
////                case INSTALL_REQUESTED:
////                    installRequested = true;
////                    return;
////                case INSTALLED:
////                    break;
////            }
////            if (mSession == null) {
////                switch (ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)) {
////                    case INSTALLED:
////                        // Success, create the AR session.
////                        mSession = new Session(this);
////                        System.out.println("\n\nCreated the AR session\n\n");
////                        break;
////                    case INSTALL_REQUESTED:
////                        // Ensures next invocation of requestInstall() will either return
////                        // INSTALLED or throw an exception.
////                        mUserRequestedInstall = false;
////                        return;
////                }
////            }
////        } catch (UnavailableUserDeclinedInstallationException | UnavailableDeviceNotCompatibleException e) {
////            // Display an appropriate message to the user and return gracefully.
////            Toast.makeText(this, "TODO: handle exception " + e, Toast.LENGTH_LONG)
////                    .show();
////            return;
////        } catch (UnavailableArcoreNotInstalledException e) {
////            e.printStackTrace();
////        } catch (UnavailableSdkTooOldException e) {
////            e.printStackTrace();
////        } catch (UnavailableApkTooOldException e) {
////            e.printStackTrace();
////        }
//
//
//
//    }

    @Override
    protected void onResume() {
        super.onResume();

        if (session == null) {
            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this);
                    return;
                }

                // Create the session.
                session = new Session(this);
                System.out.println("\n\n\n\n\n\n\n\n\n\t Session created \n\n\n\n\n\n");

            } catch (UnavailableArcoreNotInstalledException
                    | UnavailableUserDeclinedInstallationException e) {
                message = "Please install ARCore";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = "Please update ARCore";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = "Please update this app";
                exception = e;
            } catch (UnavailableDeviceNotCompatibleException e) {
                message = "This device does not support AR";
                exception = e;
            } catch (Exception e) {
                message = "Failed to create AR session";
                exception = e;
            }

            if (message != null) {
                Log.e(TAG, "Exception creating session", exception);
                System.out.println("\n\n\n\nException creating session " + exception.toString());
                return;
            }
        }

        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            session.resume();
        } catch (CameraNotAvailableException e) {
            session = null;
            return;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                    .show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
    }

//    private boolean isARCoreSupportedAndUpToDate() {
//        // Make sure ARCore is installed and supported on this device.
//        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
//        switch (availability) {
//            case SUPPORTED_INSTALLED:
//                break;
//            case SUPPORTED_APK_TOO_OLD:
//            case SUPPORTED_NOT_INSTALLED:
//                try {
//                    // Request ARCore installation or update if needed.
//                    ArCoreApk.InstallStatus installStatus =
//                            ArCoreApk.getInstance().requestInstall(this, /*userRequestedInstall=*/ true);
//                    switch (installStatus) {
//                        case INSTALL_REQUESTED:
//                            Log.e(TAG, "ARCore installation requested.");
//                            return false;
//                        case INSTALLED:
//                            break;
//                    }
//                } catch (UnavailableException e) {
//                    Log.e(TAG, "ARCore not installed", e);
////                    runOnUiThread(
////                            () ->
////                                    Toast.makeText(
////                                            getApplicationContext(), "ARCore not installed\n" + e, Toast.LENGTH_LONG)
////                                            .show());
//                    finish();
//                    return false;
//                }
//                break;
//            case UNKNOWN_ERROR:
//            case UNKNOWN_CHECKING:
//            case UNKNOWN_TIMED_OUT:
//            case UNSUPPORTED_DEVICE_NOT_CAPABLE:
//                Log.e(
//                        TAG,
//                        "ARCore is not supported on this device, ArCoreApk.checkAvailability() returned "
//                                + availability);
////                runOnUiThread(
////                        () ->
////                                Toast.makeText(
////                                        getApplicationContext(),
////                                        "ARCore is not supported on this device, "
////                                                + "ArCoreApk.checkAvailability() returned "
////                                                + availability,
////                                        Toast.LENGTH_LONG)
////                                        .show());
//                return false;
//        }
//        return true;
//    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /**
     * Called when the user taps the Hour button
     */
    public void displayHour(View view) {
        Date currentDate = new Date();
        Intent intent = new Intent(this, DisplayHourActivity.class);
        intent.putExtra(EXTRA_HOUR, currentDate.toString());
        startActivity(intent);
    }

    /**
     * Called when the user taps the Hour button
     */
    public void openWeb(View view) {
        Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
        Uri intentUri =
                Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
                        .appendQueryParameter("file", "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF/Avocado.gltf")
                        .appendQueryParameter("mode", "ar_preferred")
                        .build();
        sceneViewerIntent.setData(intentUri);
        sceneViewerIntent.setPackage("com.google.ar.core");
        startActivity(sceneViewerIntent);
    }

}
