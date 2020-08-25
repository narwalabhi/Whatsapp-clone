package com.narwal.whatsappclone;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.narwal.whatsappclone.model.User;
import com.squareup.picasso.Picasso;

public class UserViewHolder extends RecyclerView.ViewHolder {
    static final String UID = "userId";
    static final String NAME = "userName";
    static final String IMAGE = "thumbImage";
    ShapeableImageView ivUser;
    private TextView tvCount;
    private TextView tvTime;
    private TextView tvTitle;
    private TextView tvSubtitle;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        tvCount = itemView.findViewById(R.id.tvCount);
        tvTime = itemView.findViewById(R.id.tvTime);
        tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        ivUser = itemView.findViewById(R.id.userImgView);
    }

    public void bind(final User user, final Context mContext) {
        tvCount.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
        tvTitle.setText(user.getName());
        tvSubtitle.setText(user.getStatus());
        Picasso.get()
                .load(user.getThumbImgUrl())
                .placeholder(R.drawable.defaultavatar)
                .error(R.drawable.defaultavatar)
                .into(ivUser);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra(IMAGE, user.getThumbImgUrl());
                intent.putExtra(UID, user.getUid());
                intent.putExtra(NAME, user.getName());
                mContext.startActivity(intent);
            }
        });
    }
}
