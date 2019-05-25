package com.upgrad.beats;

import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
     static List<Audio> currentPlaylist;
     static ArrayList<Integer> sadList = new ArrayList<>();
     static ArrayList<Integer> happyList = new ArrayList<>();


    public static ArrayList<Integer> getSadList() {
        return sadList;
    }

    public static void setSadList(ArrayList<Integer> sadList) {
        Playlist.sadList = sadList;
    }

    public static ArrayList<Integer> getHappyList() {
        return happyList;
    }

    public static void setHappyList(ArrayList<Integer> happyList) {
        Playlist.happyList = happyList;
    }

    public Playlist() {
    }
    public Playlist(List<Audio> currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }

    public List<Audio> getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(List<Audio> currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }
}
