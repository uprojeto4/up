package br.ufc.quixada.up.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufc.quixada.up.Models.Constant;
import br.ufc.quixada.up.Models.Message;
import br.ufc.quixada.up.R;
import br.ufc.quixada.up.Utils.DateTimeControl;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<Message> mDataSet;
    private String userId;

    public ChatAdapter(String userId) {
        this.mDataSet = new ArrayList<Message>();
        this.userId = userId;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received, parent, false);
        if (viewType == Constant.RECEIVED_CONTIGUOUS_MESSAGE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received_contiguous, parent, false);
        } else if (viewType == Constant.SENT_MESSAGE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sent, parent, false);
        } else if (viewType == Constant.SENT_CONTIGUOUS_MESSAGE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sent_contiguous, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataSet.get(position).getUserId().equals(userId)) {
            return Constant.SENT_MESSAGE;
        } else {
            return Constant.RECEIVED_MESSAGE;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mDataSet.get(position);
        holder.messageTextView.setText(message.getText());
        holder.timestampTextView.setText(DateTimeControl.generateChatTimestamp(message.getDateTime()));
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

        TextView messageTextView;
        TextView timestampTextView;

        ViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageText);
            timestampTextView = (TextView) itemView.findViewById(R.id.messageTimestamp);
        }
    }
}