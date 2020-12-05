package com.black.affinity.Teams;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.black.affinity.OnItemClickListener;
import com.black.affinity.Pojos.Invite;
import com.black.affinity.R;

import java.util.List;

public class TeamRVAdapter extends RecyclerView.Adapter<TeamRVAdapter.ContentViewHolder> {

    private OnItemClickListener onItemClickListener;
    private List<Invite> teamList;

    public TeamRVAdapter(List<Invite> teamList) {
        this.teamList = teamList;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view  = inflater.inflate(R.layout.team,parent,false);
        return new ContentViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        holder.titleView.setText(teamList.get(position).getInfo().get("name"));

        int pL = holder.view.getPaddingLeft();
        int pT = holder.view.getPaddingTop();
        int pR = holder.view.getPaddingRight();
        int pB = holder.view.getPaddingBottom();
        holder.view.setBackgroundResource(teamList.get(position).getStatus().equals("pending")?R.drawable.rounded_pending:R.drawable.rounded_accepted);
        holder.view.setPadding(pL, pT, pR, pB);

    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder{
        TextView titleView;
        View view;
        public ContentViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            titleView = itemView.findViewById(R.id.teamName);
            view = itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int pos = getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            listener.OnItemClick(pos);
                        }
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        onItemClickListener = clickListener;
    }
}