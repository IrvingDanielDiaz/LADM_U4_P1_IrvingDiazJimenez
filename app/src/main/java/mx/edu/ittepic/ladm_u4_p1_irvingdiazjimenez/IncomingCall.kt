package mx.edu.ittepic.ladm_u4_p1_irvingdiazjimenez

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main2.*

class IncomingCall : BroadcastReceiver(){
    private val TAG = "PhoneStatReceiver"
    private var incomingFlag = false
    private var incoming_number: String? = null
    var mensajeEnvio =""
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            incomingFlag = false
            val phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            Log.e(TAG, "call OUT:$phoneNumber")
        } else {

            val tm =
                context.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when (tm.callState) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    incomingFlag = true
                    incoming_number = intent.getStringExtra("incoming_number")
                    Log.e(TAG, "RINGING :$incoming_number")
                    if(incoming_number!=null){
                        /*try {
                            var baseDatos = BaseDatos(context,"Llamadas",null,1)
                            var insertar = baseDatos.writableDatabase
                            var SQL = "INSERT INTO CELULAR VALUES('${incoming_number}')"
                            insertar.execSQL(SQL)
                            baseDatos.close()
                        }catch (err: SQLiteException){

                        }*/
                        //-------------------
                        envioSMS(""+incoming_number,context)
                        //-------------
                    }
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> if (incomingFlag) {
                    Log.e(TAG, "incoming ACCEPT :$incoming_number")
                }
                TelephonyManager.CALL_STATE_IDLE -> if (incomingFlag) {
                    Log.e(TAG, "incoming IDLE")
                }
            }
        }
    }

    private fun envioSMS(numero : String,context: Context) {
        var mensaje = ""
        try{
            val cursor = BaseDatos(context,"LISTANUMEROS",null,1)
                .readableDatabase
                .rawQuery("SELECT * FROM LISTANUMEROS where CELULAR = '${numero}'",null)

            if(cursor.moveToFirst()){
                do{
                    mensajeEnvio = cursor.getString(2)
                }while(cursor.moveToNext())
            }else{
                mensajeEnvio = "NORECONOCIDO"
            }
        }catch (err:SQLiteException){
        }
        if(mensajeEnvio== "deseado"){
            mensaje = leerUltimosMensajeDeseado(context)
        }
        if(mensajeEnvio== "nodeseado"){
            mensaje = leerUltimosMensajeNODeseado(context)
        }
        if(mensajeEnvio == "NORECONOCIDO"){
            mensaje = "No te reconoce"
        }

        SmsManager.getDefault().sendTextMessage(
            numero,null,
            mensaje,null,null)
    }

    fun leerUltimosMensajeDeseado(context: Context) : String{
        //leer mensajes
        var ultimoMensaje = ""
        try{
            val cursor = BaseDatos(context,"mensajes",null,1)
                .readableDatabase
                .rawQuery("SELECT * FROM MENSAJES where deseado = 'deseado'",null)

            if(cursor.moveToFirst()){
                do{
                    ultimoMensaje = cursor.getString(0)
                }while(cursor.moveToNext())
            }else{
                ultimoMensaje = "Sin mensajes aún, Tabla vacía"
            }

        }catch (err:SQLiteException){

        }
        return ultimoMensaje
    }

    fun leerUltimosMensajeNODeseado(context: Context) : String{
        //leer mensajes
        var ultimoMensaje = ""
        try{
            val cursor = BaseDatos(context,"mensajes",null,1)
                .readableDatabase
                .rawQuery("SELECT * FROM MENSAJES where deseado = 'nodeseado'",null)

            if(cursor.moveToFirst()){
                do{
                    ultimoMensaje = cursor.getString(0)
                }while(cursor.moveToNext())
            }else{
                ultimoMensaje = "Sin mensajes aún, Tabla vacía"
            }

        }catch (err:SQLiteException){

        }
        return ultimoMensaje
    }
}