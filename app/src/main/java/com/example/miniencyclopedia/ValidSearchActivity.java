package com.example.miniencyclopedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ValidSearchActivity extends AppCompatActivity {
    private TextView summary,readmore;
    private RecyclerView recyclerView;
    private ImageViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valid_search);

        //initViews
        summary = findViewById(R.id.summary_text);
        readmore = findViewById(R.id.readmore);
        recyclerView = findViewById(R.id.valid_item_image_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        recyclerView.setHasFixedSize(true);

        SpannableString content = new SpannableString("Read in Full Screen");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        readmore.setText(content);

        ItemClass itemClass = DataBaseClass.itemClass;

        //set content
        summary.setText(itemClass.summary);
        adapter= new ImageViewAdapter(this,itemClass.imageList);
        recyclerView.setAdapter(adapter);
        readmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseClass.summaryOfPosClicked = DataBaseClass.itemClass.summary;
                Intent intent = new Intent(ValidSearchActivity.this,ViewMoreActivity.class);
                startActivity(intent);
            }
        });
    }
}
