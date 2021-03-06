package com.meow.testanime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meow.testanime.DBModels.AnimeDB;
import com.meow.testanime.DBModels.MangaDB;
import com.meow.testanime.ModelsManga.Published;
import com.meow.testanime.ModelsManga.Data;
import com.meow.testanime.ModelsManga.Images;
import com.meow.testanime.ModelsManga.Jpg;
import com.meow.testanime.TableData.DBHandlerAnime;
import com.meow.testanime.TableData.DBHandlerManga;
import com.squareup.picasso.Picasso;

public class MangaDetails extends AppCompatActivity {
    private Data data;
    private ImageView mangaposter, bookmark;
    private TextView mangatitle, mangascore, mangapublishingperiod, mangach, mangavolumes, mangadescription, learnmore, watchtext;
    private LinearLayout watchlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_details);

        DBHandlerManga db = new DBHandlerManga(MangaDetails.this);

        mangaposter = findViewById(R.id.mangaposter);
        mangatitle = findViewById(R.id.mangatitle);
        mangascore = findViewById(R.id.mangascore);
        mangapublishingperiod = findViewById(R.id.mangapublishperiod);
        mangach = findViewById(R.id.mangach);
        mangavolumes = findViewById(R.id.mangavolumes);
        mangadescription = findViewById(R.id.mangadescription);
        learnmore = findViewById(R.id.learnmore);
        watchtext = findViewById(R.id.watchtext);
        bookmark = findViewById(R.id.bookmark);
        watchlist = findViewById(R.id.watchlist);

        data = (Data) getIntent().getSerializableExtra("data");
        Images imgdata = data.getImages();
        Jpg imgjpg = imgdata.getJpg();
        if (imgjpg.getImageUrl() != null) Picasso.get().load(imgjpg.getImageUrl()).into(mangaposter);

        Published string = data.getPublished();
        mangapublishingperiod.setText(string.getString());

        if (data.getTitleEnglish() == null) mangatitle.setText(data.getTitle());
        else mangatitle.setText(data.getTitleEnglish());
        mangascore.setText(data.getScore() + "");
        mangach.setText(data.getChapters() + "");
        mangavolumes.setText(data.getVolumes() + "");
        mangadescription.setText(data.getSynopsis());

        learnmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = data.getUrl();
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent intent = builder.build();
                intent.launchUrl(MangaDetails.this, Uri.parse(url));
            }
        });

        boolean watchlistAdded = db.hasObject(mangatitle.getText().toString());
        if (!watchlistAdded) {
            watchtext.setText("Bookmark");
            bookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
        }
        else {
            watchtext.setText("Remove Bookmark");
            bookmark.setImageResource(R.drawable.ic_baseline_bookmark_24);
        }

        watchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MangaDB mangaDB = new MangaDB(mangatitle.getText().toString(), imgjpg.getImageUrl(), mangascore.getText().toString(), mangapublishingperiod.getText().toString(), mangach.getText().toString(), mangavolumes.getText().toString(), data.getUrl(), data.getSynopsis());
                if (watchtext.getText().equals("Bookmark")) {
                    db.addMangaWatchList(mangaDB);
                    watchtext.setText("Remove Bookmark");
                    bookmark.setImageResource(R.drawable.ic_baseline_bookmark_24);
                } else if (watchtext.getText().equals("Remove Bookmark")) {
                    db.deleteMangaFromWatchList(mangaDB);
                    watchtext.setText("Bookmark");
                    bookmark.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                }
            }
        });
    }
}
