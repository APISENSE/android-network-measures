package io.apisense.network.ping;

/**
 * Configuration class for ICMP
 */

public class ICMPConfig {
    /**
     * URL of the host to ping
     */
    private String url;

    /**
     * Time to live of the ICMP packet
     */
    private int ttl;

    public ICMPConfig(String url) {
        this.url = url;
        this.ttl = 42;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    @Override
    public String toString() {
        return "ICMPConfig{" +
                "url='" + url + '\'' +
                ", ttl=" + ttl +
                '}';
    }
}
