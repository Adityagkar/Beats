package com.upgrad.beats;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    List<Audio> audioList,sampleList;
    AudioAdapter adapter,adapter2;
    RecyclerView recyclerView;
    DatabaseReference myRef;
    ArrayList<Integer> indexFetched;
    private String key;
    Uri uri,uri2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.playlistmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sad:
               // Toast.makeText(MainActivity.this,"Sad",Toast.LENGTH_SHORT).show();
                myRef.child(key).child("sad").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            indexFetched = (ArrayList<Integer>) dataSnapshot.getValue();
                            Object indexes[] = indexFetched.toArray();
                            sampleList = new ArrayList<>();
                            for (Object in : indexes) {
                                Long l = (Long) in;
                                sampleList.add(audioList.get(l.intValue()));
                                sampleList.get(sampleList.size()-1).setAudioType("sad");
//                                sampleList.get(l.intValue()).setAudioType("sad");
//
                            }
                            Playlist playlist = new Playlist(sampleList);
                            adapter2 = new AudioAdapter(MainActivity.this, sampleList);
                            recyclerView.swapAdapter(adapter2, true);
                            Playlist.setSadList(indexFetched);
                        }else{
                            Toast.makeText(MainActivity.this,"The playlist is empty",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });


                break;

            case R.id.happy:
               // Toast.makeText(MainActivity.this,"Happy",Toast.LENGTH_SHORT).show();
                myRef.child(key).child("happy").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            indexFetched= (ArrayList<Integer>) dataSnapshot.getValue();
                            Object indexes[]=  indexFetched.toArray();
                            sampleList = new ArrayList<>();
                            for (Object in: indexes){
                                Long l = (Long) in;
                                sampleList.add(audioList.get(l.intValue()));
                                sampleList.get(sampleList.size()-1).setAudioType("happy");
//                                sampleList.get(l.intValue()).setAudioType("happy");
//                                Playlist playlist = new Playlist(sampleList);
                            }

                            adapter2 = new AudioAdapter(MainActivity.this,sampleList);
                            Playlist.setHappyList(indexFetched);

                            Playlist playlist = new Playlist(sampleList);
                            recyclerView.swapAdapter(adapter2,true);

                        }else{
                            Toast.makeText(MainActivity.this,"The playlist is empty",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case R.id.all:

                //adding some items to our list
                loadAudio(1);
                adapter = new AudioAdapter(this, audioList);
                //setting adapter to recyclerview
                recyclerView.swapAdapter(adapter,true);


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        key= getSharedPreferences("MYSP",MODE_PRIVATE).getString("USER","anonymous")
                .replace("@","-").replace(".","-");
        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the audioList
        audioList = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance().getReference();


        //adding some items to our list
        loadAudio(1);
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if(isSDPresent){
            loadAudio(2);
        }


        adapter = new AudioAdapter(this, audioList);

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);
    }

    private void loadAudio(int i) {
        ContentResolver contentResolver = getContentResolver();

        if (i == 1) {
           uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }else if(i==2){
            uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        }

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.ALBUM_ID + " ASC";

        Cursor cursor = contentResolver.query(uri, null , selection,null, sortOrder);



        if (cursor != null && cursor.getCount() > 0) {
            audioList = new ArrayList<>();
            while (cursor.moveToNext()){
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
               // String uriImage = cursorImage.getString(cursorImage.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));

                //Log.d("TEST",uriImage);
                // Save to audioList
                audioList.add(new Audio(data, title, album, artist));

            }
        }


        //playlist.setCurrentPlaylist(audioList);
       Playlist playlist = new Playlist(audioList);
        cursor.close();
    }




}
