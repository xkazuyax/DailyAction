package to.msn.wings.dailyaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import io.realm.Realm;
import io.realm.RealmResults;

public class DataFileSelectedActivity extends AppCompatActivity {
    Realm mRealm;
    TextView selected_date;
    TextView selected_title;
    TextView selected_comment;
    ImageView selected_imageView;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_file_selected);

        selected_date = (TextView) findViewById(R.id.selected_date);
        selected_title = (TextView) findViewById(R.id.selected_subject);
        selected_comment = (TextView) findViewById(R.id.selected_comment);
        selected_imageView = (ImageView) findViewById(R.id.selected_imageView);

        mRealm = Realm.getDefaultInstance();
        Intent intent = getIntent();
        Long id = intent.getLongExtra("id",-1);
        if (id != -1) {
            RealmResults<DailyAction> results = mRealm.where(DailyAction.class).equalTo("id", id).findAll();
            if (results.size() == 1) {
                DailyAction dailyAction = results.first();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String date = sdf.format(dailyAction.getDate());
                selected_date.setText(date);
                selected_title.setText(dailyAction.getSubject());
                selected_comment.setText(dailyAction.getComment());
                File file = new File(dailyAction.getPath());
                try {
                    InputStream inputStream = new FileInputStream(file);
                    Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                    selected_imageView.setImageBitmap(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String login_id = pref.getString("id","-1");
        if (Integer.parseInt(login_id) == -1) {
          finish();
        }
    }

    //メニュー定義をもとにオプションメニューを作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    //メニューの選択時
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.logout:
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
               finish();
        }
        return true;
    }

    public void back(View view) {
        finish();
    }
}
