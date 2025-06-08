package com.litegral.greenfresh

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.litegral.greenfresh.adapter.PlantAdapter
import com.litegral.greenfresh.data.Plant
import com.litegral.greenfresh.data.remote.ApiClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var plantAdapter: PlantAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Set up toolbar properly
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        // Setup SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            fetchPlants()
        }
        
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        plantAdapter = PlantAdapter(mutableListOf()) { plant ->
            showDeleteConfirmationDialog(plant)
        }
        recyclerView.adapter = plantAdapter

        val fabAdd = findViewById<FloatingActionButton>(R.id.fab_add)
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddItemActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                fetchPlants()
                true
            }
            R.id.action_logout -> {
                showLogoutConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                logout()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun logout() {
        auth.signOut()
        // Navigate to login screen
        val intent = Intent(this, LoginFormActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        fetchPlants()
    }

    private fun showDeleteConfirmationDialog(plant: Plant) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Tanaman")
            .setMessage("Apakah Anda yakin ingin menghapus ${plant.plant_name}?")
            .setPositiveButton("Hapus") { _, _ ->
                deletePlant(plant)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deletePlant(plant: Plant) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.instance.deletePlant(plant.plant_name)
                Toast.makeText(this@MainActivity, response.message, Toast.LENGTH_SHORT).show()
                fetchPlants() // Refresh the list
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to delete plant", e)
                Toast.makeText(this@MainActivity, "Gagal menghapus: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchPlants() {
        Log.d("MainActivity", "fetchPlants called")
        lifecycleScope.launch {
            try {
                Log.d("MainActivity", "Fetching plants from API...")
                val response = ApiClient.instance.getPlants()
                Log.d("MainActivity", "API Response: ${response.data.size} plants")
                if (response.data.isNotEmpty()) {
                    plantAdapter.addPlants(response.data)
                } else {
                    plantAdapter.addPlants(emptyList()) // Clear the list if no plants
                    Toast.makeText(this@MainActivity, "No plants found", Toast.LENGTH_SHORT).show()
                }
                swipeRefreshLayout.isRefreshing = false
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to fetch plants", e)
                Toast.makeText(this@MainActivity, "Failed to fetch plants: ${e.message}", Toast.LENGTH_LONG).show()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}