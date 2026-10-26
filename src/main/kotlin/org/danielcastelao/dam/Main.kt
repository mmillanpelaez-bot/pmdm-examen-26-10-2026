package org.danielcastelao.dam

/** Imprime cómo está el ascensor en este momento. */
fun mostrarSituacion(paso: String) {
    println("$paso -> estado: $estado_acual, nivel_actual: $nivel_actual")
}

fun main() {
    mostrarSituacion("Inicio")

    // subo desde la Avenida G. Barbón hasta la Plaza de Compostela
    nivel_1 = true
    gestor()
    mostrarSituacion("Pulso nivel_1")

    gestor()
    mostrarSituacion("Sigue subiendo")

    llegada = true
    gestor()
    mostrarSituacion("Llega arriba")

    // vuelvo a bajar
    nivel_0 = true
    gestor()
    mostrarSituacion("Pulso nivel_0")

    llegada = true
    gestor()
    mostrarSituacion("Llega abajo")

    // simulo un fallo
    fallo = true
    gestor()
    mostrarSituacion("Fallo")
}
