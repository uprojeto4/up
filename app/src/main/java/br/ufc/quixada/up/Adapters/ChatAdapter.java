package br.ufc.quixada.up.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import br.ufc.quixada.up.Models.Message;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.FirebasePreferences;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private static final int SENT_MESSAGE = 1;
    private static final int RECEIVED_MESSAGE = 2;

    private List<Message> mDataSet;
    private String mId;

    public ChatAdapter(List<Message> dataSet) {
        mDataSet = dataSet;
//        mId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if (viewType == RECEIVED_MESSAGE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sent, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
//        Log.d("maxxis", mId);
//        if (mDataSet.get(position).getMessageUserId().equals(mId)) {
            return SENT_MESSAGE;
//        } else {
//            return RECEIVED_MESSAGE;
//        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mDataSet.get(position);
        holder.mTextView.setText(message.getMessageText());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void addMessage(Message message) {
        mDataSet.add(message);
        notifyItemInserted(this.mDataSet.size());
    }

//    public void removeImage(int position) {
//        images.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, images.size());
//    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) itemView.findViewById(R.id.messageText);
        }
    }
}