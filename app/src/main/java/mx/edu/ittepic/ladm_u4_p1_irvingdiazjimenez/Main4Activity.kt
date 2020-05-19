package mx.edu.ittepic.ladm_u4_p1_irvingdiazjimenez

import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main3.*
import kotlinx.android.synthetic.main.activity_main4.*

class Main4Activity : AppCompatActivity() {
    var dataLista = ArrayList<String>()
    var listaCelular = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)
        setTitle("Administrar Contactos Deseados")
        Deseados()
        button6.setOnClickListener {
            finish()
        }

        button10.setOnClickListener {
        if(editText10.text.toString().isEmpty() || editText11.text.toString().isEmpty()){
            Toast.makeText(this,"Campo Vacío",Toast.LENGTH_LONG)
                .show()
            return@setOnClickListener
        }
                try {
                    var baseDatos = BaseDatos(this,"LISTANUMEROS",null,1)
                    var insertar = baseDatos.writableDatabase
                    var SQL = "INSERT INTO LISTANUMEROS VALUES('${editText10.text.toString()}','${editText11.text.toString()}','deseado')"
                    insertar.execSQL(SQL)
                    baseDatos.close()
                }catch (err: SQLiteException){
                    Toast.makeText(this, err.message, Toast.LENGTH_LONG)
                        .show()
                }
                Deseados()
        }

        listaDeseados.setOnItemClickListener { parent, view, position, id ->
            if(listaCelular.size==0){
                return@setOnItemClickListener
            }
            AlertaEliminar(position)
        }

    }

    private fun AlertaEliminar(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("¿Deseas eliminar el contacto de la lista de contactos deseados?")
            .setMessage(dataLista[position])
            .setPositiveButton("Eliminar"){d,i-> EliminarContactoLista(position)}
            .setNeutralButton("Cancelar"){d,i->}
            .show()
    }

    private fun EliminarContactoLista(pos : Int) {
        try {
            var base = BaseDatos(this,"LISTANUMEROS",null,1)
            var eliminar = base.writableDatabase
            var idEliminar = arrayOf(listaCelular[pos])
            var respuesta =  eliminar.delete("LISTANUMEROS","CELULAR=?",idEliminar)
            if(respuesta.toInt() == 0){
                Toast.makeText(this,"NO SE ELIMINÓ EL CONTACTO",Toast.LENGTH_LONG)
                    .show()
            }
        }catch (e:SQLiteException){
            Toast.makeText(this,e.message,Toast.LENGTH_LONG)
                .show()
        }
        Deseados()
    }
    fun Deseados(){
        dataLista.clear()
        listaCelular.clear()
        try{
            val cursor = BaseDatos(this,"LISTANUMEROS",null,1)
                .readableDatabase
                .rawQuery("SELECT * FROM LISTANUMEROS WHERE deseado = 'deseado'",null)
            var ultimo = "Numeros:"
            if(cursor.moveToFirst()){
                do{
                    ultimo ="Nombre Contacto: "+
                            cursor.getString(0)+
                            "\n Numero: "+
                            cursor.getString(1)
                    dataLista.add(ultimo)
                    listaCelular.add(cursor.getString(1))
                }while(cursor.moveToNext())
            }else{
                dataLista.add("No hay datos en la base de datos")
            }
            var adaptador = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataLista)
            listaDeseados.adapter = adaptador
        }catch (err: SQLiteException){
            Toast.makeText(this,err.message, Toast.LENGTH_LONG)
                .show()
        }
    }
}
