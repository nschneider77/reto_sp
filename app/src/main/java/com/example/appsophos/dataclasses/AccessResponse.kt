package com.example.appsophos.dataclasses

// recibe los datos de la api y los guarda en las variables (se tienen que llamar igual que en la api)

data class AccessResponse (var id: String, var nombre: String, var apellido: String,
                           var acceso: Boolean, var admin: Boolean ){


}
