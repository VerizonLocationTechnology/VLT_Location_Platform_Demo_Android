package com.verizon.location.platformdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context context;
    private static final int CARD_SIZE = 8;
    private String[] titles = new String[CARD_SIZE];
    private String[] summaries = new String[CARD_SIZE];

    public RecyclerAdapter(Context context) {
        this.context = context;
        initializeData();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemTitle;
        TextView itemSummary;
        LinearLayout rootView;

        ViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.card_title);
            itemSummary = itemView.findViewById(R.id.card_subtitle);
            rootView = itemView.findViewById(R.id.root_view);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                switch(position) {
                    case 1:
                        context.startActivity(new Intent(context, MapCameraDemo.class));
                        break;
                    case 2:
                        context.startActivity(new Intent(context, MapModeDemo.class));
                        break;
                    case 3:
                        context.startActivity(new Intent(context, UserLocationDemo.class));
                        break;
                    case 4:
                        context.startActivity(new Intent(context, MapShapesDemo.class));
                        break;
                    case 5:
                        context.startActivity(new Intent(context, MapTrafficDemo.class));
                        break;
                    case 6:
                        context.startActivity(new Intent(context, MapGesturesDemo.class));
                        break;
                    case 7:
                        context.startActivity(new Intent(context, GeojsonDemo.class));
                        break;
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.itemTitle.setText(titles[i]);
        viewHolder.itemSummary.setText(summaries[i]);

        if (i == 0) {
            viewHolder.itemTitle.setTextColor(Color.WHITE);
            viewHolder.itemSummary.setTextColor(Color.WHITE);
            viewHolder.rootView.setBackgroundColor(Color.BLACK);
        } else {
            viewHolder.itemTitle.setTextColor(Color.BLACK);
            viewHolder.itemSummary.setTextColor(Color.BLACK);
            viewHolder.rootView.setBackgroundColor(Color.WHITE);
        }

    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    private void initializeData() {
        titles = new String[] {
                context.getResources().getString(R.string.welcome_label),
                context.getResources().getString(R.string.title_activity_map_camera_demo),
                context.getResources().getString(R.string.title_activity_map_mode_demo),
                context.getResources().getString(R.string.title_activity_user_location_demo),
                context.getResources().getString(R.string.title_activity_map_shapes_demo),
                context.getResources().getString(R.string.title_activity_map_traffic),
                context.getResources().getString(R.string.title_activity_map_gestures),
                context.getResources().getString(R.string.title_activity_geojson_demo)
        };

        summaries = new String[] {
                context.getResources().getString(R.string.welcome_header_label),
                context.getResources().getString(R.string.camera_header_label),
                context.getResources().getString(R.string.mode_header_label),
                context.getResources().getString(R.string.user_location_header_label),
                context.getResources().getString(R.string.shapes_header_label),
                context.getResources().getString(R.string.traffic_header_label),
                context.getResources().getString(R.string.gestures_header_label),
                context.getResources().getString(R.string.geojson_header_label)
        };
    }
}
