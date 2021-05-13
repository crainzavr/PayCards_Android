package cards.pay.sample.demo.custom;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cards.pay.paycardsrecognizer.sdk.ui.ScanCardActivity;
import cards.pay.sample.demo.R;

public class CustomScanCardActivity extends ScanCardActivity {

    @Nullable
    private View textView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewGroup activityView = getWindow().findViewById(android.R.id.content);
        View customView = LayoutInflater.from(this).inflate(R.layout.custom_scan_view, (ViewGroup) null);
        textView = customView.findViewById(R.id.custom_text_view);
        activityView.addView(customView);
    }

    @Override
    public void onCardRectCalculated(Rect rect) {
        if (textView != null) {
            textView.setY(rect.bottom);
            textView.animate().alpha(1f).setDuration(500);
        }
    }

}