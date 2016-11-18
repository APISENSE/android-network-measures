package io.apisense.network.dns;

/**
 * This exception indicated that the DNS query was over UDP
 * and the response is truncated.
 *
 * The query then will have to be made again by TCP.
 */
class TruncatedException extends Exception {
}
