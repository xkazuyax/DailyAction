package to.msn.wings.dailyaction;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by kazuya on 2018/02/24.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
    private Camera mCam;

    public CameraPreview(Context context,Camera cam) {
        super(context);
        mCam=cam;

        //SurfaceHolderの取得とCallbackの通知先
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        //CameraInstanceに画像表示先を設定
        try {
            mCam.setPreviewDisplay(holder);
            mCam.startPreview();
        } catch (IOException e) {

        }
    }

    //SurfaceView破棄
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    //SurfaceHolderが変化したときのイベント
    public void surfaceChanged(SurfaceHolder holder,int format,int width,int height) {
        ////画面回転対応の時、ここでPreviewを停止し、回転による処理を実施して、再度Previewを開始する

    }


}
