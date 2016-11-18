package io.apisense.network;

/**
 * Exception to throw when constraints are not satisfied in a measurement config
 */

public class MeasurementConfigException extends Exception {
    public MeasurementConfigException(String reason) {
        super(reason);
    }

    public MeasurementConfigException(Exception cause) {
        super(cause);
    }
}
