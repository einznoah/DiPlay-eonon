# DiPlay next release notes

Changes through DiPlay 0.2.15 are documented in [0.2.15 release notes](RELEASE-NOTES-0.2.15.md). Measured checks are in [VALIDATION.md](VALIDATION.md). Device acceptance and remaining failure families are tracked in [connection reliability validation](CONNECTION_RELIABILITY.md).

## Wireless Bluetooth bootstrap

- Diagnostic reports now keep the Bluetooth snapshot line (bond state, ACL state, cached iAP2 service). A `bondName=` key matched the report redactor's device-name rule and removed the whole line from every saved report. Reports also record how the iPhone was selected and a bounded cause token for a failed read (`causeKind=`).
- When the iPhone's Bluetooth channel opens but never delivers a byte, the connection screen now says that another CarPlay app on the head unit may be using the channel, instead of the generic "iPhone isn't available" text.
