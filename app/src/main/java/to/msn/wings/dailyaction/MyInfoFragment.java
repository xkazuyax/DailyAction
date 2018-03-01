package to.msn.wings.dailyaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by kazuya on 2018/02/28.
 */

public class MyInfoFragment extends DialogFragment {
    Realm mRealm;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.my_info_fragment,null);
        ImageView imageView = (ImageView)layout.findViewById(R.id.face);
        TextView name = (TextView) layout.findViewById(R.id.name);
        long id = getArguments().getLong("id",-1);
        mRealm = Realm.getDefaultInstance();
        RealmResults<UserInfo> results = mRealm.where(UserInfo.class).equalTo("id",id).findAll();
        if (results.size() == 1) {
            UserInfo userInfo = results.first();
            ParcelFileDescriptor pfDescriptor = null;
            try {
                pfDescriptor = getActivity().getContentResolver().openFileDescriptor(Uri.parse(userInfo.getPicturePass()),"r");
                if (pfDescriptor != null) {
                    FileDescriptor fileDescriptor = pfDescriptor.getFileDescriptor();
                    Bitmap bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    imageView.setImageBitmap(bmp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            name.setText(userInfo.getName());
        }

        //Dialog作成
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.setTitle("ユーザー紹介").setView(layout).create();
    }
}
