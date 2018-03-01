package to.msn.wings.dailyaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import io.realm.Realm;
import io.realm.RealmResults;

public class DataFileActivity extends AppCompatActivity {
    Realm mRealm;
    ListView listView;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_file);

        mRealm = Realm.getDefaultInstance();
        RealmResults<DailyAction> results = mRealm.where(DailyAction.class).findAll();
        DataAdapter dataAdapter = new DataAdapter(results);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id) {
                DailyAction dailyAction = (DailyAction) parent.getItemAtPosition(position);
                Intent intent = new Intent(DataFileActivity.this,DataFileSelectedActivity.class);
                intent.putExtra("id",dailyAction.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String id = pref.getString("id","-1");
        if (Integer.parseInt(id) == -1) {
           finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

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
