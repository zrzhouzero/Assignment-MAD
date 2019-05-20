package android.mad.assignment;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import model.EventImpl;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private static final String TAG = "EventListAdapter";

    private ArrayList<EventImpl> events;
    private Context context;

    public EventListAdapter(ArrayList<EventImpl> events, Context context) {
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_event_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: called.");

        viewHolder.topic.setText(events.get(i).getTitle());
        SimpleDateFormat format = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
        viewHolder.startTime.setText(format.format(events.get(i).getStartDate()));

        viewHolder.eventListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on item " + i);

                Intent showEventDetail = new Intent(v.getContext(), EventDetail.class);
                showEventDetail.putExtra("EVENT_INFO", events.get(i));
                showEventDetail.putExtra("ALL_EVENTS", events);
                v.getContext().startActivity(showEventDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView topic;
        TextView startTime;
        ConstraintLayout eventListLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            topic = itemView.findViewById(R.id.topic_event_list_item);
            startTime = itemView.findViewById(R.id.time_event_list_item);
            eventListLayout = itemView.findViewById(R.id.event_list_layout);
        }
    }

    public void removeItem(int position) {
        events.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(EventImpl item, int position) {
        events.add(position, item);
        notifyItemInserted(position);
    }

    public void sortEventByStartTime() {

    }

    public ArrayList<EventImpl> getEvents() {
        return events;
    }

}
