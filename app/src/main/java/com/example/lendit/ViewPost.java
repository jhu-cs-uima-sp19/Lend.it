package com.example.lendit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ViewPost extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
    }

    public void sendMSG(View v) {
        Intent i = new Intent(ViewPost.this, Message.class);
        //need to send name?
        startActivity(i);

    }
}
