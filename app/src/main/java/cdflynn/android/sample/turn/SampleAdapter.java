package cdflynn.android.sample.turn;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.SampleViewHolder> {

    private final LayoutInflater layoutInflater;

    public SampleAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public SampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView sampleView = (TextView) layoutInflater.inflate(R.layout.view_sample, parent, false);
        return new SampleViewHolder(sampleView);
    }

    @Override
    public void onBindViewHolder(SampleViewHolder holder, int position) {
        holder.tv.setText(Integer.toString(position));
    }

    @Override
    public int getItemCount() {
        return 31;
    }

    class SampleViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public SampleViewHolder(View itemView) {
            super(itemView);
            this.tv = (TextView) itemView;
        }
    }
}
