package com.ai.concise

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ai.concise.adapters.MessageAdapter
import com.ai.concise.ads.Admob
import com.ai.concise.ads.Admob.rewardedAd
import com.ai.concise.api.ApiUtilits
import com.ai.concise.databinding.ActivityImageBinding
import com.ai.concise.models.MessageModel
import com.ai.concise.models.request.ImageGenerateRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.util.*

class ImageActivity : AppCompatActivity() {

    var list = ArrayList<MessageModel>()
    private lateinit var mLayoutManager : LinearLayoutManager
    private lateinit var adapter : MessageAdapter

    var counter = 1

    private val Speech = 102

    private lateinit var binding: ActivityImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadbanner()

        Admob.loadReward(this@ImageActivity)


        binding.imageCounter.text = "1"

        binding.imageShow.visibility = View.GONE

        mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.stackFromEnd = true
        adapter = MessageAdapter(list)
        binding.imageRecycle.adapter = adapter
        binding.imageRecycle.layoutManager = mLayoutManager

        binding.imageBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.imageSend.setOnClickListener {

            if (binding.imageMessage.text!!.isEmpty()) {

                Toast.makeText(this, "Please ask question", Toast.LENGTH_SHORT).show()

            }else if (counter > 0) {

                counter--
                binding.imageCounter.setText(counter.toString())

                callApi()

            } else {

                binding.imageShow.visibility = View.VISIBLE

                binding.imageCounter.text = "0"

            }
        }

        binding.imageAds.setOnClickListener {

            loadrewardedad()

            binding.imageShow.visibility = View.GONE

            callApi()
        }

        binding.imageVoice.setOnClickListener {

            val permission = Manifest.permission.RECORD_AUDIO

            permissionLauncherAudio.launch(permission)

        }
    }

    private fun loadbanner() {

        if (Admob.mInterstitialAd != null) {
            Admob.mInterstitialAd.show(this@ImageActivity)
            Admob.mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    Admob.mInterstitialAd = null
                    Admob.loadInter(this@ImageActivity)
                }
            }
        } else {

        }
    }

    private val permissionLauncherAudio = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
        ) { isGranted ->
        Log.d(TAG, "onActivityResult : isGranted: $isGranted")
        if (isGranted) {
            speak()
        } else {
            Log.d(TAG, "onActivityResult: Permission denied...")
            Toast.makeText(this@ImageActivity, "Permission denied...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Speech && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding.imageMessage.setText(result!![0])
        }
    }

    private fun speak() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech recognition is not available", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something")
            startActivityForResult(intent, Speech)
        }
    }

    private fun loadrewardedad() {

        rewardedAd?.let { ad ->
            ad.show(this, OnUserEarnedRewardListener { rewardItem ->
                // Handle the reward.
                val rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type
                Log.d(TAG, "User earned the reward.")
            })
        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
        }
    }

    private fun callApi() {

        list.add(MessageModel(true, false, binding.imageMessage.text.toString()))

        adapter.notifyItemInserted(list.size - 1)

        binding.imageRecycle.recycledViewPool.clear()
        binding.imageRecycle.smoothScrollToPosition(list.size-1)

        val apiInterface = ApiUtilits.getApiInterface()

        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            Gson().toJson(
                ImageGenerateRequest(
                    1,
                    binding.imageMessage.text.toString(),
                    "1024x1024"
                )
            )

        )

        val contentType = "application/json"
        val authorization = "${Utils.API_KEY}"

        lifecycleScope.launch(Dispatchers.IO) {

            try {

                 val response = apiInterface.generateImage(
                    contentType, authorization, requestBody

                 )

                val textResponse = response.data.first().url

                list.add(MessageModel(false, true, textResponse))
                withContext(Dispatchers.Main){
                    adapter.notifyItemInserted(list.size - 1)

                    binding.imageRecycle.recycledViewPool.clear()
                    binding.imageRecycle.smoothScrollToPosition(list.size - 1)

                }

                binding.imageMessage.text!!.clear()

            } catch (e : Exception) {
                withContext(Dispatchers.Main){
                    Toast.makeText(this@ImageActivity, e.message, Toast.LENGTH_SHORT).show()

                }

            }
        }
    }
}