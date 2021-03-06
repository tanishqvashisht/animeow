package com.meow.testanime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meow.testanime.DBModels.AnimeDB;
import com.meow.testanime.ModelsAnime.Aired;
import com.meow.testanime.ModelsAnime.Data;
import com.meow.testanime.ModelsAnime.Images;
import com.meow.testanime.ModelsAnime.Jpg;
import com.meow.testanime.ModelsAnime.Trailer;
import com.meow.testanime.TableData.DBHandlerAnime;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AnimeDetails extends AppCompatActivity {

    private Data data;
    private ImageView animeposter, bookmark;
    private TextView animetitle, animescore, animeairingperiod, animeep, animeag, animedescription, learnmore, trailer, watchtext;
    LinearLayout watchlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_details);

        animeposter = findViewById(R.id.animeposter);
        animetitle = findViewById(R.id.animetitle);
        animescore = findViewById(R.id.animescore);
        animeairingperiod = findViewById(R.id.animeairingperiod);
        animeep = findViewById(R.id.animeep);
        animeag = findViewById(R.id.animeag);
        animedescription = findViewById(R.id.animedescription);
        learnmore = findViewById(R.id.learnmore);
        trailer = findViewById(R.id.trailer);
        watchlist = findViewById(R.id.watchlist);
        bookmark = findViewById(R.id.bookmark);
        watchtext = findViewById(R.id.watchtext);
        DBHandlerAnime db = new DBHandlerAnime(AnimeDetails.this);

        data = (Data) getIntent().getSerializableExtra("data");
        Images imgdata = data.getImages();
        Jpg imgjpg = imgdata.getJpg();
        if (imgjpg.getImageUrl() != null) Picasso.get().load(imgjpg.getImageUrl()).into(animeposter);

        Aired string = data.getAired();
        animeairingperiod.setText(string.getString());

        if (data.getTitleEnglish() == null) animetitle.setText(data.getTitle());
        else animetitle.setText(data.getTitleEnglish());
        animescore.setText(data.getScore() + "");
        animeep.setText(data.getEpisodes() + "");
        animeag.setText(data.getRating());
        animedescription.setText(data.getSynopsis());

        learnmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = data.getUrl();
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent intent = builder.build();
                intent.launchUrl(AnimeDetails.this, Uri.parse(url));
            }
        });

        Trailer trailerurl = data.getTrailer();
        String yturl = trailerurl.getUrl();
        trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent intent = builder.build();
                intent.launchUrl(AnimeDetails.this, Uri.parse(yturl));
            }
        });

        boolean watchlistAdded = db.hasObject(animetitle.getText().toString());
        if (!watchlistAdded) {
            watchtext.setText("Add To WatchList");
            bookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
        }
        else {
            watchtext.setText("Remove From WatchList");
            bookmark.setImageResource(R.drawable.ic_baseline_bookmark_24);
        }

        watchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimeDB animeDB = new AnimeDB(animetitle.getText().toString(), imgjpg.getImageUrl(), animescore.getText().toString(), animeairingperiod.getText().toString(), animeep.getText().toString(), animeag.getText().toString(), data.getUrl(), yturl, data.getSynopsis());
                if (watchtext.getText().equals("Add To WatchList")) {
                    db.addAnimeWatchList(animeDB);
                    watchtext.setText("Remove From WatchList");
                    bookmark.setImageResource(R.drawable.ic_baseline_bookmark_24);
                } else if (watchtext.getText().equals("Remove From WatchList")) {
                    db.deleteAnimeFromWatchList(animeDB);
                    watchtext.setText("Add To WatchList");
                    bookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                }
            }
        });
    }
}