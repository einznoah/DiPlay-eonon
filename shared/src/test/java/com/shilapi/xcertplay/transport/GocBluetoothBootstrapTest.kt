package com.shilapi.xcertplay.transport

import java.io.File
import java.nio.file.Files
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class GocBluetoothBootstrapTest {
    @Test fun commandCarriesTheAddressWithoutSeparatorsAndTheIap2Service() {
        assertEquals(
            "AT#SPA1B2C3D4E5F600000000-deca-fade-deca-deafdecacafe\r\n",
            GocBluetoothBootstrap.connectCommand("a1:b2:C3:D4:e5:f6"),
        )
    }

    @Test fun anythingThatIsNotABluetoothAddressNeverReachesTheCommandTerminal() {
        for (address in listOf("", "A1B2C3D4E5F6", "A1:B2:C3:D4:E5", "A1:B2:C3:D4:E5:F6\r\nAT#CW", "A1:B2:C3:D4:E5:ZZ")) {
            assertThrows(address, IllegalArgumentException::class.java) {
                GocBluetoothBootstrap.connectCommand(address)
            }
        }
    }

    @Test fun ordinaryHeadUnitsWithoutBothVendorEndpointsAreLeftAlone() {
        val folder = Files.createTempDirectory("goc-bootstrap").toFile()
        try {
            val terminal = File(folder, "goc_serial")
            val socket = File(folder, "goc_spp")
            val bootstrap = GocBluetoothBootstrap(terminal, socket)

            assertFalse(bootstrap.isAvailable())
            terminal.createNewFile()
            assertFalse(bootstrap.isAvailable())
            socket.createNewFile()
            assertTrue(bootstrap.isAvailable())
        } finally { folder.deleteRecursively() }
    }

    @Test fun requestWritesExactlyOneCommandToTheControlTerminal() {
        val terminal = File("terminal")
        val writes = mutableListOf<Pair<File, ByteArray>>()
        val bootstrap = GocBluetoothBootstrap(terminal, File("socket")) { file, bytes -> writes += file to bytes }

        bootstrap.requestIap2Channel("A1:B2:C3:D4:E5:F6")

        assertEquals(1, writes.size)
        assertSame(terminal, writes.single().first)
        assertArrayEquals(
            "AT#SPA1B2C3D4E5F600000000-deca-fade-deca-deafdecacafe\r\n".toByteArray(Charsets.US_ASCII),
            writes.single().second,
        )
    }
}
