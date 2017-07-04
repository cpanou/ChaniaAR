package tuc.christos.chaniacitywalk2.collectionActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import tuc.christos.chaniacitywalk2.MyApp;
import tuc.christos.chaniacitywalk2.R;
import tuc.christos.chaniacitywalk2.model.Scene;
import tuc.christos.chaniacitywalk2.utils.DataManager;

/**
 * An activity representing a single Scene detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link CollectionActivity}.
 */
public class SceneDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_detail);
        final String scene_id = getIntent().getStringExtra(SceneDetailFragment.ARG_ITEM_ID);
        final Scene scene = DataManager.getInstance().getScene(Long.valueOf(scene_id));

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout.LayoutParams p = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
        p.setScrollFlags(0);
        collapsingToolbar.setLayoutParams(p);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (DataManager.getInstance().getActivePlayer().hasPlaced(scene.getId())) {
            fab.setImageResource(R.drawable.ic_check_box_white_24dp);
        } else {
            fab.setImageResource(R.drawable.ic_check_box_outline_blank_white_24dp);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataManager.getInstance().getActivePlayer().hasPlaced(scene.getId())) {
                    DataManager.getInstance().clearPlace(scene.getId(), MyApp.getAppContext());
                    fab.setImageResource(R.drawable.ic_check_box_outline_blank_white_24dp);
                } else {
                    DataManager.getInstance().savePlace(scene.getId(), MyApp.getAppContext());
                    fab.setImageResource(R.drawable.ic_check_box_white_24dp);
                }
            }
        });
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView txName = (TextView) findViewById(R.id.nameTextView);
        txName.setText(scene.getName());

        if (scene.getUriThumb() != null) {
            ImageView img = (ImageView) findViewById(R.id.scene_thumb);
            Glide.with(this)
                    .load(scene.getUriThumb())
                    .placeholder(R.drawable.empty_photo)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(img);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(SceneDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(SceneDetailFragment.ARG_ITEM_ID));
            SceneDetailFragment fragment = new SceneDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.scene_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
