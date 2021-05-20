package cards.pay.paycardsrecognizer.sdk.camera;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;

import cards.pay.paycardsrecognizer.sdk.ndk.RecognitionCore;
import cards.pay.paycardsrecognizer.sdk.ndk.TorchStatusListener;
import cards.pay.paycardsrecognizer.sdk.utils.Constants;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class TorchManager {

    public static IntentFilter getTorchStateIntentFilter() {
        return new IntentFilter(TORCH_STATE_ACTION_NAME);
    }
    public static Boolean getTorchStateFromIntent(@NonNull Intent intent) {
        return intent.getBooleanExtra(TORCH_STATE_NAME, false);
    }

    private static final String TORCH_STATE_ACTION_NAME = "TORCH_STATE_ACTION";
    private static final String TORCH_STATE_NAME = "TORCH_STATE";

    private static final boolean DBG = Constants.DEBUG;
    private static final String TAG = "TorchManager";

    private final Camera mCamera;

    private boolean mPaused;

    private boolean mTorchTurnedOn;

    private final RecognitionCore mRecognitionCore;

    private final Context mContext;

    public TorchManager(RecognitionCore recognitionCore, Camera camera, Context context) {
        mCamera = camera;
        mRecognitionCore = recognitionCore;
        mContext = context;
    }

    public void pause() {
        if (DBG) Log.d(TAG, "pause()");
        CameraConfigurationUtils.setFlashLight(mCamera, false);
        mPaused = true;
        mRecognitionCore.setTorchListener(null);
    }

    public void resume() {
        if (DBG) Log.d(TAG, "resume()");
        mPaused = false;
        mRecognitionCore.setTorchListener(mRecognitionCoreTorchStatusListener);
        if (mTorchTurnedOn) {
            mRecognitionCore.setTorchStatus(true);
        } else {
            mRecognitionCore.setTorchStatus(false);
        }
    }

    public void destroy() {
        mRecognitionCore.setTorchListener(null);
    }

    private boolean isTorchTurnedOn() {
        String flashMode = mCamera.getParameters().getFlashMode();
        return Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)
                || Camera.Parameters.FLASH_MODE_ON.equals(flashMode);
    }

    public void toggleTorch() {
        if (mPaused) return;
        boolean newStatus = !isTorchTurnedOn();
        if (DBG) Log.d(TAG, "toggleTorch() called with newStatus: " +  newStatus);
        mRecognitionCore.setTorchStatus(newStatus);

        // onTorchStatusChanged() will not be called if the RecognitionCore internal status will not be changed.
        // Sync twice to keep safe
        CameraConfigurationUtils.setFlashLight(mCamera, newStatus);
    }

    private final TorchStatusListener mRecognitionCoreTorchStatusListener = new TorchStatusListener() {

        // called from RecognitionCore
        @Override
        public void onTorchStatusChanged(boolean turnTorchOn) {
            if (mCamera == null) return;
            if (DBG) Log.d(TAG, "onTorchStatusChanged() called with: " +  "turnTorchOn = [" + turnTorchOn + "]");
            if (turnTorchOn) {
                mTorchTurnedOn = true;
                if (!mPaused) CameraConfigurationUtils.setFlashLight(mCamera, true);
            } else {
                mTorchTurnedOn = false;
                CameraConfigurationUtils.setFlashLight(mCamera, false);
            }

            sendBroadcast(mTorchTurnedOn);
        }

        private void sendBroadcast(Boolean isTorchOn) {
            Intent intent = new Intent(TORCH_STATE_ACTION_NAME);
            intent.putExtra(TORCH_STATE_NAME, isTorchOn);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    };
}
