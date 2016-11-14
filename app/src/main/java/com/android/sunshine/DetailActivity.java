package com.android.sunshine;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.activity_pattern, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share, menu);
        inflater.inflate(R.menu.show_on_map, menu);

        ((ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.menu_share)))
                .setShareIntent(new Intent(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_TEXT, getIntent()
                                .getSerializableExtra("forecast data") + " " + BuildConfig.SUNSHINE_HASHTAG)
                        .setType("text/plain"));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_on_map:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String location = sharedPreferences.getString(
                        getString(R.string.pref_location_key),
                        getString(R.string.pref_location_default));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("geo:0,0?").buildUpon()
                        .appendQueryParameter("q", location)
                        .build());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.v("Show on map", "Could not open " + location + " on a map!");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class DetailFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            return inflater.inflate(R.layout.fragment_detail, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            ((TextView) view.findViewById(R.id.fragment_detail_text))
                    .setText((String) getActivity().getIntent().getSerializableExtra("forecast data"));
        }
    }
}
