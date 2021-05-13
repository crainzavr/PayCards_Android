package cards.pay.sample.demo;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import cards.pay.paycardsrecognizer.sdk.ScanCardIntent;
import cards.pay.sample.demo.custom.CustomScanCardActivity;

public class IntroActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mToolbar = findViewById(R.id.toolbar);
        setupToolbar();

        findViewById(R.id.button_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCardDetails();
            }
        });

        findViewById(R.id.button_next_custom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCustomScanActivity();
            }
        });
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCardDetails();
            }
        });
    }

    private void goToCardDetails() {
        Intent intent = new Intent(this, CardDetailsActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToCustomScanActivity() {
        Intent intent = new ScanCardIntent.Builder(this).build(CustomScanCardActivity.class);
        startActivity(intent);
    }
}
