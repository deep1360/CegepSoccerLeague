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

public class Select_Match_Rec_adapter extends RecyclerView.Adapter<Select_Match_Rec_adapter.ViewHolder>{

    private ArrayList<Schedules_List_model> Schedules_ArrayList;
    private Context context;
    private View.OnClickListener SMItemListener;

    public Select_Match_Rec_adapter(ArrayList<Schedules_List_model> Schedules_ArrayList, Context context) {
        this.Schedules_ArrayList = Schedules_ArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public Select_Match_Rec_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.played_match_list_recycler_item,parent,false);

        return new Select_Match_Rec_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Select_Match_Rec_adapter.ViewHolder holder, int position) {

        holder.pm_team1_item_name.setText(Schedules_ArrayList.get(position).getTeam1_name());
        holder.pm_team2_item_name.setText(Schedules_ArrayList.get(position).getTeam2_name());
        holder.pm_item_date.setText(Schedules_ArrayList.get(position).getMatch_date()+" "+Schedules_ArrayList.get(position).getMatch_time());

        if(!Schedules_ArrayList.get(position).getTeam1_icon().equals("No Icon")){
            byte[] decodedString = Base64.decode(Schedules_ArrayList.get(position).getTeam1_icon(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.pm_team1_item_img_view.setImageBitmap(decodedByte);
        }

        if(!Schedules_ArrayList.get(position).getTeam2_icon().equals("No Icon")){
            byte[] decodedString = Base64.decode(Schedules_ArrayList.get(position).getTeam2_icon(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.pm_team2_item_img_view.setImageBitmap(decodedByte);
        }
    }

    public void setOnClickListener(View.OnClickListener clickListener){

        SMItemListener = clickListener;

    }

    @Override
    public int getItemCount() {
        return Schedules_ArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView pm_item_date,pm_team1_item_name,pm_team2_item_name;
        ImageView pm_team1_item_img_view,pm_team2_item_img_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pm_item_date = itemView.findViewById(R.id.pm_item_date);
            pm_team1_item_name = itemView.findViewById(R.id.pm_team1_item_name);
            pm_team2_item_name = itemView.findViewById(R.id.pm_team2_item_name);
            pm_team1_item_img_view = itemView.findViewById(R.id.pm_team1_item_img_view);
            pm_team2_item_img_view = itemView.findViewById(R.id.pm_team2_item_img_view);


            itemView.setTag(this);

            itemView.setOnClickListener(SMItemListener);

        }
    }

}
