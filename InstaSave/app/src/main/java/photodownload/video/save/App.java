package photodownload.video.save;

import android.app.Application;
import android.content.Intent;

/**
 * Created by Tushar on 8/21/2017.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, ClipService.class));
    }
}
