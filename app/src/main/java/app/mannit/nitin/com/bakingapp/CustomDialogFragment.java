package app.mannit.nitin.com.bakingapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.List;

import app.mannit.nitin.com.bakingapp.models.Ingredient;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomDialogFragment extends DialogFragment {

    private static final String TAG = "CustomDialogFragment";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.ingredients_list)
    ListView mListView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.custom_dialog, container, false);
        ButterKnife.bind(this, rootView);

        mToolbar.setTitle(R.string.list_of_ingredients);

        assert getActivity() != null;
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
        if (getArguments() != null && getArguments().containsKey(Constants.INGREDIENTS)) {
            List<Ingredient> ingredients = Parcels.unwrap(getArguments().getParcelable(Constants.INGREDIENTS));
            IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(this.getContext(), ingredients);
            mListView.setAdapter(ingredientsAdapter);
        }
        setHasOptionsMenu(true);
        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // handle close button click here
            dismiss();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class IngredientsAdapter extends ArrayAdapter<Ingredient> {
        IngredientsAdapter(Context context, List<Ingredient> ingredients) {
            super(context, 0, ingredients);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            Ingredient ingredient = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            }
            TextView quantity = convertView.findViewById(android.R.id.text1);
            TextView item = convertView.findViewById(android.R.id.text2);
            if (ingredient != null) {
                quantity.setText(String.format("%s %s", ingredient.getQuantity(), ingredient.getMeasure()));
                item.setText(ingredient.getIngredient());
            }
            return convertView;
        }
    }
}