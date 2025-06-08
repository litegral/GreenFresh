package com.litegral.greenfresh

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.litegral.greenfresh.data.PlantRequestBody
import com.litegral.greenfresh.data.remote.ApiClient
import kotlinx.coroutines.launch

class AddItemActivity : AppCompatActivity() {

    private lateinit var ivTanaman: ImageView
    private lateinit var etNamaTanaman: TextInputEditText
    private lateinit var etHarga: TextInputEditText
    private lateinit var etDeskripsi: TextInputEditText
    private lateinit var btnTambah: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        // Set up toolbar properly
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        // Initialize views
        ivTanaman = findViewById(R.id.ivTanaman)
        etNamaTanaman = findViewById(R.id.etNamaTanaman)
        etHarga = findViewById(R.id.etHarga)
        etDeskripsi = findViewById(R.id.etDeskripsi)
        btnTambah = findViewById(R.id.btnTambah)

        // Set click listener for add button
        btnTambah.setOnClickListener {
            addPlant()
        }

        // Set click listener for back navigation
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun addPlant() {
        val name = etNamaTanaman.text.toString().trim()
        val priceText = etHarga.text.toString().trim()
        val description = etDeskripsi.text.toString().trim()

        if (name.isEmpty() || priceText.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // No need to convert to double, as the API expects a string
        val plantRequestBody = PlantRequestBody(
            plantName = name,
            description = description,
            price = priceText
        )

        createPlant(plantRequestBody)
    }

    private fun createPlant(plantRequestBody: PlantRequestBody) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.instance.createPlant(plantRequestBody)
                if (response.message.isNotEmpty()) {
                    Toast.makeText(this@AddItemActivity, "Tanaman berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    finish() // Go back to main activity
                } else {
                    Toast.makeText(this@AddItemActivity, "Gagal menambahkan tanaman", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddItemActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 