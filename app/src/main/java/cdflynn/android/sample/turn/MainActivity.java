package cdflynn.android.sample.turn;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
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
        Spinner gravity;
        Spinner orientation;
        CheckBox rotate;
        View controlsHandle;
        View controls;

        Views(MainActivity activity) {
            root = (ViewGroup) activity.findViewById(R.id.root);
            list = (RecyclerView) activity.findViewById(R.id.recycler_view);
            radius = (SeekBar) activity.findViewById(R.id.seek_radius);
            radiusText = (TextView) activity.findViewById(R.id.radius_text);
            peek = (SeekBar) activity.findViewById(R.id.seek_peek);
            peekText = (TextView) activity.findViewById(R.id.peek_text);
            gravity = (Spinner) activity.findViewById(R.id.gravity);
            orientation = (Spinner) activity.findViewById(R.id.orientation);
            rotate = (CheckBox) activity.findViewById(R.id.rotate);
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
        final int radius = (int) getResources().getDimension(R.dimen.list_radius);
        final int peek = (int) getResources().getDimension(R.dimen.list_peek);
        layoutManager = new TurnLayoutManager(this,
                TurnLayoutManager.Gravity.START,
                TurnLayoutManager.Orientation.VERTICAL,
                radius,
                peek,
                views.rotate.isChecked());
        views.list.setLayoutManager(layoutManager);
        views.list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        views.radius.setOnSeekBarChangeListener(radiusListener);
        views.peek.setOnSeekBarChangeListener(peekListener);
        views.radius.setProgress(radius);
        views.peek.setProgress(peek);
        views.gravity.setOnItemSelectedListener(gravityOptionsClickListener);
        views.orientation.setOnItemSelectedListener(orientationOptionsClickListener);
        views.gravity.setAdapter(new GravityAdapter(this, R.layout.spinner_item));
        views.orientation.setAdapter(new OrientationAdapter(this, R.layout.spinner_item));
        views.rotate.setOnCheckedChangeListener(rotateListener);
        views.controlsHandle.setOnClickListener(controlsHandleClickListener);
    }

    private final SeekBar.OnSeekBarChangeListener radiusListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            views.radiusText.setText(getResources().getString(R.string.radius_format, progress));
            if (fromUser) {
                layoutManager.setRadius(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // do nothing
        }
    };

    private final SeekBar.OnSeekBarChangeListener peekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            views.peekText.setText(getResources().getString(R.string.peek_format, progress));
            if (fromUser) {
                layoutManager.setPeekDistance(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // do nothing
        }
    };

    private final AdapterView.OnItemSelectedListener orientationOptionsClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    layoutManager.setOrientation(TurnLayoutManager.Orientation.VERTICAL);
                    return;
                case 1:
                    layoutManager.setOrientation(TurnLayoutManager.Orientation.HORIZONTAL);
                default:
                    // do nothing
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final AdapterView.OnItemSelectedListener gravityOptionsClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    layoutManager.setGravity(TurnLayoutManager.Gravity.START);
                    return;
                case 1:
                    layoutManager.setGravity(TurnLayoutManager.Gravity.END);
                default:
                    // do nothing
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

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
        public OrientationAdapter(@NonNull Context context, @LayoutRes int resource) {
            super(context, resource, new String[]{"Vertical", "Horizontal"});
        }
    }

    private class GravityAdapter extends ArrayAdapter<String> {
        public GravityAdapter(@NonNull Context context, @LayoutRes int resource) {
            super(context, resource, new String[]{"Start", "End"});
        }
    }
}
