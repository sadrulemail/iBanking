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

public class LinkAccountListAdapter extends RecyclerView.Adapter<LinkAccountListAdapter.ViewHolder> {
    private  List<ListItemLinkAccountList> listItems;
    private Context context;

    public LinkAccountListAdapter(List<ListItemLinkAccountList> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public LinkAccountListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate
                (R.layout.list_item_account_list,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkAccountListAdapter.ViewHolder viewHolder, int i) {
        ListItemLinkAccountList listItem=listItems.get(i);

        viewHolder.tAc.setText(listItem.getAccountNumber());
        viewHolder.tAcName.setText(listItem.getAccounName());
        viewHolder.tAcType.setText(listItem.getAccountType());
        viewHolder.tBalabce.setText(listItem.getBalance());

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tAc,tAcName,tAcType,tBalabce;
        public ViewHolder(View itemView)
        {
            super(itemView);
            tAc=itemView.findViewById(R.id.txtViewAcnumber);
            tAcName=itemView.findViewById(R.id.txtViewAcname);
            tAcType=itemView.findViewById(R.id.txtViewActype);
            tBalabce=itemView.findViewById(R.id.txtViewBalance);
        }
    }
}
