package org.danielcastelao.dam

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GestorTest {

    // dejo todo en el estado inicial antes y después de cada test
    private fun reiniciar() {
        estado_acual = Estados.Idle
        nivel_actual = 0
        nivel_0 = false
        nivel_1 = false
        llegada = false
        fallo = false
    }

    @BeforeTest
    fun prepararEstado() = reiniciar()

    @AfterTest
    fun restaurarEstado() = reiniciar()

    // ------------------------------------------------------------
    // Estado inicial
    // ------------------------------------------------------------

    @Test
    fun `el estado inicial es Idle y el nivel inicial es 0`() {
        assertEquals(Estados.Idle, estado_acual)
        assertEquals(0, nivel_actual)
        assertFalse(nivel_0)
        assertFalse(nivel_1)
        assertFalse(llegada)
        assertFalse(fallo)
    }

    // ------------------------------------------------------------
    // Tests del código base (acciones de cada estado)
    // ------------------------------------------------------------

    @Test
    fun `gestor mantiene el nivel cuando el estado es Idle`() {
        estado_acual = Estados.Idle
        nivel_actual = 0

        assertEquals(0, gestor())
        assertEquals(0, nivel_actual)
    }

    @Test
    fun `gestor establece el nivel cuando el estado es MovingUP`() {
        estado_acual = Estados.MovingUP
        nivel_actual = 5

        assertEquals(2, gestor())
        assertEquals(2, nivel_actual)
    }

    @Test
    fun `gestor establece el nivel cuando el estado es MovingDOWN`() {
        estado_acual = Estados.MovingDOWN
        nivel_actual = 5

        assertEquals(2, gestor())
        assertEquals(2, nivel_actual)
    }

    @Test
    fun `gestor establece el nivel de error cuando el estado es Error`() {
        estado_acual = Estados.Error
        nivel_actual = 5

        assertEquals(-1, gestor())
        assertEquals(-1, nivel_actual)
    }

    // ------------------------------------------------------------
    // Idle -> MovingUP / MovingDOWN
    // ------------------------------------------------------------

    @Test
    fun `desde Idle en nivel 0 al pulsar nivel_1 pasa a MovingUP`() {
        nivel_1 = true

        assertEquals(NIVEL_MOVIMIENTO, gestor())
        assertEquals(Estados.MovingUP, estado_acual)
    }

    @Test
    fun `desde Idle en nivel 1 al pulsar nivel_0 pasa a MovingDOWN`() {
        nivel_actual = NIVEL_1
        nivel_0 = true

        assertEquals(NIVEL_MOVIMIENTO, gestor())
        assertEquals(Estados.MovingDOWN, estado_acual)
    }

    @Test
    fun `desde Idle en nivel 0 al pulsar nivel_0 se queda en Idle`() {
        nivel_0 = true

        assertEquals(NIVEL_0, gestor())
        assertEquals(Estados.Idle, estado_acual)
    }

    @Test
    fun `desde Idle en nivel 1 al pulsar nivel_1 se queda en Idle`() {
        nivel_actual = NIVEL_1
        nivel_1 = true

        assertEquals(NIVEL_1, gestor())
        assertEquals(Estados.Idle, estado_acual)
    }

    @Test
    fun `desde Idle sin pulsar nada no cambia de estado ni de nivel`() {
        assertEquals(NIVEL_0, gestor())
        assertEquals(Estados.Idle, estado_acual)

        nivel_actual = NIVEL_1
        assertEquals(NIVEL_1, gestor())
        assertEquals(Estados.Idle, estado_acual)
    }

    @Test
    fun `desde Idle en nivel 0 con los dos botones pulsados sube`() {
        nivel_0 = true
        nivel_1 = true

        gestor()

        assertEquals(Estados.MovingUP, estado_acual)
    }

    @Test
    fun `desde Idle en nivel 1 con los dos botones pulsados baja`() {
        nivel_actual = NIVEL_1
        nivel_0 = true
        nivel_1 = true

        gestor()

        assertEquals(Estados.MovingDOWN, estado_acual)
    }

    @Test
    fun `la llegada no tiene efecto en Idle`() {
        llegada = true

        gestor()

        assertEquals(Estados.Idle, estado_acual)
        assertEquals(NIVEL_0, nivel_actual)
    }

    // ------------------------------------------------------------
    // MovingUP / MovingDOWN -> Idle
    // ------------------------------------------------------------

    @Test
    fun `MovingUP sin llegada sigue en MovingUP`() {
        estado_acual = Estados.MovingUP
        nivel_actual = NIVEL_MOVIMIENTO

        assertEquals(NIVEL_MOVIMIENTO, gestor())
        assertEquals(Estados.MovingUP, estado_acual)
    }

    @Test
    fun `MovingUP con llegada pasa a Idle en el nivel 1`() {
        estado_acual = Estados.MovingUP
        nivel_actual = NIVEL_MOVIMIENTO
        llegada = true

        assertEquals(NIVEL_1, gestor())
        assertEquals(Estados.Idle, estado_acual)
    }

    @Test
    fun `MovingDOWN sin llegada sigue en MovingDOWN`() {
        estado_acual = Estados.MovingDOWN
        nivel_actual = NIVEL_MOVIMIENTO

        assertEquals(NIVEL_MOVIMIENTO, gestor())
        assertEquals(Estados.MovingDOWN, estado_acual)
    }

    @Test
    fun `MovingDOWN con llegada pasa a Idle en el nivel 0`() {
        estado_acual = Estados.MovingDOWN
        nivel_actual = NIVEL_MOVIMIENTO
        llegada = true

        assertEquals(NIVEL_0, gestor())
        assertEquals(Estados.Idle, estado_acual)
    }

    @Test
    fun `la llegada se consume al terminar el movimiento`() {
        estado_acual = Estados.MovingUP
        llegada = true

        gestor()

        assertFalse(llegada)
    }

    @Test
    fun `los botones se ignoran mientras sube`() {
        estado_acual = Estados.MovingUP
        nivel_0 = true
        nivel_1 = true

        assertEquals(NIVEL_MOVIMIENTO, gestor())
        assertEquals(Estados.MovingUP, estado_acual)
    }

    @Test
    fun `los botones se ignoran mientras baja`() {
        estado_acual = Estados.MovingDOWN
        nivel_0 = true
        nivel_1 = true

        assertEquals(NIVEL_MOVIMIENTO, gestor())
        assertEquals(Estados.MovingDOWN, estado_acual)
    }

    @Test
    fun `un boton pulsado mientras se mueve no se acumula para despues`() {
        estado_acual = Estados.MovingUP
        nivel_0 = true
        gestor()

        llegada = true
        gestor()

        // ha llegado arriba y el botón pulsado antes no debe hacerle bajar
        assertEquals(Estados.Idle, estado_acual)
        assertEquals(NIVEL_1, nivel_actual)
        gestor()
        assertEquals(Estados.Idle, estado_acual)
    }

    // ------------------------------------------------------------
    // Fallo -> Error
    // ------------------------------------------------------------

    @Test
    fun `fallo en Idle pasa a Error`() {
        fallo = true

        assertEquals(NIVEL_ERROR, gestor())
        assertEquals(Estados.Error, estado_acual)
    }

    @Test
    fun `fallo en Idle en el nivel 1 pasa a Error`() {
        nivel_actual = NIVEL_1
        fallo = true

        assertEquals(NIVEL_ERROR, gestor())
        assertEquals(Estados.Error, estado_acual)
    }

    @Test
    fun `fallo en MovingUP pasa a Error`() {
        estado_acual = Estados.MovingUP
        nivel_actual = NIVEL_MOVIMIENTO
        fallo = true

        assertEquals(NIVEL_ERROR, gestor())
        assertEquals(Estados.Error, estado_acual)
    }

    @Test
    fun `fallo en MovingDOWN pasa a Error`() {
        estado_acual = Estados.MovingDOWN
        nivel_actual = NIVEL_MOVIMIENTO
        fallo = true

        assertEquals(NIVEL_ERROR, gestor())
        assertEquals(Estados.Error, estado_acual)
    }

    @Test
    fun `el fallo tiene prioridad sobre la llegada`() {
        estado_acual = Estados.MovingUP
        llegada = true
        fallo = true

        gestor()

        assertEquals(Estados.Error, estado_acual)
        assertEquals(NIVEL_ERROR, nivel_actual)
    }

    @Test
    fun `el fallo tiene prioridad sobre los botones`() {
        nivel_1 = true
        fallo = true

        gestor()

        assertEquals(Estados.Error, estado_acual)
    }

    // ------------------------------------------------------------
    // Error -> Error
    // ------------------------------------------------------------

    @Test
    fun `Error se queda en Error aunque se pulsen los botones`() {
        estado_acual = Estados.Error
        nivel_actual = NIVEL_ERROR
        nivel_0 = true
        nivel_1 = true

        assertEquals(NIVEL_ERROR, gestor())
        assertEquals(Estados.Error, estado_acual)
    }

    @Test
    fun `Error se queda en Error aunque llegue la señal de llegada`() {
        estado_acual = Estados.Error
        llegada = true

        assertEquals(NIVEL_ERROR, gestor())
        assertEquals(Estados.Error, estado_acual)
    }

    @Test
    fun `Error se queda en Error aunque desaparezca el fallo`() {
        estado_acual = Estados.Error
        fallo = false

        repeat(5) { gestor() }

        assertEquals(Estados.Error, estado_acual)
        assertEquals(NIVEL_ERROR, nivel_actual)
    }

    // ------------------------------------------------------------
    // Botonera
    // ------------------------------------------------------------

    @Test
    fun `la botonera se limpia despues de cada ciclo`() {
        nivel_0 = true
        nivel_1 = true

        gestor()

        assertFalse(nivel_0)
        assertFalse(nivel_1)
    }

    @Test
    fun `limpiarBotonera pone los dos botones a false`() {
        nivel_0 = true
        nivel_1 = true

        limpiarBotonera()

        assertFalse(nivel_0)
        assertFalse(nivel_1)
    }

    // ------------------------------------------------------------
    // Funciones auxiliares
    // ------------------------------------------------------------

    @Test
    fun `iniciarSubida deja el estado en MovingUP y el nivel en 2`() {
        iniciarSubida()

        assertEquals(Estados.MovingUP, estado_acual)
        assertEquals(NIVEL_MOVIMIENTO, nivel_actual)
    }

    @Test
    fun `iniciarBajada deja el estado en MovingDOWN y el nivel en 2`() {
        nivel_actual = NIVEL_1

        iniciarBajada()

        assertEquals(Estados.MovingDOWN, estado_acual)
        assertEquals(NIVEL_MOVIMIENTO, nivel_actual)
    }

    @Test
    fun `finalizarMovimiento deja Idle en el nivel indicado y consume la llegada`() {
        estado_acual = Estados.MovingUP
        llegada = true

        finalizarMovimiento(NIVEL_1)

        assertEquals(Estados.Idle, estado_acual)
        assertEquals(NIVEL_1, nivel_actual)
        assertFalse(llegada)
    }

    @Test
    fun `activarError deja el estado en Error y el nivel en -1`() {
        activarError()

        assertEquals(Estados.Error, estado_acual)
        assertEquals(NIVEL_ERROR, nivel_actual)
    }

    // ------------------------------------------------------------
    // Recorridos completos
    // ------------------------------------------------------------

    @Test
    fun `recorrido completo subir y bajar`() {
        // sube
        nivel_1 = true
        gestor()
        assertEquals(Estados.MovingUP, estado_acual)
        assertEquals(NIVEL_MOVIMIENTO, nivel_actual)

        gestor()
        assertEquals(Estados.MovingUP, estado_acual)

        llegada = true
        gestor()
        assertEquals(Estados.Idle, estado_acual)
        assertEquals(NIVEL_1, nivel_actual)

        // baja
        nivel_0 = true
        gestor()
        assertEquals(Estados.MovingDOWN, estado_acual)
        assertEquals(NIVEL_MOVIMIENTO, nivel_actual)

        llegada = true
        gestor()
        assertEquals(Estados.Idle, estado_acual)
        assertEquals(NIVEL_0, nivel_actual)
    }

    @Test
    fun `recorrido con fallo a mitad de la subida`() {
        nivel_1 = true
        gestor()
        assertEquals(Estados.MovingUP, estado_acual)

        fallo = true
        gestor()
        assertEquals(Estados.Error, estado_acual)
        assertEquals(NIVEL_ERROR, nivel_actual)

        // ya no se mueve más
        fallo = false
        nivel_0 = true
        llegada = true
        gestor()
        assertEquals(Estados.Error, estado_acual)
    }

    @Test
    fun `varios viajes seguidos terminan en el nivel de partida`() {
        repeat(3) {
            nivel_1 = true
            gestor()
            llegada = true
            gestor()
            assertEquals(NIVEL_1, nivel_actual)

            nivel_0 = true
            gestor()
            llegada = true
            gestor()
            assertEquals(NIVEL_0, nivel_actual)
        }
        assertEquals(Estados.Idle, estado_acual)
    }

    // ------------------------------------------------------------
    // Estados (sealed class)
    // ------------------------------------------------------------

    @Test
    fun `toString de los estados devuelve su nombre`() {
        assertEquals("Idle", Estados.Idle.toString())
        assertEquals("MovingUP", Estados.MovingUP.toString())
        assertEquals("MovingDOWN", Estados.MovingDOWN.toString())
        assertEquals("Error", Estados.Error.toString())
    }

    @Test
    fun `los valores de nivel son los que pide el enunciado`() {
        assertEquals(0, NIVEL_0)
        assertEquals(1, NIVEL_1)
        assertEquals(2, NIVEL_MOVIMIENTO)
        assertEquals(-1, NIVEL_ERROR)
        assertTrue(estado_acual is Estados.Idle)
    }
}
