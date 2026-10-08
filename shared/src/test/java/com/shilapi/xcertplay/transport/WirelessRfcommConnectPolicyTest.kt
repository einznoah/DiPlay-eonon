package com.shilapi.xcertplay.transport

import com.shilapi.xcertplay.transport.WirelessRfcommConnectPolicy.Mode
import org.junit.Assert.assertEquals
import org.junit.Test

class WirelessRfcommConnectPolicyTest {
    @Test fun aSingleSilentChannelKeepsTheDefaultSecureSocket() {
        val policy = WirelessRfcommConnectPolicy()

        assertEquals(Mode.SECURE, policy.mode())
        policy.onEarlyReadFailure()

        assertEquals(Mode.SECURE, policy.mode())
    }

    @Test fun repeatedSilentChannelsAlternateBetweenSocketTypes() {
        val policy = WirelessRfcommConnectPolicy()

        repeat(2) { policy.onEarlyReadFailure() }
        assertEquals(Mode.INSECURE, policy.mode())
        policy.onEarlyReadFailure()
        assertEquals(Mode.INSECURE, policy.mode())
        policy.onEarlyReadFailure()
        assertEquals(Mode.SECURE, policy.mode())
    }

    @Test fun aModeThatCarriedDataIsKeptAndItsFailureCountRestarts() {
        val policy = WirelessRfcommConnectPolicy()
        repeat(2) { policy.onEarlyReadFailure() }

        policy.onEarlyReadFailure()
        policy.onBytesReceived()
        policy.onEarlyReadFailure()

        assertEquals(Mode.INSECURE, policy.mode())
    }
}
