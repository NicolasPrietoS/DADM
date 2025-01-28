package co.edu.unal.reto8

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "EmpresasDB"
        private const val DATABASE_VERSION = 1

        // Nombre de la tabla y columnas
        private const val TABLE_EMPRESAS = "empresas"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_URL = "url"
        private const val COLUMN_TELEFONO = "telefono"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PRODUCTOS = "productos"
        private const val COLUMN_CLASIFICACION = "clasificacion"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_EMPRESAS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_URL TEXT,
                $COLUMN_TELEFONO TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_PRODUCTOS TEXT,
                $COLUMN_CLASIFICACION TEXT
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_EMPRESAS")
        onCreate(db)
    }

    // Operación: Crear (Insertar)
    fun insertarEmpresa(nombre: String, url: String, telefono: String, email: String, productos: String, clasificacion: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOMBRE, nombre)
        values.put(COLUMN_URL, url)
        values.put(COLUMN_TELEFONO, telefono)
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PRODUCTOS, productos)
        values.put(COLUMN_CLASIFICACION, clasificacion)

        val result = db.insert(TABLE_EMPRESAS, null, values)
        db.close()
        return result
    }

    // Operación: Leer (Obtener todas las empresas)
    fun obtenerEmpresas(): List<Map<String, String>> {
        val empresas = mutableListOf<Map<String, String>>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_EMPRESAS"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val empresa = mapOf(
                    COLUMN_ID to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)).toString(),
                    COLUMN_NOMBRE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                    COLUMN_URL to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL)),
                    COLUMN_TELEFONO to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONO)),
                    COLUMN_EMAIL to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    COLUMN_PRODUCTOS to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTOS)),
                    COLUMN_CLASIFICACION to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASIFICACION))
                )
                empresas.add(empresa)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return empresas
    }

    // Operación: Actualizar
    fun actualizarEmpresa(id: Int, nombre: String, url: String, telefono: String, email: String, productos: String, clasificacion: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOMBRE, nombre)
        values.put(COLUMN_URL, url)
        values.put(COLUMN_TELEFONO, telefono)
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PRODUCTOS, productos)
        values.put(COLUMN_CLASIFICACION, clasificacion)

        val result = db.update(TABLE_EMPRESAS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    // Operación: Eliminar
    fun eliminarEmpresa(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_EMPRESAS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
}
