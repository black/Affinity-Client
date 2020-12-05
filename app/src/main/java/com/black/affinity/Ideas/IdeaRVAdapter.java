package com.black.affinity.Ideas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.black.affinity.Pojos.Ideas;
import com.black.affinity.R;
import com.black.affinity.OnItemClickListener;

import java.util.Collections;
import java.util.List;

public class IdeaRVAdapter extends RecyclerView.Adapter<IdeaRVAdapter.ContentViewHolder> {

    private OnItemClickListener onItemClickListener;
    private List<Ideas> ideasList;
    private String avatar;

    public IdeaRVAdapter(List<Ideas> teamList,String avatar) {
        this.ideasList = teamList;
        this.avatar = avatar;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view  = inflater.inflate(R.layout.idea,parent,false);
        return new ContentViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        holder.titleView.setText(ideasList.get(position).getMsg());
        holder.avatar.setText(ideasList.get(position).getAvatar());
        int pL = holder.view.getPaddingLeft();
        int pT = holder.view.getPaddingTop();
        int pR = holder.view.getPaddingRight();
        int pB = holder.view.getPaddingBottom();
        holder.view.setBackgroundResource(ideasList.get(position).getAvatar().equals(avatar)?R.drawable.rounded_ideas_mine:R.drawable.rounded_ideas_other);
        holder.view.setPadding(pL, pT, pR, pB);
    }

    @Override
    public int getItemCount() {
        return ideasList.size();
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder{
        TextView titleView,avatar;
        View view;
        public ContentViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            titleView = itemView.findViewById(R.id.ideaTitle);
            avatar = itemView.findViewById(R.id.avatar);
            view = (LinearLayout)itemView;
            itemView.setOnClickListener(view -> {
                if(listener!=null){
                    int pos = getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        listener.OnItemClick(pos);
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        onItemClickListener = clickListener;
    }
}