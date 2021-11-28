package com.example.fall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fall.data.DeadPlayersDB
import com.example.fall.data.PlayerData
import com.example.fall.databinding.ActivityCemeteryBinding
import kotlin.concurrent.thread

class CemeteryActivity : AppCompatActivity(), DeadPlayerAdapter.OnPlayerSelectedListener {
    private lateinit var binding: ActivityCemeteryBinding
    private lateinit var adapter: DeadPlayerAdapter
    private lateinit var db: DeadPlayersDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCemeteryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DeadPlayersDB.getDatabase(applicationContext)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rvCemetery.layoutManager = LinearLayoutManager(this)
        adapter = DeadPlayerAdapter(this)
        binding.rvCemetery.adapter = adapter
        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        thread() {
            val items = db.playerDao().getAll()
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onItemChanged(item: PlayerData) {
        TODO("Not yet implemented")
    }
}