package com.example.appsophos.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.appsophos.ApiService
import com.example.appsophos.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor


class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // objeto de vinculacion de views
        setContentView(binding.root)
        binding.botonIngreso.setOnClickListener { ingresar()}
        binding.botonHuella.setOnClickListener {biometricPrompt.authenticate(promptInfo)}
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = androidx.biometric.BiometricPrompt(this,executor,
            object: androidx.biometric.BiometricPrompt.AuthenticationCallback(){

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    showNotify("Verificado")
                    ingresoConHuella()


                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showNotify("NOUUUU")
                }


            })
        promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticacion Biometrica")
            .setSubtitle("Ingresa tu huella")
            .setNegativeButtonText("log in con usuario y contraseña")
            .build()




    }
    private fun showNotify(mensaje: String){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }



    private fun getRetrofit(): Retrofit{ // retrofit para hacer la peticion
        return Retrofit.Builder()
            .baseUrl("https://6w33tkx4f9.execute-api.us-east-1.amazonaws.com/") // url del servicio
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private fun intentoIngreso(correo: String,contra:String){ // los parametros son las credenciales para entrar
        CoroutineScope(Dispatchers.IO).launch {   // corrutina para llamar al servicio en otro hilo
            val call = getRetrofit().create(ApiService::class.java).GetUserByCredentials("RS_Usuarios?idUsuario=$correo&clave=$contra") // llamada a la url(queda la respuesta aca)
            val respuesta = call.body() // extrae el body de la respuesta
            runOnUiThread { // muestra en pantalla lo que se hace en la corrutina

                if (respuesta?.acceso == true) { // si el request es correcto (aca poner lo que quiera que se ejecute si la request es exitosa)
                    val nombre = respuesta?.nombre // extrae el atributo nombre del json de respuesta
                    val apellido = respuesta?.apellido
                    showSuccess(nombre,apellido,correo)

                } else {
                    showError()

                }
            }


        }
    }


    private fun showSuccess(nombre: String, apellido:String,correoElectronico: String) { // saca toast de bienvenida y guarda las variables para la otra activity
        Toast.makeText(this,"Bienvenido $nombre $apellido ",Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra("NOMBRE",nombre) // guarda la variable nombre para pasarla a la otra activity
        intent.putExtra("APELLIDO",apellido)
        intent.putExtra("CORREO",correoElectronico)
        startActivity(intent)
    }

    private fun showError() { // saca toast de datos erroneos
        Toast.makeText(this,"Datos de Usuario incorrectos",Toast.LENGTH_SHORT).show()
    }


    private fun ingresar() {

        val correoIngresado = binding.textoCorreo.text.toString() // guarda el correo digitado
        val contraIngresada = binding.textoContra.text.toString() // guarda la contraseña digitada
        intentoIngreso(correoIngresado,contraIngresada)

    }
    private fun ingresoConHuella(){

        intentoIngreso("nicsh99@gmail.com","KMArmN7dE33VHQdm")

    }


}
