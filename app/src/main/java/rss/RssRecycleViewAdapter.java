package rss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus_user.labs.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

public class RssRecycleViewAdapter extends Adapter<RssRecycleViewAdapter.RssViewHolder> {
    public class RssViewHolder extends RecyclerView.ViewHolder{

        TextView sometext;
        public RssViewHolder(@NonNull View itemView) {
            super(itemView);
            sometext = itemView.findViewById(R.id.rss_card_text_view);
        }
    }

    private Context context;
    private LayoutInflater layoutInflater;
    private List<RssNote> notes;

    public RssRecycleViewAdapter(Context context, List<RssNote> notes) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.notes = notes;
    }


    @NonNull
    @Override
    public RssViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.rss_list_item, parent, false);
        return new RssViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RssViewHolder holder, int position) {
        RssNote note = notes.get(position);
        holder.sometext.setText(note.getTitle());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
