package co.edu.unal.reto9

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class POIDialogFragment(private val name: String, private val description: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_poi, null)
        view.findViewById<TextView>(R.id.poi_name).text = name
        view.findViewById<TextView>(R.id.poi_description).text = description

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setPositiveButton("Close", null)
            .create()
    }
}
