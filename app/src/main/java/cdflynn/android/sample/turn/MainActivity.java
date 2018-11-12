package cdflynn.android.sample.turn;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import cdflynn.android.library.turn.TurnLayoutManager;

public class MainActivity extends AppCompatActivity {

    static class Views {
        ViewGroup root;
        RecyclerView list;
        SeekBar radius;
        TextView radiusText;
        SeekBar peek;
        TextView peekText;
        SeekBar minScale;
        TextView minScaleText;
        SeekBar maxScale;
        TextView maxScaleText;
        SeekBar minAlpha;
        TextView minAlphaText;
        SeekBar maxAlpha;
        TextView maxAlphaText;
        Spinner gravity;
        Spinner orientation;
        CheckBox rotate;
        View controlsHandle;
        View controls;

        Views(MainActivity activity) {
            root = activity.findViewById(R.id.root);
            list = activity.findViewById(R.id.recycler_view);
            list.setClipToPadding(false);
            radius = activity.findViewById(R.id.seek_radius);
            radiusText = activity.findViewById(R.id.radius_text);
            peek = activity.findViewById(R.id.seek_peek);
            peekText = activity.findViewById(R.id.peek_text);
            minScale = activity.findViewById(R.id.min_scale);
            minScaleText = activity.findViewById(R.id.min_scale_text);
            maxScale = activity.findViewById(R.id.max_scale);
            maxScaleText = activity.findViewById(R.id.max_scale_text);
            minAlpha = activity.findViewById(R.id.min_alpha);
            minAlphaText = activity.findViewById(R.id.min_alpha_text);
            maxAlpha = activity.findViewById(R.id.max_alpha);
            maxAlphaText = activity.findViewById(R.id.max_alpha_text);
            gravity = activity.findViewById(R.id.gravity);
            orientation = activity.findViewById(R.id.orientation);
            rotate = activity.findViewById(R.id.rotate);
            controlsHandle = activity.findViewById(R.id.control_handle);
            controls = activity.findViewById(R.id.control_panel);
        }
    }

