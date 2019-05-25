package com.upgrad.beats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import at.markushi.ui.CircleButton;


import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MusicPlayerActivity extends AppCompatActivity {

    private CircleButton play,stop,shuffle,next,previous,repeat,sad,happy;
    private TextView currentDuration, totalDuration, title, album, artist;
    private  MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Handler mHandler = new Handler();
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private List<Audio> audioList;
    private Utilities utils;
    Drawable drawable;
    private ImageView albumArt;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        play=findViewById(R.id.play);
        stop=findViewById(R.id.stop);
        shuffle=findViewById(R.id.shuffle);
        next=findViewById(R.id.next);
        previous=findViewById(R.id.previous);
        repeat=findViewById(R.id.repeat);
        title = findViewById(R.id.songname);
        album = findViewById(R.id.albumname);
        artist = findViewById(R.id.artistname);
        currentDuration=findViewById(R.id.curr);
        totalDuration=findViewById(R.id.totaldur);
        seekBar=findViewById(R.id.seekBar);
        mediaPlayer = new MediaPlayer();
        utils = new Utilities();
        sad=findViewById(R.id.sad);
        happy=findViewById(R.id.happy);
        audioList = new Playlist().getCurrentPlaylist();
        albumArt = findViewById(R.id.imageView3);

        key= getSharedPreferences("MYSP",MODE_PRIVATE).getString("USER","anonymous")
                .replace("@","-").replace(".","-");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        //retreive music from prev activity
        Intent intent = getIntent();
        currentSongIndex = intent.getExtras().getInt("song");
        Log.d("TEST",currentSongIndex+"");
        // play selected song
        playSong(currentSongIndex);


        drawable= getDrawable(R.drawable.ill);


        sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                happy.setVisibility(View.GONE);
                sad.setVisibility(View.GONE);
                Toast.makeText(MusicPlayerActivity.this,"Seems a Sad Song !",Toast.LENGTH_LONG).show();
                Playlist.getSadList().add(currentSongIndex);
                //audioList.get(currentSongIndex).setAudioType("sad");
                myRef.child(key).child("sad").setValue(Playlist.getSadList());
            }
        });

        happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sad.setVisibility(View.GONE);
                happy.setVisibility(View.GONE);
                Toast.makeText(MusicPlayerActivity.this,"Seems an Happy Song !",Toast.LENGTH_LONG).show();
               // audioList.get(currentSongIndex).setAudioType("happy");
                Playlist.getHappyList().add(currentSongIndex);
                myRef.child(key).child("happy").setValue(Playlist.getHappyList());
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // check for repeat is ON or OFF
                if(isRepeat){
                    // repeat is on play same song again
                    playSong(currentSongIndex);
                } else if(isShuffle){
                    // shuffle is on - play a random song
                    Random rand = new Random();
                    currentSongIndex = rand.nextInt((audioList.size() - 1) - 0 + 1) + 0;
                    playSong(currentSongIndex);
                } else{
                    // no repeat or shuffle ON - play next song
                    if(currentSongIndex < (audioList.size() - 1)){
                        playSong(currentSongIndex + 1);
                        currentSongIndex = currentSongIndex + 1;
                    }else{
                        // play first song
                        playSong(0);
                        currentSongIndex = 0;
                    }
                }

            }
        });

        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(timeUpdateTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(timeUpdateTask);
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
                // forward or backward to certain seconds
                Log.d("TEST","current"+currentPosition);
                mediaPlayer.seekTo(currentPosition,MediaPlayer.SEEK_CLOSEST_SYNC);
                // update timer progress again
                updateProgressBar();

            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if next song is there or not
                if(currentSongIndex < (audioList.size() - 1)){
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                }else{
                    playSong(0);
                    currentSongIndex = 0;
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(currentSongIndex > 0){
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                }else{
                    // play last song
                    playSong(audioList.size() - 1);
                    currentSongIndex = audioList.size() - 1;
                }

            }
        });

        play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if(mediaPlayer.isPlaying()){
                    if(mediaPlayer!=null){
                        mediaPlayer.pause();
                        // Changing button image to play button
                        play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    }
                }else{
                    // Resume song
                    if(mediaPlayer!=null){
                        mediaPlayer.start();
                        // Changing button image to pause button
                        play.setImageResource(R.drawable.ic_pause_black_24dp);
                    }
                }

            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    repeat.setImageResource(R.drawable.ic_replay_black_24dp);
                }else{
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    repeat.setImageResource(R.drawable.ic_replay_focus_24dp);
                    shuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
                }
            }
        });


        shuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    shuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
                }else{
                    // make repeat to true
                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    shuffle.setImageResource(R.drawable.ic_shuffle_focus_24dp);
                    repeat.setImageResource(R.drawable.ic_replay_black_24dp);
                }
            }
        });



    }

    private void getImage(int currentSongIndex) {
        //for getting a song's image
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums.ALBUM+ "=?",
                new String[] {String.valueOf(audioList.get(currentSongIndex).getAlbum())},
                null);

        if (cursor.moveToFirst()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            //if the path is null
            String newpath = path+"path";
            if(!newpath.equals("nullpath")){
                albumArt.setImageBitmap(BitmapFactory.decodeFile(path));
            }else{
                albumArt.setImageDrawable(drawable);
            }

            Log.d("TEST","path"+path);
            // do whatever you need to do
        }

    }

    private void playSong(int currentSongIndex) {

        if(Playlist.currentPlaylist.get(currentSongIndex).getAudioType().equals("all")){
            happy.setVisibility(View.VISIBLE);
            sad.setVisibility(View.VISIBLE);
        }else{
            happy.setVisibility(View.GONE);
            sad.setVisibility(View.GONE);
        }
        // Play song
        try {getImage(currentSongIndex);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioList.get(currentSongIndex).getData());
            mediaPlayer.prepare();
            mediaPlayer.start();
            // Displaying Song title
            String songTitle = audioList.get(currentSongIndex).getTitle();
            title.setText(songTitle);
            album.setText(audioList.get(currentSongIndex).getAlbum());
            artist.setText(audioList.get(currentSongIndex).getArtist());

            // Changing Button Image to pause image
            play.setImageResource(R.drawable.ic_pause_black_24dp);

            // set Progress bar values
            seekBar.setProgress(0);
            seekBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateProgressBar() {
        mHandler.postDelayed(timeUpdateTask, 100);
    }
    private Runnable timeUpdateTask = new Runnable() {
        public void run() {
            long totalDurationInt = mediaPlayer.getDuration();
            long currentDurationInt = mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time

            totalDuration.setText(""+utils.milliSecondsToTimer(totalDurationInt));
            // Displaying time completed playing
            currentDuration.setText(""+utils.milliSecondsToTimer(currentDurationInt));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDurationInt, totalDurationInt));

            seekBar.setProgress(progress);

            mHandler.postDelayed(this, 100);
        }
    };



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mHandler.removeCallbacks(timeUpdateTask);
    }


}
