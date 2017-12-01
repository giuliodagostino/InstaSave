package photodownload.video.save;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tushar on 8/21/2017.
 */

public class ClipService extends Service {
    ClipboardManager cm;

    @Override
    public void onCreate() {
        Log.d("ONCREATE: ","onCreate called..");
        super.onCreate();
        cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.addPrimaryClipChangedListener(new ClipboardListener());
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        cm.removePrimaryClipChangedListener(new ClipboardListener());

    }
    class ClipboardListener implements ClipboardManager.OnPrimaryClipChangedListener{

        @Override
        public void onPrimaryClipChanged() {
            Log.d("CLIP CHANGED: ","onPrimaryClipChanged called..");
            if(cm!=null) {
                String s1 = cm.getPrimaryClip().getItemAt(0).coerceToText(ClipService.this).toString();
                Log.d("url",s1);
                Pattern p = Pattern.compile("instagram.com");
                Matcher m = p.matcher(s1);//replace with string to compare
                if (m.find()) {
                    Log.d("Yes: ","success");
                    PackageManager pm = getPackageManager();
                    Intent intent = pm.getLaunchIntentForPackage("photodownload.video.save");
                    startActivity(intent);
                }else {
                    Log.d("No: ","unsuccessful");
                }
            }
        }
    }
}
