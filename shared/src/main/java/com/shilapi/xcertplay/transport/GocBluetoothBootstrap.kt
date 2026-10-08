package com.shilapi.xcertplay.transport

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Opens the iPhone iAP2 RFCOMM channel on head units whose Bluetooth runs in a Goodocom module.
 *
 * On these units the vendor framework routes `BluetoothSocket.connect()` to the module's data
 * socket and passes only the device address. The module then searches for the generic serial
 * service, which an iPhone does not offer, and drops the socket before any byte arrives.
 * Asking the module for the iAP2 service first makes the same socket attach to that channel.
 *
 * Observed on an Android 12 Allwinner unit with module firmware GBTS_AG440: the control
 * terminal accepts `AT#SP<address><uuid>` and the following socket reports "spp already
 * connected". Other firmware has not been tested.
 */
class GocBluetoothBootstrap(
    private val controlTerminal: File = File(CONTROL_TERMINAL_PATH),
    private val dataSocket: File = File(DATA_SOCKET_PATH),
    private val write: (File, ByteArray) -> Unit = { file, bytes ->
        FileOutputStream(file).use { it.write(bytes) }
    },
) {
    /** True only when both vendor endpoints exist; an ordinary Android Bluetooth stack has neither. */
    fun isAvailable(): Boolean = controlTerminal.exists() && dataSocket.exists()

    /** Requests the iAP2 channel. The module gives no reply that an app can read without taking it from the vendor service. */
    @Throws(IOException::class)
    fun requestIap2Channel(address: String) {
        write(controlTerminal, connectCommand(address).toByteArray(Charsets.US_ASCII))
    }

    companion object {
        const val CONTROL_TERMINAL_PATH = "/dev/goc_serial"
        const val DATA_SOCKET_PATH = "/dev/socket/goc_spp"

        /** The module needed about 650 ms to open the channel; the socket must not attach earlier. */
        const val CHANNEL_SETTLE_MILLIS = 1_500L

        private const val IAP2_SERVICE = "00000000-deca-fade-deca-deafdecacafe"
        private val bluetoothAddress = Regex("(?:[0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}")

        /** The address is validated because the text goes to a command terminal. */
        fun connectCommand(address: String): String {
            require(bluetoothAddress.matches(address)) { "Not a Bluetooth address" }
            return "AT#SP${address.replace(":", "").uppercase()}$IAP2_SERVICE\r\n"
        }
    }
}
