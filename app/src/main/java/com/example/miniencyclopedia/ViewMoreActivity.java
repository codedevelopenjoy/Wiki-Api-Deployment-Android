package com.example.miniencyclopedia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ViewMoreActivity extends AppCompatActivity {
    TextView summary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        summary = findViewById(R.id.summaryFinal);

        summary.setText(DataBaseClass.summaryOfPosClicked);
    }
}
