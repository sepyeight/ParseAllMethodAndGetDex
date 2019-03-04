package com.example.parseallmethodandgetdex;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import static com.example.parseallmethodandgetdex.ConstantValue.TAG;

public class AppAdapter extends RecyclerView.Adapter {
    private List<Appinfo> appinfos;
    private int selected = -1;
    private Context context;

    private OnItemClickLitener mOnItemClickLitener;

    public AppAdapter(List<Appinfo> appinfos, Context context) {
        this.appinfos = appinfos;
        this.context = context;
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public void setSelected(int position) {
        this.selected = position;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof SingleViewHolder) {
            final SingleViewHolder viewHolder = (SingleViewHolder) holder;
            String appname = appinfos.get(position).getAppName();
            String apppackage = appinfos.get(position).getAppPackage();
            Drawable appicon = appinfos.get(position).getIcon();
            viewHolder.iv_icon.setImageDrawable(appicon);
            viewHolder.tv_appname.setText(appname);
            viewHolder.tv_apppackage.setText(apppackage);

            if (mOnItemClickLitener != null) {
                viewHolder.cb_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickLitener.onItemClick(viewHolder.itemView, viewHolder.getAdapterPosition());
                        Log.d(TAG, "onClick: item " + viewHolder.tv_apppackage.getText().toString() + " is selected.");
                        Intent intent = new Intent(context, AppDetalActivity.class);
                        Appinfo appinfo = appinfos.get(position);
                        intent.putExtra("data", appinfo);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
            }
        }
    }


    @Override
    public int getItemCount() {
        return appinfos.size();
    }

    public void setSelection(int selection) {
        this.selected = selection;
        notifyDataSetChanged();
    }

    class SingleViewHolder extends RecyclerView.ViewHolder {
        TextView tv_appname;
        TextView tv_apppackage;
        ImageView iv_icon;
        LinearLayout cb_select;

        public SingleViewHolder(View itemView) {
            super(itemView);
            tv_appname = itemView.findViewById(R.id.tv_appname);
            tv_apppackage = itemView.findViewById(R.id.tv_apppackage);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            cb_select = itemView.findViewById(R.id.cb_select);
        }
    }
}