# Android Network measures

We aim to provide a minimalistic library to perform network measures on Android. 


## Available tests

Currently there are 7 available test types:

- DNS lookup
- Ping
- Traceroute
- TCP Download
- TCP Upload
- UDP Download
- UDP Upload

Thoses tests are for the most heavily inspired from [Mobilyzer](https://github.com/mobilyzer/Mobilyzer),
but easier to use (at least we hope).

##  Usage example

### Require dependency

#### Maven

    <dependency>
        <groupId>io.apisense.network</groupId>
        <artifactId>android-network-measures</artifactId>
        <version>1.1.0</version>
    </dependency>

#### Gradle

    compile 'io.apisense.network:android-network-measures:1.1.0'
    
### Call a measurement

Here is an example of a DNS test:

    import io.apisense.network.dns.DNSLookupConfig;
    import io.apisense.network.dns.DNSLookupTask;
    import io.apisense.network.MeasurementCallback;
    import io.apisense.network.MeasurementResult;
    import io.apisense.network.MeasurementError;

    DNSLookupConfig config = new DNSLookupConfig("www.google.com"); // Mandatory configurations
    config.setServer("8.8.8.8"); // Every optional configurations are accessible via setters

    DNSLookupTask dnsLookup = new DNSLookupTask(config);
    dnsLookup.call(new MeasurementCallback() { // Measurement is processed in an AsyncTask
        // Callback is executed on UI thread
        public void onResult(MeasurementResult result) {
            // ...
        }

        public void onError(MeasurementError error) {
            // ...
         }
    });
