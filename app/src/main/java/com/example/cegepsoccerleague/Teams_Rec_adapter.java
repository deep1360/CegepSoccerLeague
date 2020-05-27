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

public class Teams_Rec_adapter extends RecyclerView.Adapter<Teams_Rec_adapter.ViewHolder>{

    private ArrayList<Teams_List_model> Teams_ArrayList;
    private Context context;
    private View.OnClickListener TeamItemListener;

    public Teams_Rec_adapter(ArrayList<Teams_List_model> Teams_ArrayList, Context context) {
        this.Teams_ArrayList = Teams_ArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public Teams_Rec_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teams_list_recycler_item,parent,false);

        return new Teams_Rec_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Teams_Rec_adapter.ViewHolder holder, int position) {

        holder.team_item_name.setText(Teams_ArrayList.get(position).getTeam_name());
        holder.team_item_manager_name.setText("Managed By: "+ Teams_ArrayList.get(position).getTeam_manager_name());
        if(!Teams_ArrayList.get(position).getTeam_icon().equals("No Icon")){
            byte[] decodedString = Base64.decode(Teams_ArrayList.get(position).getTeam_icon(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.team_item_img_view.setImageBitmap(decodedByte);
        }
    }

    public void setOnClickListener(View.OnClickListener clickListener){

        TeamItemListener = clickListener;

    }

    @Override
    public int getItemCount() {
        return Teams_ArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView team_item_name,team_item_manager_name;
        ImageView team_item_img_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            team_item_name = itemView.findViewById(R.id.team_item_name);
            team_item_manager_name = itemView.findViewById(R.id.team_item_manager_name);
            team_item_img_view = itemView.findViewById(R.id.team_item_img_view);

            itemView.setTag(this);

            itemView.setOnClickListener(TeamItemListener);

        }
    }

}