    private Views views;
    private TurnLayoutManager layoutManager;
    private SampleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        views = new Views(this);
        adapter = new SampleAdapter(this);
        final int size = Math.max(getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().widthPixels);
        final int radius = getRadius(size);
        final int peek = getPeek(size);
        final int minScale = getResources().getInteger(R.integer.list_min_scale);
        final int maxScale = getResources().getInteger(R.integer.list_max_scale);
        final int minAlpha = getResources().getInteger(R.integer.list_min_alpha);
        final int maxAlpha = getResources().getInteger(R.integer.list_max_alpha);
        layoutManager = new TurnLayoutManager.Builder(this)
                .setRadius(radius)
                .setPeekDistance(peek)
                .setGravity(TurnLayoutManager.Gravity.START)
                .setOrientation(TurnLayoutManager.Orientation.VERTICAL)
                .setRotate(views.rotate.isChecked())
                .build();
        views.list.setLayoutManager(layoutManager);
        new LinearSnapHelper().attachToRecyclerView(views.list);
        views.list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        views.radius.setOnSeekBarChangeListener(radiusListener);
        views.peek.setOnSeekBarChangeListener(peekListener);
        views.maxScale.setOnSeekBarChangeListener(maxScaleListener);
        views.minScale.setOnSeekBarChangeListener(minScaleListener);
        views.maxAlpha.setOnSeekBarChangeListener(maxAlphaListener);
        views.minAlpha.setOnSeekBarChangeListener(minAlphaListener);
        views.radius.setProgress(radius);
        views.peek.setProgress(peek);
        views.maxScale.setProgress(maxScale);
        views.minScale.setProgress(minScale);
        views.maxAlpha.setProgress(maxAlpha);
        views.minAlpha.setProgress(minAlpha);
        views.gravity.setOnItemSelectedListener(gravityOptionsClickListener);
        views.orientation.setOnItemSelectedListener(orientationOptionsClickListener);
        views.gravity.setAdapter(new GravityAdapter(this, R.layout.spinner_item));
        views.orientation.setAdapter(new OrientationAdapter(this, R.layout.spinner_item));
        views.rotate.setOnCheckedChangeListener(rotateListener);
        views.controlsHandle.setOnClickListener(controlsHandleClickListener);
    }

    private int getPeek(int size) {
        return Math.round(size * 0.15f);
    }

    private int getRadius(int size) {
        float halfChordLength = size / 2f;
        float chordHeight = getPeek(size);
        float radius = (float) ((Math.pow(chordHeight, 2) +
                Math.pow(halfChordLength, 2)) / (2f * chordHeight));
        return Math.round(radius);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle) {
            item.setIcon(views.controls.getTranslationY() == 0 ?
                    R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
            views.controlsHandle.performClick();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private final SeekBarChangeAdapter radiusListener = new SeekBarChangeAdapter() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            views.radiusText.setText(getResources().getString(R.string.radius_format, progress));
            if (fromUser) {
                layoutManager.setRadius(progress);
            }
        }
    };

    private final SeekBarChangeAdapter peekListener = new SeekBarChangeAdapter() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            views.peekText.setText(getResources().getString(R.string.peek_format, progress));
            if (fromUser) {
                layoutManager.setPeekDistance(progress);
            }
        }
    };

    private final SeekBarChangeAdapter minScaleListener = new SeekBarChangeAdapter() {
        private Integer factor;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (factor == null)
                factor = getResources().getInteger(R.integer.list_scale_factor);
            int maxProgress = views.maxScale.getProgress();
            if (progress > maxProgress) {
                progress = maxProgress;
                views.minScale.setProgress(progress);
            }
            float scale = progress / (float) factor;
            views.minScaleText.setText(getResources()
                    .getString(R.string.min_scale_format, scale));
            if (fromUser) {
                layoutManager.setMinScale(scale);
            }
        }
    };

    private final SeekBarChangeAdapter maxScaleListener = new SeekBarChangeAdapter() {
        private Integer factor;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (factor == null)
                factor = getResources().getInteger(R.integer.list_scale_factor);
            int minProgress = views.minScale.getProgress();
            if (progress < minProgress) {
                progress = minProgress;
                views.maxScale.setProgress(progress);
            }
            float scale = progress / (float) factor;
            views.maxScaleText.setText(getResources()
                    .getString(R.string.max_scale_format, scale));
            if (fromUser) {
                layoutManager.setMaxScale(scale);
            }
        }
    };

    private final SeekBar.OnSeekBarChangeListener minAlphaListener = new SeekBarChangeAdapter() {
        private Integer factor;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (factor == null)
                factor = getResources().getInteger(R.integer.list_alpha_factor);
            int maxProgress = views.maxAlpha.getProgress();
            if (progress > maxProgress) {
                progress = maxProgress;
                views.minAlpha.setProgress(progress);
            }
            float alpha = progress / (float) factor;
            views.minAlphaText.setText(getResources()
                    .getString(R.string.min_alpha_format, alpha));
            if (fromUser) {
                layoutManager.setMinAlpha(alpha);
            }
        }
    };

    private final SeekBarChangeAdapter maxAlphaListener = new SeekBarChangeAdapter() {
        private Integer factor;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (factor == null)
                factor = getResources().getInteger(R.integer.list_alpha_factor);
            int minProgress = views.minAlpha.getProgress();
            if (progress < minProgress) {
                progress = minProgress;
                views.maxAlpha.setProgress(progress);
            }
            float alpha = progress / (float) factor;
            views.maxAlphaText.setText(getResources()
                    .getString(R.string.max_alpha_format, alpha));
            if (fromUser) {
                layoutManager.setMaxAlpha(alpha);
            }
        }
    };

    private final AdapterItemSelected orientationOptionsClickListener = new AdapterItemSelected() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    layoutManager.setOrientation(TurnLayoutManager.Orientation.VERTICAL);
                    break;
                case 1:
                    layoutManager.setOrientation(TurnLayoutManager.Orientation.HORIZONTAL);
                    break;
                default:
                    break;
            }
        }
    };

    private final AdapterItemSelected gravityOptionsClickListener = new AdapterItemSelected() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    layoutManager.setGravity(TurnLayoutManager.Gravity.START);
                    break;
                case 1:
                    layoutManager.setGravity(TurnLayoutManager.Gravity.END);
                    break;
                default:
                    break;
            }
        }
    };

    private final CompoundButton.OnCheckedChangeListener rotateListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            layoutManager.setRotate(isChecked);
        }
    };

    private final View.OnClickListener controlsHandleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final float translationY = views.controls.getTranslationY() == 0 ? views.controls.getHeight() : 0;
            views.controls.animate().translationY(translationY).start();
            views.controlsHandle.animate().translationY(translationY).start();
        }
    };

    private class OrientationAdapter extends ArrayAdapter<String> {
        OrientationAdapter(@NonNull Context context, @LayoutRes int resource) {
            super(context, resource, new String[]{"Vertical", "Horizontal"});
        }
    }

    private class GravityAdapter extends ArrayAdapter<String> {
        GravityAdapter(@NonNull Context context, @LayoutRes int resource) {
            super(context, resource, new String[]{"Start", "End"});
        }
    }

    private class SeekBarChangeAdapter implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // do nothing
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // do nothing
        }
    }

    private class AdapterItemSelected implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // do nothing
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // do nothing
        }
    }
}
