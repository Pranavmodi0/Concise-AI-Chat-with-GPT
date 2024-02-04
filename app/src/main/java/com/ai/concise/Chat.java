package com.ai.concise;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.concise.adapters.MessageAda;
import com.ai.concise.ads.Admob;
import com.ai.concise.models.Message;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Chat extends AppCompatActivity {
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    RecyclerView recyclerView;
    EditText messageEditText;
    ImageView sendButton, back, voiceAssist;
    List<Message> messageList;
    MessageAda messageAdapter;
    TextView counterView;
    LinearLayout ads;
    Button showAds;

    int counter = 2;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    private static final String TAG = "PERMISSION TAG";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageList = new ArrayList<>();
        loadBannerAd();

        Admob.loadReward(Chat.this);

        recyclerView = findViewById(R.id.chat_recycle);
        messageEditText = findViewById(R.id.chat_message);
        sendButton = findViewById(R.id.chat_send);
        back = findViewById(R.id.chat_back);
        voiceAssist = findViewById(R.id.chat_voice);
        counterView = findViewById(R.id.chat_counter);
        ads = findViewById(R.id.chat_show);
        showAds = findViewById(R.id.chat_ads);
        ads.setVisibility(View.GONE);

        messageAdapter = new MessageAda(Chat.this, messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        counterView.setText("2");

        sendButton.setOnClickListener((v)->{
            if (messageEditText.getText().toString().isEmpty()) {

                Toast.makeText(Chat.this, "Please ask question", Toast.LENGTH_SHORT).show();

            } else if (counter>0) {
                counter--;

                counterView.setText(String.valueOf(counter));
                String question = messageEditText.getText().toString().trim();
                addToChat(question, Message.SENT_BY_ME);
                messageEditText.setText("");
                callAPI(question);
            } else {

                ads.setVisibility(View.VISIBLE);

                counterView.setText("0");
            }
        });

        showAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadrewardedad();

                ads.setVisibility(View.GONE);

                String question = messageEditText.getText().toString().trim();
                addToChat(question,Message.SENT_BY_ME);
                messageEditText.setText("");
                callAPI(question);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chat.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        voiceAssist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String permission = Manifest.permission.RECORD_AUDIO;

                permissionLauncherAudio.launch(permission);
            }
        });

    }

    private void loadrewardedad() {

        if (Admob.rewardedAd != null) {

            Activity activityContext = Chat.this;

            Admob.rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();

                }
            });
        } else {

        }
    }

    private ActivityResultLauncher<String> permissionLauncherAudio = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {

                    Log.d(TAG, "onActivityResult : isGranted: "+isGranted);

                    if (isGranted) {
                        speak();
                    }
                    else {

                        Log.d(TAG, "onActivityResult: Permission denied...");
                        Toast.makeText(Chat.this, "Permission denied...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void speak() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something");

        try {

            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }
        catch (Exception e) {

            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    messageEditText.setText(result.get(0));
                }
                break;
            }
        }

    }

    private void loadBannerAd() {

        if (Admob.mInterstitialAd != null) {
            Admob.mInterstitialAd.show(Chat.this);

            Admob.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    Admob.mInterstitialAd = null;
                    Admob.loadInter(Chat.this);

                }
            });
        } else {

        }

    }

    void addToChat(String message,String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response){
        messageList.remove(messageList.size()-1);
        addToChat(response,Message.SENT_BY_BOT);
    }

    static {
        System.loadLibrary("native-lib");
    }

    void callAPI(String question){
        messageList.add(new Message("Typing... ",Message.SENT_BY_BOT));

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model","gpt-3.5-turbo");

            JSONArray messageArr = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("role","user");
            obj.put("content",question);
            messageArr.put(obj);

            jsonBody.put("messages",messageArr);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);

        Request request = new Request.Builder()
                .url("\n" +
                        "https://api.openai.com/v1/chat/completions")
                .header("Authorization",Utils.INSTANCE.getAPI_KEY())
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response please try again");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    JSONObject  jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else{
                    addResponse("Failed to load response please try again");
                }
            }
        });
    }
}
