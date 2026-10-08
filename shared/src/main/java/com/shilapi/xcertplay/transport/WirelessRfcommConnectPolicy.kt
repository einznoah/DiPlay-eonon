package com.shilapi.xcertplay.transport

/**
 * Chooses how the next wireless bootstrap opens RFCOMM to the iPhone.
 *
 * Some vendor Bluetooth stacks report a connected socket that never carries a byte. Switching
 * the socket type after repeated early failures is a compatibility hypothesis, not a proven
 * repair, so every attempt logs the mode it used.
 */
class WirelessRfcommConnectPolicy {
    enum class Mode { SECURE, INSECURE }

    private var mode = Mode.SECURE
    private var earlyReadFailures = 0

    @Synchronized fun mode(): Mode = mode

    /** A socket opened and then failed its first read without delivering a byte. */
    @Synchronized fun onEarlyReadFailure() {
        earlyReadFailures++
        if (earlyReadFailures < EARLY_FAILURES_BEFORE_SWITCH) return
        earlyReadFailures = 0
        mode = if (mode == Mode.SECURE) Mode.INSECURE else Mode.SECURE
    }

    /** The current mode carried data, so later failures are not evidence against it. */
    @Synchronized fun onBytesReceived() {
        earlyReadFailures = 0
    }

    companion object {
        const val EARLY_FAILURES_BEFORE_SWITCH = 2

        /** Each connection attempt builds a new controller, so the state lives with the process. */
        val process = WirelessRfcommConnectPolicy()
    }
}
