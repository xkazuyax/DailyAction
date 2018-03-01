package to.msn.wings.dailyaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class TopActivity extends AppCompatActivity {
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String name = pref.getString("name","no_name");

        TextView loginUsername = (TextView)findViewById(R.id.login_username);
        loginUsername.setText(String.format("%sさんログイン中",name));
    }

    @Override
    public void onResume() {
        super.onResume();
        String id =pref.getString("id","-1");
        if (Integer.parseInt(id) == -1) {
            finish();
        }
    }

    //メニュー定義ファイルからオプションメニューを表示
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    //メニュー選択時に項目を表示
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
                finish();
        }
        return true;
    }

    public void show_map(View view) {
        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);
    }

    public void show_dataFile(View view) {
        Intent intent = new Intent(this,DataFileActivity.class);
        startActivity(intent);
    }

    public void show_slideshow(View view) {
        Intent intent = new Intent(this,SlideShowActivity.class);
        startActivity(intent);
    }

    public void show_webpage(View view) {
        Intent intent = new Intent(this,WebViewActivity.class);
        startActivity(intent);
    }
}
