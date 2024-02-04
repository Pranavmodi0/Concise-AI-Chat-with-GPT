package com.ai.concise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ai.concise.ads.Admob
import com.ai.concise.databinding.ActivitySettingBinding
import com.google.android.gms.ads.FullScreenContentCallback

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadbanner()

        binding.back.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.terms.setOnClickListener {
            startActivity(Intent(this, TermsActivity::class.java))
        }

        binding.privacy.setOnClickListener {
            startActivity(Intent(this, PrivacyActivity::class.java))
        }
    }

    private fun loadbanner() {

        if (Admob.mInterstitialAd != null) {
            Admob.mInterstitialAd.show(this@SettingActivity)
            Admob.mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    Admob.mInterstitialAd = null
                    Admob.loadInter(this@SettingActivity)
                }
            }
        } else {

        }
    }
}