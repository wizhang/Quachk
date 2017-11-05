package net.quachk.quachk.Adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.quachk.quachk.Models.PublicPlayer;
import net.quachk.quachk.R;

import java.util.ArrayList;
import java.util.List;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder>{
    private List<PublicPlayer> _list;

    public PlayerListAdapter(List<PublicPlayer> list){
        this._list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindResult(getData().get(position));
        holder.setValues();
    }

    @Override
    public int getItemCount() {
        return getData().size();
    }

    public List<PublicPlayer> getData(){
        return this._list;
    }

    public void updateData(List<PublicPlayer> newData){
        this._list.clear();
        this._list.addAll(newData);
        notifyDataSetChanged();
    }

    /**
     * A class that holds the view for each recyclerview item
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView text;

        private PublicPlayer _item;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            text = itemView.findViewById(R.id.text);
        }

        /**
         * Binds the PublicPlayer in this view with an item in the adapter data list
         * @param item The Public Player
         */
        public void bindResult(PublicPlayer item){
            this._item = item;
        }

        /**
         * Updates the view of this item based on the PublicPlayer
         */
        public void setValues(){
            text.setText(this._item.getUsername());
        }
        @Override
        public void onClick(View view) {
            //TODO Handle Clicks
        }
    }
}
