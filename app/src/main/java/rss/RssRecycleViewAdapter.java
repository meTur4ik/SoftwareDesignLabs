package rss;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus_user.labs.MainActivity;
import com.example.asus_user.labs.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import glide.GlideApp;
import layout.WebPreviewFragment;

public class RssRecycleViewAdapter extends Adapter<RssRecycleViewAdapter.RssViewHolder> {
    public class RssViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView description;
        ImageView image;
        public RssViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.rss_card_text_view);
            description = itemView.findViewById(R.id.rss_card_description_text_view);
            image = itemView.findViewById(R.id.rss_card_image_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    String myMessage = "https://vk.com";
                    bundle.putString("message", myMessage );
                    WebPreviewFragment fragInfo = new WebPreviewFragment();
                    fragInfo.setArguments(bundle);

                    FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, fragInfo);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    /*NavController controller = ((NavHostFragment) context.getSupportFragmentManager()
                            .findFragmentById(R.id.nav_host_fragment))
                            .getNavController();

                    controller.navigate(R.id.rssFragment);*/
                }
            });
        }
    }

    private MainActivity context;
    private LayoutInflater layoutInflater;
    private List<RssNote> notes;

    public RssRecycleViewAdapter(MainActivity context, List<RssNote> notes) {
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
        holder.title.setText(note.getTitle());
        holder.description.setText(note.getDescription());
        GlideApp.with(holder.itemView.getContext())
                .asBitmap()
                .load(note.getImageUri())
                .override(1280, 800)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        try {
            return notes.size();
        } catch (NullPointerException e) {
            return 0;
        }

    }
}
