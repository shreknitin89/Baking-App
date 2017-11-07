package app.mannit.nitin.com.bakingapp.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import app.mannit.nitin.com.bakingapp.Constants;

import static app.mannit.nitin.com.bakingapp.Constants.INGREDIENTS_LIST;

public class UpdateBakingService extends IntentService {

    public UpdateBakingService() {
        super("UpdateBakingService");
    }

    public static void startBakingService(Context context, ArrayList<String> list) {
        Intent intent = new Intent(context, UpdateBakingService.class);
        intent.putExtra(INGREDIENTS_LIST, list);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            ArrayList<String> ingredientsList = intent.getExtras().getStringArrayList(INGREDIENTS_LIST);
            Intent newIntent = new Intent(Constants.UPDATE);
            newIntent.setAction(Constants.UPDATE);
            newIntent.putExtra(INGREDIENTS_LIST, ingredientsList);
            sendBroadcast(newIntent);
        }
    }
}
