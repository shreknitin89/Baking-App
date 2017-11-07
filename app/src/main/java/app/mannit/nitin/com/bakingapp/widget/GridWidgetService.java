package app.mannit.nitin.com.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import app.mannit.nitin.com.bakingapp.R;

import static app.mannit.nitin.com.bakingapp.widget.BakingWidgetProvider.ingredientsList;

public class GridWidgetService extends RemoteViewsService {
    List<String> ingredients;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }

    class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        Context mContext = null;

        GridRemoteViewsFactory(Context context) {
            mContext = context;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            ingredients = ingredientsList;
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return ingredients != null ? ingredients.size() : 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_grid_view_item);
            views.setTextViewText(R.id.widget_grid_view_item, ingredients.get(position));
            Intent fillInIntent = new Intent();
            views.setOnClickFillInIntent(R.id.widget_grid_view_item, fillInIntent);
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}

