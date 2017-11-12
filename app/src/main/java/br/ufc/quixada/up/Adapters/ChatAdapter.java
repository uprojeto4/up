package br.ufc.quixada.up.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.ufc.quixada.up.Models.Message;
import br.ufc.quixada.up.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private static final int CHAT_END = 1;
    private static final int CHAT_START = 2;

    private List<Message> mDataSet;

    public ChatAdapter(List<Message> dataSet) {
        mDataSet = dataSet;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if (viewType == CHAT_END) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sent, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        /*if (mDataSet.get(position).getMessageUser().equals(mId)) {
            return CHAT_END;
        }*/

        return CHAT_START;
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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) itemView.findViewById(R.id.messageText);
        }
    }
}