package photodownload.video.save;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Tushar on 8/21/2017.
 */

public class DownloadBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            //Show a notification
            Toast.makeText(context, "Download Complete!", Toast.LENGTH_SHORT).show();

        }
    }
}
