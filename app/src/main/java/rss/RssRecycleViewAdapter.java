package rss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus_user.labs.MainActivity;
import com.example.asus_user.labs.R;
import com.example.asus_user.labs.WebViewActivity;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import functions.Utility;
import glide.GlideApp;
import layout.WebPreviewFragment;

public class RssRecycleViewAdapter extends Adapter<RssRecycleViewAdapter.RssViewHolder> {
    public class RssViewHolder extends RecyclerView.ViewHolder{

        TextView pubDate;
        TextView title;
        TextView description;
        ImageView image;
        //String link;
        int notePosittion;

        public RssViewHolder(@NonNull View itemView) {
            super(itemView);
            notePosittion = 0;
            title = itemView.findViewById(R.id.rss_card_text_view);
            description = itemView.findViewById(R.id.rss_card_description_text_view);
            image = itemView.findViewById(R.id.rss_card_image_view);
            pubDate = itemView.findViewById(R.id.rss_card_pub_date);
        }

        public void setOnClickLink(final String link){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utility.isNetworkAvailable(context)) {
                        Intent intent = new Intent(context, WebViewActivity.class);
                        intent.putExtra("link", link);
                        Log.i("GOT LINK", link);
                        context.startActivity(intent);
                    }
                    else {
                        Toast.makeText(context, "No network connectionn available", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        public int getNotePosittion() {
            return notePosittion;
        }

        public void setNotePosittion(int notePosittion) {
            this.notePosittion = notePosittion;
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
        //String pubDate = note.getPubDate().replace(",", "");
        /*DateTime dateTime = DateTime.parse(note.getPubDate(), DateTimeFormat.forPattern("E, d MMM yyyy HH:mm:ss Z"));
        holder.pubDate.setText(dateTime.getDayOfMonth()+ "-" + dateTime.getMonthOfYear() + "-" +
        dateTime.getYear() + ", " + dateTime.getHourOfDay() + ":" + dateTime.getMinuteOfHour() + ":" + dateTime.getSecondOfMinute());*/
        holder.pubDate.setText(note.getPubDate());
        holder.title.setText(note.getTitle());
        //Log.i("GOT TITLE", note.getTitle());
        holder.description.setText(note.getDescription());
        //Log.i("GOT LINK", note.toString());
        holder.setOnClickLink(note.getLink());
        if(note.getImageUri() != null) {
            GlideApp.with(holder.itemView.getContext())
                    .load(note.getImageUri())
                    .into(holder.image);
        }

        //holder.image.setImageResource(R.drawable.noimage);
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
