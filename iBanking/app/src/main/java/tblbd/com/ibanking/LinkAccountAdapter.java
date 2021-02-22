package tblbd.com.ibanking;

import android.app.LauncherActivity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class LinkAccountAdapter extends RecyclerView.Adapter<LinkAccountAdapter.ViewHolder> {
    private  List<ListItemLinkAccount> listItems;
    private Context context;

    public LinkAccountAdapter(List<ListItemLinkAccount> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public LinkAccountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate
                (R.layout.list_item_link_account,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkAccountAdapter.ViewHolder viewHolder, int i) {
        ListItemLinkAccount listItem=listItems.get(i);

        viewHolder.tAc.setText(listItem.getAccountNumber());
        viewHolder.tAcName.setText(listItem.getAccounName());
        viewHolder.tAcType.setText(listItem.getAccountType());

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tAc,tAcName,tAcType;
        public ViewHolder(View itemView)
        {
            super(itemView);
            tAc=itemView.findViewById(R.id.txtViewAcnumber);
            tAcName=itemView.findViewById(R.id.txtViewAcname);
            tAcType=itemView.findViewById(R.id.txtViewActype);
        }
    }
}
