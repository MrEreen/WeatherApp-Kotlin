package com.example.weatherapp

import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var weatherApi: WeatherApi
    private val apiKey = "dc2c6c1130ce62fa7a9858eb856bc33d"
    private lateinit var binding: ActivityMainBinding
    private var lastCities: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weatherApi = RetrofitClient.instance.create(WeatherApi::class.java)

        val sharedPreferences = getSharedPreferences("weather_prefs", MODE_PRIVATE)
        lastCities = sharedPreferences.getStringSet("last_cities", mutableSetOf())?.toMutableList() ?: mutableListOf()
        sehirleriEkrandaGoster()

        binding.alSehirButon.setOnClickListener {
            val konum = binding.sehirInput.text.toString()
            if (konum.isNotEmpty()) {
                fetchWeatherData(konum)
            }
        }
    }

    private fun fetchWeatherData(city: String) {
        weatherApi.getWeather(city, apiKey).enqueue(object : Callback<apiCek> {
            override fun onResponse(call: Call<apiCek>, response: Response<apiCek>) {
                if (response.isSuccessful) {
                    updateGuncellemeTarihi()
                    val HavaDurumu = response.body()
                    binding.konum.text = HavaDurumu?.name
                    binding.havaTasfiri.text = HavaDurumu?.weather?.get(0)?.description
                    binding.sicaklik.text = "${HavaDurumu?.main?.temp}°C"
                    binding.minSicaklik.text = "Min Sıcaklık: ${HavaDurumu?.main?.temp_min}°C"
                    binding.maxSicaklik.text = "Max Sıcaklık: ${HavaDurumu?.main?.temp_max}°C"
                    binding.nem.text = "Nem: ${HavaDurumu?.main?.humidity}%"
                    binding.ruzgar.text = "Rüzgar: ${HavaDurumu?.wind?.speed} m/s"

                    sonKonumlariKaydet(city)

                    if (HavaDurumu != null) {
                        println("Sicaklik: ${HavaDurumu.main?.temp}")
                        println("Min Sic: ${HavaDurumu.main?.temp_min}")
                        println("Max Sic: ${HavaDurumu.main?.temp_max}")
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Data Alinamadi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<apiCek>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateGuncellemeTarihi() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        val currentDateAndTime: String = dateFormat.format(Date())
        binding.guncellemeTarihi.text = "Guncelleme Tarihi: $currentDateAndTime"
    }

    private fun sonKonumlariKaydet(konum: String) {
        val sharedPreferences = getSharedPreferences("weather_prefs", MODE_PRIVATE)

        val arananSehirler = sharedPreferences.getStringSet("last_cities", mutableSetOf())?.toMutableList() ?: mutableListOf()

        arananSehirler.clear()
        arananSehirler.addAll(this.lastCities)

        if (arananSehirler.contains(konum)) {
            arananSehirler.remove(konum)
        }

        if (arananSehirler.size >= 3) {
            arananSehirler.removeAt(0)
        }

        arananSehirler.add(konum)

        this.lastCities.clear()
        this.lastCities.addAll(arananSehirler)

        sharedPreferences.edit().putStringSet("last_cities", arananSehirler.toMutableSet()).apply()
        sehirleriEkrandaGoster()
    }

    private fun sehirleriEkrandaGoster() {
        val sharedPreferences = getSharedPreferences("weather", MODE_PRIVATE)
        val lastCities = sharedPreferences.getStringSet("lastCities", setOf())?.toList()

        binding.sonKonumlarContainer.removeAllViews()

        lastCities?.forEach { konum ->
            val KonumTextView = TextView(this).apply {
                text = konum
                textSize = 16f
                setPadding(0, 8, 0, 8)
                gravity = Gravity.CENTER_HORIZONTAL
                setOnClickListener {
                    fetchWeatherData(konum)
                }
            }
            binding.sonKonumlarContainer.addView(KonumTextView)
        }
    }
}
