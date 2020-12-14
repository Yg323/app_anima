package com.example.app_anima;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyTrainAdapter extends RecyclerView.Adapter<RecyTrainAdapter.ViewHolder>{
    private ArrayList<RecyTrainItem> mData=null;
    RecyTrainAdapter(ArrayList<RecyTrainItem>list){
        mData = list;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener (OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public RecyTrainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_train, parent, false) ;
        RecyTrainAdapter.ViewHolder vh = new RecyTrainAdapter.ViewHolder(view) ;

        return vh ;
    }
    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(RecyTrainAdapter.ViewHolder holder, int position) {

        RecyTrainItem item = mData.get(position) ;
        holder.iv_trainIcon.setImageDrawable(item.getDrawable()) ;
        holder.tv_trainTitle.setText(item.getTitle()) ;
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_trainIcon ;
        TextView tv_trainTitle ;

        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
            iv_trainIcon = itemView.findViewById(R.id.iv_trainIcon) ;
            tv_trainTitle = itemView.findViewById(R.id.tv_trainTitle) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (onItemClickListener != null) onItemClickListener.onItemClick(v, pos);
                    }
                }
            });
        }
    }
}
