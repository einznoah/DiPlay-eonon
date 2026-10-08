# DiPlay next release notes

Changes through DiPlay 0.2.15 are documented in [0.2.15 release notes](RELEASE-NOTES-0.2.15.md). Measured checks are in [VALIDATION.md](VALIDATION.md). Device acceptance and remaining failure families are tracked in [connection reliability validation](CONNECTION_RELIABILITY.md).

## Wireless Bluetooth bootstrap

- Diagnostic reports now keep the Bluetooth snapshot line (bond state, ACL state, cached iAP2 service). A `bondName=` key matched the report redactor's device-name rule and removed the whole line from every saved report. Reports also record how the iPhone was selected and a bounded cause token for a failed read (`causeKind=`).
- Head units whose Bluetooth runs in a Goodocom module (seen on an Android 12 Allwinner unit, module firmware `GBTS_AG440`) can now start wireless CarPlay. Their vendor framework sends only the phone's address to the module, which then looks for a serial service the iPhone does not have and drops the socket. DiPlay now asks the module for the iPhone's iAP2 service first, when the module's control terminal and data socket are present. On that unit the log shows iAP2 identification, MFi authentication and the Wi-Fi configuration exchange completing over this path, and the owner reports a working wireless session with the built-in car hotspot. With Wi-Fi Direct the iPhone did not join the group. Other module firmware is untested.
- When the iPhone's Bluetooth channel opens but never delivers a byte, the connection screen now says that another CarPlay app on the head unit may be using the channel, instead of the generic "iPhone isn't available" text.
