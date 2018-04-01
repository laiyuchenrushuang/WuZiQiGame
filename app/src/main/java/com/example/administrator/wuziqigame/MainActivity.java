package com.example.administrator.wuziqigame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

private GamingBGView mGamingBGView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGamingBGView = (GamingBGView) findViewById(R.id.gaming_background);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_setting:
                mGamingBGView.reStart();
                return true;
            case R.id.action_step_back:
                mGamingBGView.stepback();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
