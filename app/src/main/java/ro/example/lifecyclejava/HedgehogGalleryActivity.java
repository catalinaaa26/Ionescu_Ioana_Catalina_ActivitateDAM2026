package ro.example.lifecyclejava;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

public class HedgehogGalleryActivity extends LifecycleLoggingActivity {
    private List<HedgehogGalleryItem> galleryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hedgehog_gallery);

        galleryItems = createGalleryItems();
        ListView galleryList = findViewById(R.id.list_hedgehog_gallery);
        galleryList.setAdapter(new HedgehogGalleryAdapter(this, galleryItems));
        galleryList.setOnItemClickListener((parent, view, position, id) ->
                openDetails(galleryItems.get(position)));
    }

    private void openDetails(HedgehogGalleryItem item) {
        Intent intent = new Intent(this, HedgehogDetailsActivity.class);
        intent.putExtra(HedgehogDetailsActivity.EXTRA_DETAILS_URL, item.getDetailsUrl());
        intent.putExtra(HedgehogDetailsActivity.EXTRA_PAGE_TITLE, item.getTitle());
        startActivity(intent);
    }

    private List<HedgehogGalleryItem> createGalleryItems() {
        return Arrays.asList(
                new HedgehogGalleryItem(
                        "Arici european în prim-plan",
                        "Vezi cum arată ariciul european și cum îl protejează spinii.",
                        "https://thumb.wikimedia.org/wikipedia/commons/thumb/7/72/European_hedgehog_%2854312785908%29.jpg/960px-European_hedgehog_%2854312785908%29.jpg",
                        "https://commons.wikimedia.org/wiki/File:European_hedgehog_(54312785908).jpg",
                        "Andrey Gulivanov · CC BY 2.0",
                        "https://creativecommons.org/licenses/by/2.0/",
                        "https://kids.nationalgeographic.com/animals/mammals/facts/hedgehog"
                ),
                new HedgehogGalleryItem(
                        "Arici în frunziș",
                        "Aricii folosesc grădini, margini de pădure și garduri vii ca habitat.",
                        "https://thumb.wikimedia.org/wikipedia/commons/thumb/5/50/Hedgehog-06.jpg/960px-Hedgehog-06.jpg",
                        "https://commons.wikimedia.org/wiki/File:Hedgehog-06.jpg",
                        "Saichetan57 · CC BY-SA 4.0",
                        "https://creativecommons.org/licenses/by-sa/4.0/",
                        "https://www.woodlandtrust.org.uk/trees-woods-and-wildlife/animals/mammals/hedgehog/"
                ),
                new HedgehogGalleryItem(
                        "Arici în pădure",
                        "Află mai multe despre viața aricilor și felul în care îi putem ajuta.",
                        "https://thumb.wikimedia.org/wikipedia/commons/thumb/f/f6/West_European_Hedgehog.jpg/960px-West_European_Hedgehog.jpg",
                        "https://commons.wikimedia.org/wiki/File:West_European_Hedgehog.jpg",
                        "Hrald · CC BY-SA 3.0",
                        "https://creativecommons.org/licenses/by-sa/3.0/",
                        "https://www.wwf.org.uk/learn/fascinating-facts/hedgehogs"
                ),
                new HedgehogGalleryItem(
                        "Arici european",
                        "Aricii sunt activi mai ales noaptea și caută hrană în vegetație.",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/6/60/European_hedgehog_01.jpg/960px-European_hedgehog_01.jpg",
                        "https://commons.wikimedia.org/wiki/File:European_hedgehog_01.jpg",
                        "Լիլիթ85 · CC BY-SA 4.0",
                        "https://creativecommons.org/licenses/by-sa/4.0/",
                        "https://www.britishhedgehogs.org.uk/faq/"
                ),
                new HedgehogGalleryItem(
                        "Ariciul din parcurile urbane",
                        "Parcurile și grădinile pot fi spații importante pentru aricii din oraș.",
                        "https://thumb.wikimedia.org/wikipedia/commons/thumb/4/44/Hedgehog_01.jpg/960px-Hedgehog_01.jpg",
                        "https://commons.wikimedia.org/wiki/File:Hedgehog_01.jpg",
                        "Coyau · CC BY-SA 3.0",
                        "https://creativecommons.org/licenses/by-sa/3.0/",
                        "https://www.royalparks.org.uk/read-watch-listen/hidden-stories-royal-parks"
                )
        );
    }
}
