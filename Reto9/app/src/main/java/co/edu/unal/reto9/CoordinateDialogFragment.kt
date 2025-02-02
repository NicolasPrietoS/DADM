package co.edu.unal.reto9

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class CoordinateDialogFragment(private val onCoordinatesEntered: (Double, Double) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_coordinates, null)
        val latInput = view.findViewById<EditText>(R.id.input_latitude)
        val lonInput = view.findViewById<EditText>(R.id.input_longitude)

        return AlertDialog.Builder(requireContext())
            .setTitle("Enter Coordinates")
            .setView(view)
            .setPositiveButton("Search") { _, _ ->
                val lat = latInput.text.toString().toDoubleOrNull()
                val lon = lonInput.text.toString().toDoubleOrNull()

                if (lat != null && lon != null) {
                    onCoordinatesEntered(lat, lon)
                } else {
                    Toast.makeText(requireContext(), "Invalid coordinates", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
