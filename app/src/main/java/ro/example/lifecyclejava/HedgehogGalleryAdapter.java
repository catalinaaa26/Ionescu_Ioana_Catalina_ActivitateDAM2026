package ro.example.lifecyclejava;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HedgehogGalleryAdapter extends ArrayAdapter<HedgehogGalleryItem> {
    private static final ExecutorService IMAGE_EXECUTOR = Executors.newFixedThreadPool(3);
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    private static final LruCache<String, Bitmap> IMAGE_CACHE = new LruCache<String, Bitmap>(8 * 1024 * 1024) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            return bitmap.getByteCount();
        }
    };

    private final LayoutInflater inflater;

    public HedgehogGalleryAdapter(Context context, List<HedgehogGalleryItem> items) {
        super(context, 0, items);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            row = inflater.inflate(R.layout.item_hedgehog_gallery, parent, false);
            holder = new ViewHolder(
                    row.findViewById(R.id.image_hedgehog),
                    row.findViewById(R.id.text_gallery_title),
                    row.findViewById(R.id.text_gallery_description),
                    row.findViewById(R.id.text_gallery_credit)
            );
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        HedgehogGalleryItem item = getItem(position);
        if (item != null) {
            holder.title.setText(item.getTitle());
            holder.description.setText(item.getDescription());
            holder.credit.setText("Foto: " + item.getAttribution());
            holder.image.setContentDescription(item.getTitle());
            loadImage(item.getImageUrl(), holder.image);
        }
        return row;
    }

    private void loadImage(String imageUrl, ImageView imageView) {
        imageView.setTag(imageUrl);
        imageView.setImageResource(android.R.drawable.ic_menu_gallery);

        Bitmap cachedImage = IMAGE_CACHE.get(imageUrl);
        if (cachedImage != null) {
            imageView.setImageBitmap(cachedImage);
            return;
        }

        IMAGE_EXECUTOR.execute(() -> {
            Bitmap downloadedImage = downloadImage(imageUrl);
            if (downloadedImage == null) {
                return;
            }

            IMAGE_CACHE.put(imageUrl, downloadedImage);
            MAIN_HANDLER.post(() -> {
                if (imageUrl.equals(imageView.getTag())) {
                    imageView.setImageBitmap(downloadedImage);
                }
            });
        });
    }

    private Bitmap downloadImage(String imageUrl) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(imageUrl).openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("Accept", "image/*");
            connection.connect();

            try (InputStream inputStream = connection.getInputStream()) {
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception ignored) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static class ViewHolder {
        final ImageView image;
        final TextView title;
        final TextView description;
        final TextView credit;

        ViewHolder(ImageView image, TextView title, TextView description, TextView credit) {
            this.image = image;
            this.title = title;
            this.description = description;
            this.credit = credit;
        }
    }
}
