# Overview

Simulate a [Wavefront](https://www.wavefront.com/) agent which accepts Wavefront formatted metrics on a TCP Port.  Based on [Spring Integration][]'s excellent [TCP/IP Server][] support.

Useful for validating a stream of metrics in a high volume environment.

# Usage

Build the uberjar with the following command:

`./mvnw clean package`

Then run the jar with `java -jar ./target/metrics-validator-0.0.1-SNAPSHOT.jar`

This then runs the service on port *2878*.  Point your services that report to Wavefront at this service and it will keep track of the points it receives and which ones fail validation.

## Connecting

You can connect via tcp using the `nc localhost 2878` command which will connect to the validator on port 2878 of your localhost.  Once connected, you can just enter metrics into the terminal prompt, separated by a new line.

# Results

Valid/Invalid request rates can be viewed on the `/metrics` endpoint.  For a list of the most recent 32 invalid metrics, the `/bad-metrics` endpoint can be used.

# License

This utility is released under version 2.0 of the [Apache License][].

[Spring Integration]: http://projects.spring.io/spring-integration/
[TCP/IP Server]: http://docs.spring.io/spring-integration/docs/4.2.2.RELEASE/reference/html/ip.html
[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
