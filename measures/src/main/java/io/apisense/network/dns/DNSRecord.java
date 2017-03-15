package io.apisense.network.dns;

import org.xbill.DNS.DClass;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

/**
 * Definition of a DNS record.
 */
public class DNSRecord {

  /**
   * The recorded server name
   */
  public final String name;

  /**
   * The Record Type as String
   */
  public final String type;

  /**
   * The Record Class as String
   */
  public final String dclass;

  /**
   * The recorded Time To Live
   */
  public final long ttl;

  DNSRecord(Record record) {
    name = record.getName().toString();
    type = Type.string(record.getType());
    dclass = DClass.string(record.getDClass());
    ttl = record.getTTL();
  }

  @Override
  public String toString() {
    return "DNSRecord{" +
        "name='" + name + '\'' +
        ", type='" + type + '\'' +
        ", dclass='" + dclass + '\'' +
        ", ttl=" + ttl +
        '}';
  }
}
