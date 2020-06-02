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

public class Scoreboards_Rec_adapter extends RecyclerView.Adapter<Scoreboards_Rec_adapter.ViewHolder>{

    private ArrayList<Scoreboards_List_model> Scoreboards_ArrayList;
    private Context context;
    private View.OnClickListener ScoreboardItemListener;

    public Scoreboards_Rec_adapter(ArrayList<Scoreboards_List_model> Scoreboards_ArrayList, Context context) {
        this.Scoreboards_ArrayList = Scoreboards_ArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public Scoreboards_Rec_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scoreboards_list_recycler_item,parent,false);

        return new Scoreboards_Rec_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Scoreboards_Rec_adapter.ViewHolder holder, int position) {

        holder.scoreboard_item_date.setText(Scoreboards_ArrayList.get(position).getMatch_date());

        holder.sb_team1_item_name.setText(Scoreboards_ArrayList.get(position).getTeam1_name());
        holder.sb_team1_item_goal_txt.setText(Scoreboards_ArrayList.get(position).getTeam1_goals());
        if(!Scoreboards_ArrayList.get(position).getTeam1_icon().equals("No Icon")){
            byte[] decodedString = Base64.decode(Scoreboards_ArrayList.get(position).getTeam1_icon(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.sb_team1_item_img_view.setImageBitmap(decodedByte);
        }

        holder.sb_team2_item_name.setText(Scoreboards_ArrayList.get(position).getTeam2_name());
        holder.sb_team2_item_goal_txt.setText(Scoreboards_ArrayList.get(position).getTeam2_goals());
        if(!Scoreboards_ArrayList.get(position).getTeam2_icon().equals("No Icon")){
            byte[] decodedString = Base64.decode(Scoreboards_ArrayList.get(position).getTeam2_icon(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.sb_team2_item_img_view.setImageBitmap(decodedByte);
        }

        int team1_goals = Integer.parseInt(Scoreboards_ArrayList.get(position).getTeam1_goals());
        int team2_goals = Integer.parseInt(Scoreboards_ArrayList.get(position).getTeam2_goals());

        if(team1_goals>team2_goals){
            holder.sb_team1_item_win_icon.setVisibility(View.VISIBLE);
            holder.sb_team1_item_bg.setVisibility(View.VISIBLE);
        }
        else {
            holder.sb_team2_item_win_icon.setVisibility(View.VISIBLE);
            holder.sb_team2_item_bg.setVisibility(View.VISIBLE);
        }

    }

    public void setOnClickListener(View.OnClickListener clickListener){

        ScoreboardItemListener = clickListener;

    }

    @Override
    public int getItemCount() {
        return Scoreboards_ArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView scoreboard_item_date,
                sb_team1_item_goal_txt,sb_team1_item_name,
                sb_team2_item_name,sb_team2_item_goal_txt;
        ImageView sb_team1_item_img_view, sb_team1_item_win_icon,
                sb_team2_item_img_view, sb_team2_item_win_icon;
        View sb_team1_item_bg,sb_team2_item_bg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            scoreboard_item_date = itemView.findViewById(R.id.scoreboard_item_date);

            sb_team1_item_goal_txt = itemView.findViewById(R.id.sb_team1_item_goal_txt);
            sb_team1_item_name = itemView.findViewById(R.id.sb_team1_item_name);
            sb_team1_item_img_view = itemView.findViewById(R.id.sb_team1_item_img_view);
            sb_team1_item_bg = itemView.findViewById(R.id.sb_team1_item_bg);
            sb_team1_item_win_icon = itemView.findViewById(R.id.sb_team1_item_win_icon);

            sb_team2_item_goal_txt = itemView.findViewById(R.id.sb_team2_item_goal_txt);
            sb_team2_item_name = itemView.findViewById(R.id.sb_team2_item_name);
            sb_team2_item_img_view = itemView.findViewById(R.id.sb_team2_item_img_view);
            sb_team2_item_bg = itemView.findViewById(R.id.sb_team2_item_bg);
            sb_team2_item_win_icon = itemView.findViewById(R.id.sb_team2_item_win_icon);

            itemView.setTag(this);

            itemView.setOnClickListener(ScoreboardItemListener);

        }
    }

}
