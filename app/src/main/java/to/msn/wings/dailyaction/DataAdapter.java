package to.msn.wings.dailyaction;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by kazuya on 2018/02/28.
 */

public class DataAdapter extends RealmBaseAdapter<DailyAction> {
    public static class ViewHolder {
        TextView date;
        TextView title;
        ImageView picture;
    }

    public DataAdapter(@Nullable OrderedRealmCollection<DailyAction> data) {
        super(data);
    }

    @Nullable
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_adapter,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.date = (TextView) convertView.findViewById(R.id.list_date);
            viewHolder.title = (TextView) convertView.findViewById(R.id.list_subject);
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DailyAction dailyAction = adapterData.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String formatDate = sdf.format(dailyAction.getDate());
        viewHolder.date.setText(formatDate);
        viewHolder.title.setText(dailyAction.getSubject());
        File file = new File(dailyAction.getPath());
        try{
            InputStream is = new FileInputStream(file);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            viewHolder.picture.setImageBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertView;
    }

}
