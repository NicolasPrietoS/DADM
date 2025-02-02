package co.edu.unal.reto9


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class POIDetailBottomSheet(
    private val name: String,
    private val address: String,
    private val rating: String,
    private val type: String
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_poi_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.poi_name).text = name
        view.findViewById<TextView>(R.id.poi_address).text = "Address: $address"
        view.findViewById<TextView>(R.id.poi_rating).text = "Rating: $rating"
        view.findViewById<TextView>(R.id.poi_type).text = "Type: $type"
    }
}
