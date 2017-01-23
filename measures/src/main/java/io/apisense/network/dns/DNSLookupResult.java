package io.apisense.network.dns;

import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.apisense.network.MeasurementResult;

/**
 * Class containing result of {@link DNSLookupTask}
 */
public class DNSLookupResult extends MeasurementResult {

  /**
   * DNS response code for the query.
   */
  private final String responseCode;
  /**
   * Is the response truncated?
   */
  private final boolean truncated;
  /**
   * Report configuration used on this query.
   */
  private DNSLookupConfig configuration;
  /**
   * List of the actual DNS records for the queried domain.
   */
  private List<DNSRecord> records;


  public DNSLookupResult(DNSLookupConfig configuration, long startTime, long endTime,
                         String rCode, boolean tc, List<Record> records) {
    super(DNSLookupTask.TAG, startTime, endTime);
    this.configuration = configuration;
    this.responseCode = rCode;
    this.truncated = tc;
    this.records = new ArrayList<>();
    for (Record record : records) {
      this.records.add(new DNSRecord(record));
    }
  }

  static DNSLookupResult fromMessage(DNSLookupConfig config, Message response, long startTime, long endTime) {
    return new DNSLookupResult(config,
        startTime, endTime,
        Rcode.string(response.getHeader().getRcode()),
        response.getHeader().getFlag(Flags.TC),
        Arrays.asList(response.getSectionArray(1))
    );
  }

  public DNSLookupConfig getConfiguration() {
    return configuration;
  }

  public String getResponseCode() {
    return responseCode;
  }

  public boolean isTruncated() {
    return truncated;
  }

  public List<DNSRecord> getRecords() {
    return records;
  }

  @Override
  public String toString() {
    return "DNSLookupResult{" +
        "configuration=" + configuration +
        ", responseCode='" + responseCode + '\'' +
        ", truncated=" + truncated +
        ", records=" + records +
        '}';
  }
}
