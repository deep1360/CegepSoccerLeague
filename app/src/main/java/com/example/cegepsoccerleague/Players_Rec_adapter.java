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

public class Players_Rec_adapter extends RecyclerView.Adapter<Players_Rec_adapter.ViewHolder>{

    private ArrayList<Players_List_model> Players_ArrayList;
    private Context context;
    private View.OnClickListener PlayerItemListener;

    public Players_Rec_adapter(ArrayList<Players_List_model> Players_ArrayList, Context context) {
        this.Players_ArrayList = Players_ArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public Players_Rec_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.players_list_recycler_item,parent,false);

        return new Players_Rec_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Players_Rec_adapter.ViewHolder holder, int position) {

        holder.player_item_name.setText(Players_ArrayList.get(position).getPlayer_first_name()+" "+Players_ArrayList.get(position).getPlayer_last_name());
        holder.player_item_position.setText("Position: "+Players_ArrayList.get(position).getPlayer_position());
        holder.player_item_age.setText("Age: "+Players_ArrayList.get(position).getPlaer_age());

        if(!Players_ArrayList.get(position).getPlayer_icon().equals("No Icon")){
            byte[] decodedString = Base64.decode(Players_ArrayList.get(position).getPlayer_icon(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.player_item_img_view.setImageBitmap(decodedByte);
        }
    }

    public void setOnClickListener(View.OnClickListener clickListener){

        PlayerItemListener = clickListener;

    }

    @Override
    public int getItemCount() {
        return Players_ArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView player_item_name, player_item_age, player_item_position;
        ImageView player_item_img_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            player_item_name = itemView.findViewById(R.id.player_item_name);
            player_item_age = itemView.findViewById(R.id.player_item_age);
            player_item_position = itemView.findViewById(R.id.player_item_position);
            player_item_img_view = itemView.findViewById(R.id.player_item_img_view);

            itemView.setTag(this);

            itemView.setOnClickListener(PlayerItemListener);

        }
    }

}
