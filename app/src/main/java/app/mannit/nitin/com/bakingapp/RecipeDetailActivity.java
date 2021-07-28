package app.mannit.nitin.com.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import app.mannit.nitin.com.bakingapp.models.Baking;
import app.mannit.nitin.com.bakingapp.models.Ingredient;
import app.mannit.nitin.com.bakingapp.models.Recipe;
import app.mannit.nitin.com.bakingapp.models.Step;
import app.mannit.nitin.com.bakingapp.widget.UpdateBakingService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An activity representing a single Recipe detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeListActivity}.
 */
public class RecipeDetailActivity extends AppCompatActivity {
    private static Parcelable mListState;
    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recipe_list)
    RecyclerView mRecyclerView;
    @Nullable
    @BindView(R.id.step_detail_container)
    FrameLayout mStepContainer;
    private boolean mTwoPane;
    private Recipe mItem;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Recipe> mRecipes;
    private List<Step> mSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (mStepContainer != null) {
            mTwoPane = true;
        }

        //This is the actual data coming from network
        mRecipes = Parcels.unwrap(getIntent().getParcelableExtra(Constants.RECIPES));

        // This code is not actually reading from JSON, this is a safety check rather. The actual data comes from intent
        if (mRecipes == null || mRecipes.size() == 0) {
            Baking baking = new Gson().fromJson(Util.loadJSONFromAsset(RecipeDetailActivity.this), Baking.class);
            mRecipes = baking.getRecipes();
        }
        //
        final int position = getIntent().getIntExtra(Constants.RECIPE_ID, 0);
        mItem = mRecipes.get(position - 1);
        if (mItem != null) {
            this.setTitle(mItem.getName());
            final List<Ingredient> ingredients = mItem.getIngredients();
            ArrayList<String> ingredientsList = new ArrayList<>();
            for (Ingredient ingredient : ingredients) {
                ingredientsList.add(String.format("%s %s %s", ingredient.getQuantity(), ingredient.getMeasure(), ingredient.getIngredient()));
            }
            UpdateBakingService.startBakingService(this, ingredientsList);
            mSteps = mItem.getSteps();
            mLinearLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
        }
    }

    @OnClick(R.id.ingredients)
    void showIngredients() {
        List<Ingredient> ingredients = mItem.getIngredients();
        if (ingredients != null && ingredients.size() > 0) {
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putParcelable(Constants.INGREDIENTS, Parcels.wrap(ingredients));
                CustomDialogFragment fragment = new CustomDialogFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.step_detail_container, fragment)
                        .commit();
            } else {
                Bundle arguments = new Bundle();
                arguments.putParcelable(Constants.INGREDIENTS, Parcels.wrap(ingredients));
                FragmentManager fragmentManager = getSupportFragmentManager();
                CustomDialogFragment newFragment = new CustomDialogFragment();
                newFragment.setArguments(arguments);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mListState = mLinearLayoutManager.onSaveInstanceState();
        outState.putParcelable(Constants.LAYOUT_MANAGER_STATE, mListState);
        outState.putParcelable(Constants.RECIPE_LIST, Parcels.wrap(mRecipes));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mListState = savedInstanceState.getParcelable(Constants.LAYOUT_MANAGER_STATE);
        mRecipes = Parcels.unwrap(savedInstanceState.getParcelable(Constants.RECIPE_LIST));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, RecipeListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mListState != null) {
            mLinearLayoutManager.onRestoreInstanceState(mListState);
            mRecyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, mSteps, mTwoPane));
        } else {
            mRecyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, mSteps, mTwoPane));
        }
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<RecipeDetailActivity.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final RecipeDetailActivity mParentActivity;
        private final List<Step> mSteps;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Step item = (Step) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(Constants.STEPS, Parcels.wrap(item));
                    StepDetailFragment fragment = new StepDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.step_detail_container, fragment)
                            .commit();
                } else {
                    Intent intent = new Intent(mParentActivity, StepActivity.class);
                    intent.putExtra(Constants.STEPS, Parcels.wrap(item));
                    mParentActivity.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(RecipeDetailActivity parent, List<Step> item, boolean twoPane) {
            mTwoPane = twoPane;
            mSteps = item;
            mParentActivity = parent;
        }

        @Override
        public RecipeDetailActivity.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_detail_layout, parent, false);
            return new RecipeDetailActivity.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecipeDetailActivity.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mIdView.setText(mSteps.get(position).getShortDescription());
            String thumbnail = mSteps.get(position).getThumbnailURL();
            if (!TextUtils.isEmpty(thumbnail)) {
                Picasso.get()
                        .load(thumbnail)
                        .error(R.drawable.place_holder)
                        .into(holder.mImageView);
            }
            holder.itemView.setTag(mSteps.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mSteps.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.text1)
            TextView mIdView;
            @BindView(R.id.imageView)
            ImageView mImageView;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
