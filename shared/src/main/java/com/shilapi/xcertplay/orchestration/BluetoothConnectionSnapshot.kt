package com.shilapi.xcertplay.orchestration

import android.bluetooth.BluetoothDevice

/**
 * Formats Bluetooth state for diagnostic reports.
 *
 * The report redactor drops every line that has a key ending in `name=`, so no key here may end
 * that way. A previous `bondName=` key removed this whole line from saved reports.
 */
internal object BluetoothConnectionSnapshot {
    fun describe(
        point: String,
        enabled: Boolean?,
        bondState: Int,
        aclConnected: Boolean,
        cachedServicesReadable: Boolean,
        cachedServiceCount: Int?,
        cachedIap2Service: Boolean?,
    ): String {
        val bond = when (bondState) {
            BluetoothDevice.BOND_NONE -> "NONE"
            BluetoothDevice.BOND_BONDING -> "BONDING"
            BluetoothDevice.BOND_BONDED -> "BONDED"
            else -> "UNKNOWN"
        }
        return "Bluetooth snapshot point=$point enabled=$enabled " +
            "bondState=$bondState bond=$bond aclConnected=$aclConnected " +
            "cachedServicesReadable=$cachedServicesReadable " +
            "cachedServiceCount=${cachedServiceCount ?: "unknown"} " +
            "cachedIap2Service=${cachedIap2Service ?: "unknown"}"
    }

    fun describeSelection(explicit: Boolean, bonded: Int, bondedIPhones: Int?, directlyConnected: Int?): String =
        "Bluetooth selection explicit=$explicit bonded=$bonded " +
            "bondedIPhones=${bondedIPhones ?: "not_checked"} " +
            "directlyConnected=${directlyConnected ?: "not_checked"}"
}
