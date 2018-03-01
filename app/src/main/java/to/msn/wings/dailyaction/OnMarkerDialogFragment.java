package to.msn.wings.dailyaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by kazuya on 2018/02/25.
 */

public class OnMarkerDialogFragment extends DialogFragment {
    private Realm mRealm;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mRealm = Realm.getDefaultInstance();
        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.marker_dialog,null);
        String sid = getArguments().getString("id");
        Long id =Long.parseLong(sid);
        RealmResults<DailyAction> results = mRealm.where(DailyAction.class).equalTo("id",id).findAll();
        if (results.size() == 1) {
            DailyAction dailyAction = results.first();
            TextView txtDate = (TextView) layout.findViewById(R.id.dialog_date);
            TextView txtSubject = (TextView) layout.findViewById(R.id.dialog_subect);
            ImageView img = (ImageView) layout.findViewById(R.id.dialog_image);
            TextView txtComment = (TextView) layout.findViewById(R.id.dialog_comment);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            txtDate.setText(sdf.format(dailyAction.getDate()));
            txtSubject.setText(dailyAction.getSubject());
            txtComment.setText(dailyAction.getComment());
            File file = new File(dailyAction.getPath());
            try {
                InputStream is = new FileInputStream(file);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                img.setImageBitmap(bmp);
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            }
        }
            //ダイアログを作成
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //Dialogの設定
            return builder.setTitle("ギャラリー").setView(layout).create();
    }

}
