package com.upgrad.beats;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    Bitmap bitmap;
    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the Audios in a list
    private List<Audio> audioList;

    //getting the context and Audio list with constructor
    public AudioAdapter(Context mCtx, List<Audio> AudioList) {
        this.mCtx = mCtx;
        this.audioList = AudioList;
    }

    @Override
    public AudioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.music_tile, null);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AudioViewHolder holder, final int position) {
        //getting the Audio of the specified position
        Audio audio = audioList.get(position);

        //binding the data with the viewholder views
        holder.textViewTitle.setText(audio.getTitle());
        holder.textViewShortDesc.setText(audio.getArtist());
        holder.textViewRating.setText(audio.getAlbum());
        holder.textViewPrice.setText(audio.getData());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx,MusicPlayerActivity.class);
                intent.putExtra("song",position);
                v.getContext().startActivity(intent);
            }
        });
        //@android:drawable/dialog_holo_light_frame
       // holder.imageView.setImageBitmap(bitmap);

    }


    @Override
    public int getItemCount() {
        return audioList.size();
    }


    class AudioViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
        CardView cardView;
        //ImageView imageView;

        public AudioViewHolder(View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.card);

            textViewTitle = itemView.findViewById(R.id.title);
            textViewShortDesc = itemView.findViewById(R.id.artist);
            textViewPrice = itemView.findViewById(R.id.album);
            textViewRating=itemView.findViewById(R.id.textViewShortDesc);
            //imageView = itemView.findViewById(R.id.imageView);
        }
    }
}