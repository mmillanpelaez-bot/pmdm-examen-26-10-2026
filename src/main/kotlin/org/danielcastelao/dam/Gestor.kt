package org.danielcastelao.dam

// ---------------------------------------------------------------
// Constantes con los valores posibles del nivel
// ---------------------------------------------------------------
const val NIVEL_0: Int = 0          // Avenida G. Barbón
const val NIVEL_1: Int = 1          // Plaza de Compostela
const val NIVEL_MOVIMIENTO: Int = 2 // en movimiento, no está en ningún nivel
const val NIVEL_ERROR: Int = -1     // error

// ---------------------------------------------------------------
// Variables de la máquina de estados
// ---------------------------------------------------------------

// nivel en el que se encuentra el ascensor (0, 1, 2 o -1), empieza en el nivel 0
var nivel_actual: Int = 0
// estado actual del ascensor
var estado_acual: Estados = Estados.Idle

// botonera interior: true si el botón está pulsado
var nivel_0: Boolean = false
var nivel_1: Boolean = false

// sensor que indica que el mecanismo ha llegado al nivel de destino
var llegada: Boolean = false
// true si se detecta un fallo en el mecanismo
var fallo: Boolean = false

/**
 * Gestiona el cambio de estados
 */
fun gestor(): Int {
    when (estado_acual) {
        is Estados.Idle -> {
            nivel_actual = 0
            return nivel_actual
        }
        is Estados.MovingUP -> {
            nivel_actual = 2
            return nivel_actual
        }
        is Estados.MovingDOWN -> {
            nivel_actual = 2
            return nivel_actual
        }
        is Estados.Error -> {
            nivel_actual = -1
            return nivel_actual
        }
    }
}
