package org.danielcastelao.dam

/**
 * Estados posibles del Halo de Vigo.
 *
 * Uso una sealed class para que el compilador me obligue a tratar
 * todos los estados dentro de un when.
 */
sealed class Estados {
    // reposo, en el nivel 0 o en el nivel 1
    object Idle : Estados()
    // el mecanismo está subiendo
    object MovingUP : Estados()
    // el mecanismo está bajando
    object MovingDOWN : Estados()
    // fallo, no está en ningún nivel y no se puede mover
    object Error : Estados()

    // para que al imprimir el estado salga el nombre y no el hash del objeto
    override fun toString(): String = when (this) {
        is Idle -> "Idle"
        is MovingUP -> "MovingUP"
        is MovingDOWN -> "MovingDOWN"
        is Error -> "Error"
    }
}
