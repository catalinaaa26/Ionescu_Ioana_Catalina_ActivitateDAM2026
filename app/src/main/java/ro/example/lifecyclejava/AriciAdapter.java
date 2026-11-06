package ro.example.lifecyclejava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AriciAdapter extends ArrayAdapter<AriciIIC> {
    private final LayoutInflater inflater;

    public AriciAdapter(Context context, List<AriciIIC> ariciList) {
        super(context, 0, ariciList);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            row = inflater.inflate(R.layout.item_arici, parent, false);
            holder = new ViewHolder(
                    row.findViewById(R.id.text_arici_name),
                    row.findViewById(R.id.text_arici_description)
            );
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        AriciIIC arici = getItem(position);
        if (arici != null) {
            holder.name.setText(arici.getNume());
            holder.description.setText(arici.toString());
        }
        return row;
    }

    private static class ViewHolder {
        final TextView name;
        final TextView description;

        ViewHolder(TextView name, TextView description) {
            this.name = name;
            this.description = description;
        }
    }
}
