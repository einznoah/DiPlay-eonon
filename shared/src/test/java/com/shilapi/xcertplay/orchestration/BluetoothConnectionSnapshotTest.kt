package com.shilapi.xcertplay.orchestration

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class BluetoothConnectionSnapshotTest {
    // Mirrors the report redactor rule that drops a whole line.
    private val droppedByRedactor = Regex("(?i)(phone|device|peer|host)?name[=:]")

    @Test fun snapshotKeepsBondAndServiceEvidenceInAFormTheReportRedactorKeeps() {
        val line = BluetoothConnectionSnapshot.describe(
            point = "before-connect",
            enabled = true,
            bondState = 12,
            aclConnected = true,
            cachedServicesReadable = true,
            cachedServiceCount = 5,
            cachedIap2Service = false,
        )

        assertEquals(
            "Bluetooth snapshot point=before-connect enabled=true bondState=12 bond=BONDED aclConnected=true " +
                "cachedServicesReadable=true cachedServiceCount=5 cachedIap2Service=false",
            line,
        )
        assertFalse(droppedByRedactor.containsMatchIn(line))
    }

    @Test fun unreadableServiceCacheAndUnknownBondAreReportedWithoutGuessing() {
        val line = BluetoothConnectionSnapshot.describe("after-failure", null, 99, false, false, null, null)

        assertEquals(
            "Bluetooth snapshot point=after-failure enabled=null bondState=99 bond=UNKNOWN aclConnected=false " +
                "cachedServicesReadable=false cachedServiceCount=unknown cachedIap2Service=unknown",
            line,
        )
    }

    @Test fun selectionLineCarriesCountsButNoDeviceLabel() {
        val explicit = BluetoothConnectionSnapshot.describeSelection(explicit = true, bonded = 3, null, null)
        val automatic = BluetoothConnectionSnapshot.describeSelection(explicit = false, bonded = 3, 1, 1)

        assertEquals("Bluetooth selection explicit=true bonded=3 bondedIPhones=not_checked directlyConnected=not_checked", explicit)
        assertEquals("Bluetooth selection explicit=false bonded=3 bondedIPhones=1 directlyConnected=1", automatic)
        assertFalse(droppedByRedactor.containsMatchIn(explicit + automatic))
    }
}
