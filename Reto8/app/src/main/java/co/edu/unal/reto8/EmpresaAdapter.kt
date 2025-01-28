package co.edu.unal.reto8

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmpresaAdapter(
    private val empresas: List<Map<String, String>>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<EmpresaAdapter.EmpresaViewHolder>() {

    class EmpresaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreEmpresa)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
        val tvClasificacion: TextView = itemView.findViewById(R.id.tvClasificacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpresaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.empresa_item, parent, false)
        return EmpresaViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmpresaViewHolder, position: Int) {
        val empresa = empresas[position]
        holder.tvNombre.text = empresa["nombre"]
        holder.tvTelefono.text = empresa["telefono"]
        holder.tvClasificacion.text = empresa["clasificacion"]

        // Configurar el clic en el elemento
        holder.itemView.setOnClickListener {
            val id = empresa["id"]?.toIntOrNull()
            if (id != null) {
                onItemClick(id)
            }
        }
    }

    override fun getItemCount(): Int {
        return empresas.size
    }
}
