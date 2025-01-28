package co.edu.unal.reto8

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EmpresaAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var btnAgregar: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar la base de datos
        dbHelper = DatabaseHelper(this)

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.rvEmpresas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar botón para agregar empresa
        btnAgregar = findViewById(R.id.btnAgregarEmpresa)
        btnAgregar.setOnClickListener {
            val intent = Intent(this, EditEmpresaActivity::class.java)
            startActivity(intent)
        }

        // Actualizar la lista de empresas
        actualizarLista()
    }

    override fun onResume() {
        super.onResume()
        actualizarLista() // Actualizar la vista al volver a la actividad principal
    }

    private fun actualizarLista() {
        val empresas = dbHelper.obtenerEmpresas()
        adapter = EmpresaAdapter(empresas) { empresaId ->
            mostrarOpcionesEmpresa(empresaId)
        }
        recyclerView.adapter = adapter
    }

    private fun mostrarOpcionesEmpresa(empresaId: Int) {
        val opciones = arrayOf("Modificar", "Eliminar")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccione una opción")
        builder.setItems(opciones) { dialog, which ->
            when (which) {
                0 -> { // Modificar
                    val intent = Intent(this, EditEmpresaActivity::class.java)
                    intent.putExtra("EMPRESA_ID", empresaId)
                    startActivity(intent)
                }
                1 -> { // Eliminar
                    confirmarEliminacion(empresaId)
                }
            }
        }
        builder.show()
    }

    private fun confirmarEliminacion(empresaId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que deseas eliminar esta empresa?")
        builder.setPositiveButton("Sí") { _, _ ->
            val result = dbHelper.eliminarEmpresa(empresaId)
            if (result > 0) {
                Toast.makeText(this, "Empresa eliminada correctamente", Toast.LENGTH_SHORT).show()
                actualizarLista()
            } else {
                Toast.makeText(this, "Error al eliminar la empresa", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }
}

