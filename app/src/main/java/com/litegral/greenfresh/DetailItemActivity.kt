package com.litegral.greenfresh

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.litegral.greenfresh.data.PlantRequestBody
import com.litegral.greenfresh.data.remote.ApiClient
import kotlinx.coroutines.launch

class DetailItemActivity : AppCompatActivity() {

    private lateinit var etNamaTanaman: TextInputEditText
    private lateinit var etHarga: TextInputEditText
    private lateinit var etDeskripsi: TextInputEditText
    private lateinit var ivTanaman: ImageView
    private lateinit var btnEdit: MaterialButton
    private lateinit var btnSave: MaterialButton
    private var originalPlantName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_item)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        etNamaTanaman = findViewById(R.id.etNamaTanaman)
        etHarga = findViewById(R.id.etHarga)
        etDeskripsi = findViewById(R.id.etDeskripsi)
        ivTanaman = findViewById(R.id.ivTanaman)
        btnEdit = findViewById(R.id.btnEdit)
        btnSave = findViewById(R.id.btnSave)

        originalPlantName = intent.getStringExtra("PLANT_NAME")
        if (originalPlantName != null) {
            fetchPlantDetails(originalPlantName!!)
        } else {
            Toast.makeText(this, "Plant name not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        setEditMode(false) // Initial state

        btnEdit.setOnClickListener {
            setEditMode(true)
        }

        btnSave.setOnClickListener {
            originalPlantName?.let {
                savePlantDetails(it)
            }
        }
    }

    private fun setEditMode(isEditing: Boolean) {
        etNamaTanaman.isEnabled = isEditing
        etHarga.isEnabled = isEditing
        etDeskripsi.isEnabled = isEditing

        if (isEditing) {
            btnEdit.visibility = View.GONE
            btnSave.visibility = View.VISIBLE
            etNamaTanaman.requestFocus()
        } else {
            btnEdit.visibility = View.VISIBLE
            btnSave.visibility = View.GONE
        }
    }

    private fun savePlantDetails(name: String) {
        val newName = etNamaTanaman.text.toString().trim()
        val newPrice = etHarga.text.toString().trim()
        val newDescription = etDeskripsi.text.toString().trim()

        if (newName.isEmpty() || newPrice.isEmpty() || newDescription.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedPlant = PlantRequestBody(plantName = newName, price = newPrice, description = newDescription)

        lifecycleScope.launch {
            try {
                val response = ApiClient.instance.updatePlant(name, updatedPlant)
                Toast.makeText(this@DetailItemActivity, response.message, Toast.LENGTH_SHORT).show()
                if (response.message == "Success Update plant") {
                    setEditMode(false)
                    originalPlantName = newName // Update original name if it changed
                    supportActionBar?.title = newName // Update toolbar title
                }
            } catch (e: Exception) {
                Log.e("DetailItemActivity", "Failed to update plant", e)
                Toast.makeText(this@DetailItemActivity, "Gagal memperbarui: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchPlantDetails(name: String) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.instance.getPlantByName(name)
                if (response.data != null) {
                    val plant = response.data
                    supportActionBar?.title = plant.plant_name
                    etNamaTanaman.setText(plant.plant_name)
                    etHarga.setText(plant.price)
                    etDeskripsi.setText(plant.description)

                    // Assuming there is an image URL in the future.
                    // For now, we use a placeholder.
                    Glide.with(this@DetailItemActivity)
                        .load(R.drawable.default_plant_image)
                        .into(ivTanaman)
                } else {
                    Toast.makeText(this@DetailItemActivity, response.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Log.e("DetailItemActivity", "Failed to fetch plant details", e)
                Toast.makeText(this@DetailItemActivity, "Failed to fetch plant details: ${e.message}", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
} 