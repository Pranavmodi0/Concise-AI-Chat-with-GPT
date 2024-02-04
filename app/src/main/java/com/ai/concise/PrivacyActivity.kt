package com.ai.concise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ai.concise.adapters.RulesAdapter
import com.ai.concise.databinding.ActivityPrivacyBinding
import com.ai.concise.models.RulesModel
import com.google.firebase.firestore.FirebaseFirestore

class PrivacyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var rulesModel: ArrayList<RulesModel>
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.privacy_rec)
        recyclerView.layoutManager = LinearLayoutManager(this)

        rulesModel = arrayListOf()

        db = FirebaseFirestore.getInstance()

        db.collection("Privacy").get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    for (data in it.documents) {
                        val rules: RulesModel? = data.toObject(RulesModel::class.java)
                        if (rules != null) {
                            rulesModel.add(rules)
                        }
                    }
                    recyclerView.adapter = RulesAdapter(rulesModel)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }

        binding.pback.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
            finish()
        })
    }
}