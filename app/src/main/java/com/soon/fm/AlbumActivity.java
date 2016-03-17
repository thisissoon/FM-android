package com.soon.fm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.soon.fm.spotify.api.model.AlbumListItem;

public class AlbumActivity extends AppCompatActivity {

    private AlbumListItem album;
    private TextView albumName;
    private TextView artistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        albumName = (TextView) findViewById(R.id.album_name);
        artistName = (TextView) findViewById(R.id.artist_name);

        album = (AlbumListItem) getIntent().getExtras().getSerializable(Constant.EXTRA_STARTING_ALBUM_POSITION);
    }

    private void prepopulateView(AlbumListItem album) {
        albumName.setText(album.getTitle());
        artistName.setText(album.getSubTitle());
    }

}
