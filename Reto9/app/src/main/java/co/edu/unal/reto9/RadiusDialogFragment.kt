package co.edu.unal.reto9


import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class RadiusDialogFragment(private val onRadiusSelected: (Int) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_radius, null)
        val inputField = view.findViewById<EditText>(R.id.radius_input)

        return AlertDialog.Builder(requireContext())
            .setTitle("Enter Search Radius (km)")
            .setView(view)
            .setPositiveButton("OK") { _, _ ->
                val inputText = inputField.text.toString()
                val radius = inputText.toIntOrNull()

                if (radius != null && radius > 0) {
                    onRadiusSelected(radius)
                } else {
                    Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}