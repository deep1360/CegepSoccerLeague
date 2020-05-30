package com.example.cegepsoccerleague;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Schedules_Rec_adapter extends RecyclerView.Adapter<Schedules_Rec_adapter.ViewHolder>{

    private ArrayList<Schedules_List_model> Schedules_ArrayList;
    private Context context;
    private View.OnClickListener ScheduleItemListener;

    public Schedules_Rec_adapter(ArrayList<Schedules_List_model> Schedules_ArrayList, Context context) {
        this.Schedules_ArrayList = Schedules_ArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public Schedules_Rec_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedules_list_recycler_item,parent,false);

        return new Schedules_Rec_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Schedules_Rec_adapter.ViewHolder holder, int position) {

        holder.schedule_team1_item_name.setText(Schedules_ArrayList.get(position).getTeam1_name());
        holder.schedule_team2_item_name.setText(Schedules_ArrayList.get(position).getTeam2_name());
        holder.schedule_item_date.setText(Schedules_ArrayList.get(position).getMatch_date());
        holder.schedule_item_time.setText(Schedules_ArrayList.get(position).getMatch_time());

        if(!Schedules_ArrayList.get(position).getTeam1_icon().equals("No Icon")){
            byte[] decodedString = Base64.decode(Schedules_ArrayList.get(position).getTeam1_icon(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.schedule_team1_item_img_view.setImageBitmap(decodedByte);
        }

        if(!Schedules_ArrayList.get(position).getTeam2_icon().equals("No Icon")){
            byte[] decodedString = Base64.decode(Schedules_ArrayList.get(position).getTeam2_icon(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.schedule_team2_item_img_view.setImageBitmap(decodedByte);
        }
    }

    public void setOnClickListener(View.OnClickListener clickListener){

        ScheduleItemListener = clickListener;

    }

    @Override
    public int getItemCount() {
        return Schedules_ArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView schedule_item_date,schedule_item_time,schedule_team1_item_name,schedule_team2_item_name;
        ImageView schedule_team1_item_img_view,schedule_team2_item_img_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            schedule_item_date = itemView.findViewById(R.id.schedule_item_date);
            schedule_item_time = itemView.findViewById(R.id.schedule_item_time);
            schedule_team1_item_name = itemView.findViewById(R.id.schedule_team1_item_name);
            schedule_team2_item_name = itemView.findViewById(R.id.schedule_team2_item_name);
            schedule_team1_item_img_view = itemView.findViewById(R.id.schedule_team1_item_img_view);
            schedule_team2_item_img_view = itemView.findViewById(R.id.schedule_team2_item_img_view);


            itemView.setTag(this);

            itemView.setOnClickListener(ScheduleItemListener);

        }
    }

}
