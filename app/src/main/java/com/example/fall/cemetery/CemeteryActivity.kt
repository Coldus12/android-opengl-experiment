package com.example.fall.cemetery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fall.R
import com.example.fall.data.persistent_data.DeadPlayersDB
import com.example.fall.data.persistent_data.PlayerData
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

    override fun onPlayerSelected(item: PlayerData) {
        val intent = Intent(this@CemeteryActivity, DetailsActivity::class.java)

        val name = when (item.modelResourceId) {
            R.drawable.pistol_moving_sprite -> "Denzel"
            R.drawable.shotgun_player -> "Justin"
            else -> "Denzel"
        }

        val resId = when (item.modelResourceId) {
            R.drawable.pistol_moving_sprite -> R.drawable.pistolie
            R.drawable.shotgun_player -> R.drawable.shottie
            else -> R.drawable.pistolie
        }

        intent.putExtra("name", name)
        intent.putExtra("score", item.score)
        intent.putExtra("levels", item.nrOfLevelsReached)
        intent.putExtra("resId", resId)

        startActivity(intent)
    }
}