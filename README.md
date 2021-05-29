# ErrorCollector Logback-classic 

This projects aims to provide a convenient way to be notified if an error in on of your systems occurs.

The appender will collect all errors in a configurable interval (default 5 min).

If exception occurred will send them to the configured publisher(Microsoft Teams and Telegram are currently supported)

