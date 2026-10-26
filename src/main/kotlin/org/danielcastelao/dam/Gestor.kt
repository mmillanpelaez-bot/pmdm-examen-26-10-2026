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

// ---------------------------------------------------------------
// Funciones auxiliares de las transiciones
// ---------------------------------------------------------------

/** Pone la botonera interior a "sin pulsar". */
fun limpiarBotonera() {
    nivel_0 = false
    nivel_1 = false
}

/** Idle -> MovingUP */
fun iniciarSubida() {
    estado_acual = Estados.MovingUP
    nivel_actual = NIVEL_MOVIMIENTO
}

/** Idle -> MovingDOWN */
fun iniciarBajada() {
    estado_acual = Estados.MovingDOWN
    nivel_actual = NIVEL_MOVIMIENTO
}

/** MovingUP / MovingDOWN -> Idle, el mecanismo ya ha llegado al nivel de destino */
fun finalizarMovimiento(nivelDestino: Int) {
    estado_acual = Estados.Idle
    nivel_actual = nivelDestino
    llegada = false
}

/** Cualquier estado -> Error */
fun activarError() {
    estado_acual = Estados.Error
    nivel_actual = NIVEL_ERROR
}

// ---------------------------------------------------------------
// Funciones de cada estado (aquí se deciden las transiciones)
// ---------------------------------------------------------------

/**
 * Idle: está parado en el nivel 0 o en el nivel 1.
 * - Si está en el nivel 0 y se pulsa nivel_1 -> MovingUP
 * - Si está en el nivel 1 y se pulsa nivel_0 -> MovingDOWN
 * - Si se pulsa el botón del nivel en el que ya está no hace nada
 */
fun accionesIdle() {
    if (nivel_actual == NIVEL_0 && nivel_1) {
        iniciarSubida()
    } else if (nivel_actual == NIVEL_1 && nivel_0) {
        iniciarBajada()
    }
}

/**
 * MovingUP: está subiendo, cuando llega al nivel 1 vuelve a Idle.
 */
fun accionesMovingUP() {
    nivel_actual = NIVEL_MOVIMIENTO
    if (llegada) {
        finalizarMovimiento(NIVEL_1)
    }
}

/**
 * MovingDOWN: está bajando, cuando llega al nivel 0 vuelve a Idle.
 */
fun accionesMovingDOWN() {
    nivel_actual = NIVEL_MOVIMIENTO
    if (llegada) {
        finalizarMovimiento(NIVEL_0)
    }
}

/**
 * Error: no sale nunca de este estado (Error -> Error),
 * da igual lo que se pulse en la botonera.
 */
fun accionesError() {
    estado_acual = Estados.Error
    nivel_actual = NIVEL_ERROR
}

/**
 * Gestiona el cambio de estados.
 * Cada vez que se llama se evalúan las transiciones del estado actual
 * y devuelve el valor de nivel_actual.
 */
fun gestor(): Int {
    // el fallo tiene prioridad sobre todo lo demás
    if (fallo && estado_acual !is Estados.Error) {
        activarError()
    } else {
        when (estado_acual) {
            is Estados.Idle -> accionesIdle()
            is Estados.MovingUP -> accionesMovingUP()
            is Estados.MovingDOWN -> accionesMovingDOWN()
            is Estados.Error -> accionesError()
        }
    }
    // los botones se "consumen" en cada ciclo
    limpiarBotonera()
    return nivel_actual
}
