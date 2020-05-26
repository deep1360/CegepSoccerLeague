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

public class Leagues_Rec_adapter extends RecyclerView.Adapter<Leagues_Rec_adapter.ViewHolder>{

    private ArrayList<Leagues_List_model> Leagues_ArrayList;
    private Context context;
    private View.OnClickListener LeagueItemListener;

    public Leagues_Rec_adapter(ArrayList<Leagues_List_model> Leagues_ArrayList, Context context) {
        this.Leagues_ArrayList = Leagues_ArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public Leagues_Rec_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leagues_list_recycler_item,parent,false);

        return new Leagues_Rec_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Leagues_Rec_adapter.ViewHolder holder, int position) {

        holder.league_item_name.setText(Leagues_ArrayList.get(position).getLeague_name());
        if(!Leagues_ArrayList.get(position).getLeague_icon().equals("No Icon")){
            byte[] decodedString = Base64.decode(Leagues_ArrayList.get(position).getLeague_icon(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.league_item_img_view.setImageBitmap(decodedByte);
        }
    }

    public void setOnClickListener(View.OnClickListener clickListener){

        LeagueItemListener = clickListener;

    }

    @Override
    public int getItemCount() {
        return Leagues_ArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView league_item_name;
        ImageView league_item_img_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            league_item_name = itemView.findViewById(R.id.league_item_name);
            league_item_img_view = itemView.findViewById(R.id.league_item_img_view);


            itemView.setTag(this);

            itemView.setOnClickListener(LeagueItemListener);

        }
    }

}
