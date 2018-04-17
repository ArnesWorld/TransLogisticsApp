package com.example.arne.translogistics_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Arne on 08-03-2018.
 */

public class MenuActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_old_recordings:
                launchViewRecordings();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }



    private void launchViewRecordings(){
        Intent intent = new Intent(getApplicationContext(), DisplayRecordingsActivity.class);
        startActivity(intent);
    }
}
