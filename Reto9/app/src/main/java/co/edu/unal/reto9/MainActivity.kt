package co.edu.unal.reto9

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private var userLocation: Location? = null
    private var searchRadius: Int = 5 // Default radius in km

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Configuration.getInstance().load(applicationContext, getPreferences(MODE_PRIVATE))
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        findViewById<Button>(R.id.btn_select_radius).setOnClickListener {
            showRadiusDialog()
        }
        findViewById<Button>(R.id.btn_search_coordinates).setOnClickListener {
            showCoordinateDialog()
        }

        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setupLocationOverlay()
        } else {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) setupLocationOverlay()
            }.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setupLocationOverlay() {
        val locationOverlay = MyLocationNewOverlay(map)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        locationOverlay.runOnFirstFix {
            runOnUiThread {
                val userGeoPoint = locationOverlay.myLocation
                if (userGeoPoint != null) {
                    userLocation = Location("").apply {
                        latitude = userGeoPoint.latitude
                        longitude = userGeoPoint.longitude
                    }
                    map.controller.animateTo(userGeoPoint)
                    map.controller.setZoom(15.0)
                    fetchPOIs(userGeoPoint.latitude, userGeoPoint.longitude, searchRadius) // Carga POIs iniciales
                }
            }
        }
        map.overlays.add(locationOverlay)
    }



    private fun addPOIToMap(name: String, lat: Double, lon: Double, address: String, rating: String, type: String) {
        val marker = Marker(map)
        marker.position = GeoPoint(lat, lon)
        marker.title = name
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        // Evento de clic para mostrar los detalles del POI en un BottomSheet
        marker.setOnMarkerClickListener { _, _ ->
            val bottomSheet = POIDetailBottomSheet(name, address, rating, type)
            bottomSheet.show(supportFragmentManager, "POIDetailBottomSheet")
            true
        }

        map.overlays.add(marker)
        map.invalidate()
    }


    private fun fetchPOIs(lat: Double, lon: Double, radius: Int) {
        val query = """
        [out:json];
        (
            node(around:${radius * 1000}, $lat, $lon) ["amenity"];
        );
        out;
    """.trimIndent()

        val url = "https://overpass-api.de/api/interpreter?data=${query.replace("\n", "")}"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) return
                    val jsonData = response.body?.string() ?: return

                    runOnUiThread {
                        clearPOIs() // 🔥 Limpia los POIs antes de agregar nuevos
                        parseAndDisplayPOIs(jsonData)
                    }
                }
            }
        })
    }

    private fun clearPOIs() {
        val markersToRemove = mutableListOf<Marker>()

        for (overlay in map.overlays) {
            if (overlay is Marker) {
                markersToRemove.add(overlay)
            }
        }

        map.overlays.removeAll(markersToRemove)
        map.invalidate()
    }


    private fun parseAndDisplayPOIs(jsonData: String) {
        val jsonObject = JSONObject(jsonData)
        val elements = jsonObject.getJSONArray("elements")

        for (i in 0 until elements.length()) {
            val element = elements.getJSONObject(i)
            val lat = element.getDouble("lat")
            val lon = element.getDouble("lon")
            val tags = element.optJSONObject("tags") ?: JSONObject()

            val name = tags.optString("name", "Unnamed POI")
            val address = tags.optString("addr:street", "Unknown Address")
            val rating = tags.optString("rating", "No Rating")
            val type = tags.optString("amenity", "Unknown Type")

            addPOIToMap(name, lat, lon, address, rating, type)
        }
    }
    fun fetchPOIsFromCoordinates(lat: Double, lon: Double, radius: Int) {
        val query = """
        [out:json];
        (
            node(around:${radius * 1000}, $lat, $lon) ["amenity"];
        );
        out;
    """.trimIndent()

        val url = "https://overpass-api.de/api/interpreter?data=${query.replace("\n", "")}"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) return
                    val jsonData = response.body?.string() ?: return

                    runOnUiThread {
                        clearPOIs() // 🔥 Limpia los POIs antes de agregar nuevos
                        parseAndDisplayPOIs(jsonData)
                    }
                }
            }
        })
    }

    private fun showRadiusDialog() {
        val dialog = RadiusDialogFragment { selectedRadius ->
            searchRadius = selectedRadius // Actualiza el radio
            userLocation?.let {
                fetchPOIs(it.latitude, it.longitude, searchRadius) // Vuelve a cargar POIs con el nuevo radio
            }
        }
        dialog.show(supportFragmentManager, "radiusDialog")
    }
    private fun showCoordinateDialog() {
        val dialog = CoordinateDialogFragment { lat, lon ->
            fetchPOIsFromCoordinates(lat, lon, searchRadius) // 🔥 Buscar POIs con las coordenadas ingresadas
            map.controller.animateTo(GeoPoint(lat, lon)) // Centrar el mapa en las coordenadas ingresadas
            map.controller.setZoom(15.0)
        }
        dialog.show(supportFragmentManager, "coordinateDialog")
    }

}
