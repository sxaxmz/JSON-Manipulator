package stackstagingcom.firstwebpage3_com.websiteranking;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;


public class MyRVA extends RecyclerView.Adapter<MyRVA.viewHolder> {

    private ArrayList<items> itemsArray;
    private onItemClickListener itemListener;

    public interface onItemClickListener {
        void onItemClick (int position);
    }

    public void setOnItemClickListener (onItemClickListener listener) {
        itemListener = listener;
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        public TextView siteName;
        public TextView visitDate;
        public TextView visitors;

        public viewHolder(View itemView, final onItemClickListener itemListener, final ArrayList<items> itemArrays) {
            super(itemView);
            siteName = itemView.findViewById(R.id.txtSiteName);
            visitDate = itemView.findViewById(R.id.txtVisitDate);
            visitors = itemView.findViewById(R.id.txtVisitors);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            itemListener.onItemClick(position);
                            items currentItem = itemArrays.get(position);
                            MainActivity.clickedSiteName = currentItem.getSiteName();
                            MainActivity.clickedVisits = currentItem.getVisiotrs();
                        }
                    }
                }
            });
        }

    }

    public MyRVA(ArrayList<items> itemsArrayList) {
        itemsArray = itemsArrayList;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent, false);
        viewHolder vh = new viewHolder(view, itemListener, itemsArray);
        return vh;
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        items currentItem = itemsArray.get(position);
        holder.siteName.setText((currentItem.getSiteName()));
        holder.visitDate.setText((currentItem.getVisitDate()));
        holder.visitors.setText((currentItem.getVisiotrs()));

        int jsonID = itemsArray.size();
        MainActivity.jsonID = jsonID;

    }

    @Override
    public int getItemCount() {
        return itemsArray.size();
    }

}


