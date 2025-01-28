package co.edu.unal.reto8

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditEmpresaActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etUrl: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etEmail: EditText
    private lateinit var etProductos: EditText
    private lateinit var spClasificacion: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    private lateinit var dbHelper: DatabaseHelper
    private var empresaId: Int? = null // Variable para identificar si estamos editando una empresa existente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit) // Cambiado para reflejar el nombre correcto del XML

        // Inicializar vistas
        etNombre = findViewById(R.id.etNombreEmpresa)
        etUrl = findViewById(R.id.etUrl)
        etTelefono = findViewById(R.id.etTelefono)
        etEmail = findViewById(R.id.etEmail)
        etProductos = findViewById(R.id.etProductosServicios)
        spClasificacion = findViewById(R.id.spClasificacion)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)

        // Configurar Spinner de Clasificación
        val clasificaciones = arrayOf("Consultoría", "Desarrollo a la medida", "Fábrica de software")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, clasificaciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spClasificacion.adapter = adapter

        // Inicializar base de datos
        dbHelper = DatabaseHelper(this)

        // Verificar si estamos editando una empresa existente
        empresaId = intent.getIntExtra("EMPRESA_ID", -1).takeIf { it != -1 }

        if (empresaId != null) {
            cargarDatosEmpresa(empresaId!!)
        }

        // Botón Guardar
        btnGuardar.setOnClickListener {
            guardarEmpresa()
        }

        // Botón Cancelar
        btnCancelar.setOnClickListener {
            finish() // Cierra la actividad sin realizar cambios
        }
    }

    private fun cargarDatosEmpresa(id: Int) {
        val empresas = dbHelper.obtenerEmpresas()
        val empresa = empresas.find { it["id"]?.toInt() == id }

        if (empresa != null) {
            etNombre.setText(empresa["nombre"])
            etUrl.setText(empresa["url"])
            etTelefono.setText(empresa["telefono"])
            etEmail.setText(empresa["email"])
            etProductos.setText(empresa["productos"])
            val clasificacionIndex = (spClasificacion.adapter as ArrayAdapter<String>)
                .getPosition(empresa["clasificacion"])
            spClasificacion.setSelection(clasificacionIndex)
        }
    }

    private fun guardarEmpresa() {
        val nombre = etNombre.text.toString().trim()
        val url = etUrl.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val productos = etProductos.text.toString().trim()
        val clasificacion = spClasificacion.selectedItem.toString()

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre de la empresa es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        if (empresaId == null) {
            // Agregar nueva empresa
            val result = dbHelper.insertarEmpresa(nombre, url, telefono, email, productos, clasificacion)
            if (result != -1L) {
                Toast.makeText(this, "Empresa agregada correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al agregar la empresa", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Actualizar empresa existente
            val result = dbHelper.actualizarEmpresa(empresaId!!, nombre, url, telefono, email, productos, clasificacion)
            if (result > 0) {
                Toast.makeText(this, "Empresa actualizada correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al actualizar la empresa", Toast.LENGTH_SHORT).show()
            }
        }

        finish() // Cierra la actividad después de guardar los cambios
    }
}
