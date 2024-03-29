package app.mannit.nitin.com.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingResource;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import app.mannit.nitin.com.bakingapp.IdlingResource.SimpleIdlingResource;
import app.mannit.nitin.com.bakingapp.models.Baking;
import app.mannit.nitin.com.bakingapp.models.Recipe;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeListActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recipe_list)
    RecyclerView mRecyclerView;
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }


    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        Call<List<Recipe>> call = Util.loadDataFromNetwork(this);
        if (call != null) {
            call.enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                    //This is the actual data coming from network
                    List<Recipe> recipes = response.body();
                    Baking baking = new Baking();
                    baking.setRecipes(recipes);
                    setLayoutManager(baking);
                }

                @Override
                public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                    Baking baking = new Gson().fromJson(Util.loadJSONFromAsset(RecipeListActivity.this), Baking.class);
                    setLayoutManager(baking);
                }
            });
        } else {
            // This code is rather a safety check to continue the flow
            Baking baking = new Gson().fromJson(Util.loadJSONFromAsset(RecipeListActivity.this), Baking.class);
            setLayoutManager(baking);
        }
    }

    private void setLayoutManager(Baking baking) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(RecipeListActivity.this, 2));
        }
        mRecyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(RecipeListActivity.this, baking.getRecipes()));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final RecipeListActivity mParentActivity;
        private final List<Recipe> mValues;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recipe item = (Recipe) view.getTag();
                Context context = view.getContext();
                Intent intent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtra(Constants.RECIPES, Parcels.wrap(mValues));
                intent.putExtra(Constants.RECIPE_ID, item.getId());
                context.startActivity(intent);
            }
        };

        SimpleItemRecyclerViewAdapter(RecipeListActivity parent, List<Recipe> items) {
            mValues = items;
            mParentActivity = parent;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            String imagePath = mValues.get(position).getImage();
            if (imagePath == null || imagePath.isEmpty()) {
                holder.mImageView.setImageResource(R.drawable.place_holder);
            } else {
                Picasso.get()
                        .load(imagePath)
                        .error(R.drawable.place_holder)
                        .into(holder.mImageView);
            }
            holder.mIdView.setText(String.valueOf(mValues.get(position).getId()));
            holder.mContentView.setText(mValues.get(position).getName());
            holder.mServingsView.setText(String.format(mParentActivity.getString(R.string.servings), mValues.get(position).getServings()));
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.id_text)
            TextView mIdView;
            @BindView(R.id.content)
            TextView mContentView;
            @BindView(R.id.recipe_image)
            ImageView mImageView;
            @BindView(R.id.servings)
            TextView mServingsView;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
