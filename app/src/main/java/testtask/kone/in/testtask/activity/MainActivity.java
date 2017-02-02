package testtask.kone.in.testtask.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.VideoView;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;

import testtask.kone.in.testtask.App;
import testtask.kone.in.testtask.utility.Config;
import testtask.kone.in.testtask.R;

public class MainActivity extends Activity implements CacheListener{
private VideoView videoView;
    String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        videoView = (VideoView) findViewById(R.id.videoView);
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        videoView.setLayoutParams(new FrameLayout.LayoutParams(metrics.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT));

        Log.d("Came inside","Came inside ");
        if(Build.VERSION.SDK_INT<23){
            Log.d("Came inside","Came inside if CP");
            checkCachedState();

        }
        else{
            
            if(checkPermission()){
                Log.d("Came inside","Came inside else if CP");
                checkCachedState();
            }
            else{
                Log.d("Came inside","Came inside else else CP");
                String[] perms = { Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(MainActivity.this, perms, 101);
            }
        }
    }

    private boolean checkPermission() {
        int result_internet = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET);
        int result_read = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result_write = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result_internet== PackageManager.PERMISSION_GRANTED&&result_read== PackageManager.PERMISSION_GRANTED&&result_write==PackageManager.PERMISSION_GRANTED)
        {
       return true;
        }
    return false;
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {

    }

    private void checkCachedState() {
        HttpProxyCacheServer proxy = App.getProxy(this);
        boolean fullyCached = proxy.isCached(Config.VIDEO_URL);
        setCachedState(fullyCached);
        Log.d("Fully cached state","Fully cached state "+fullyCached);
        startVideo();
    }

    private void startVideo() {
        HttpProxyCacheServer proxy = App.getProxy(this);
        proxy.registerCacheListener(this, Config.VIDEO_URL);
        String proxyUrl = proxy.getProxyUrl(Config.VIDEO_URL);
        videoView.setVideoPath(proxyUrl);
        videoView.start();
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED&&grantResults[2]== PackageManager.PERMISSION_GRANTED) {
                    Log.d("Came inside","Came inside else CP onRquest");
                    checkCachedState();
                }}
    }
                    private void setCachedState(boolean cached) {
      //  int statusIconId = cached ? R.drawable.ic_cloud_done : R.drawable.ic_cloud_download;

    }
}
