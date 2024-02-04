package com.ai.concise.adapters;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.concise.R;
import com.ai.concise.models.Message;

import java.util.List;

public class MessageAda extends RecyclerView.Adapter<MessageAda.MyViewHolder> {

    Context context;
    List<Message> messageList;

    public MessageAda(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,null);
        MyViewHolder myViewHolder = new MyViewHolder(chatView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Message message = messageList.get(position);
        if(message.getSentBy().equals(Message.SENT_BY_ME)){
            holder.leftChatView.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
            holder.copy.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.rightTextView.setText(message.getMessage());
        }else{
            holder.rightChatView.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.copy.setVisibility(View.VISIBLE);
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.leftTextView.setText(message.getMessage());
        }

        holder.textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.textToSpeech.speak(""+ holder.leftTextView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = (ClipData) ClipData.newPlainText("text", messageList.get(position).getMessage());
                clipboardManager.setPrimaryClip(data);

                Toast.makeText(context, "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout leftChatView,rightChatView;
        TextView leftTextView,rightTextView;
        TextToSpeech textToSpeech;
        ImageView imageView, copy;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatView  = itemView.findViewById(R.id.left_chat_view);
            rightChatView = itemView.findViewById(R.id.right_chat_view);
            leftTextView = itemView.findViewById(R.id.left_chat_text_view);
            rightTextView = itemView.findViewById(R.id.right_chat_text_view);
            imageView = itemView.findViewById(R.id.voice);
            copy = itemView.findViewById(R.id.copy);
        }
    }
}
