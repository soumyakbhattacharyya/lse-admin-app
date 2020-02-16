package com.lse.admin.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.qldbsession.AmazonQLDBSessionClientBuilder;

import software.amazon.qldb.PooledQldbDriver;
import software.amazon.qldb.QldbDriver;
import software.amazon.qldb.QldbSession;
import software.amazon.qldb.exceptions.QldbClientException;

public final class ConnectToLedger {
  public static final Logger log = LoggerFactory.getLogger(ConnectToLedger.class);
  public static AWSCredentialsProvider credentialsProvider;
  public static String endpoint = null;
  public static String ledgerName = Constants.LEDGER_NAME;
  public static String region = null;

  public static PooledQldbDriver driver = createQldbDriver();

  private ConnectToLedger() {
  }

  /**
   * Create a pooled driver for creating sessions.
   *
   * @return The pooled driver for creating sessions.
   */
  public static PooledQldbDriver createQldbDriver() {
    AmazonQLDBSessionClientBuilder builder = AmazonQLDBSessionClientBuilder.standard();
    if (null != endpoint && null != region) {
      builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region));
    }
    if (null != credentialsProvider) {
      builder.setCredentials(credentialsProvider);
    }
    return PooledQldbDriver.builder().withLedger(ledgerName).withRetryLimit(Constants.RETRY_LIMIT).withSessionClientBuilder(builder).build();
  }

  /**
   * Connect to a ledger through a {@link QldbDriver}.
   *
   * @return {@link QldbSession}.
   */
  public static QldbSession createQldbSession() {
    return driver.getSession();
  }

  public static String listTables() {
    StringBuilder builder = new StringBuilder();
    try (QldbSession qldbSession = createQldbSession()) {
      log.info("Listing table names ");
      for (String tableName : qldbSession.getTableNames()) {
        builder.append(tableName);
        builder.append("\n");
      }
    } catch (QldbClientException e) {
      log.error("Unable to create session.", e);
    }
    return builder.toString();
  }

  public static void main(final String... args) {
    try (QldbSession qldbSession = createQldbSession()) {
      log.info("Listing table names ");
      for (String tableName : qldbSession.getTableNames()) {
        log.info(tableName);
      }
    } catch (QldbClientException e) {
      log.error("Unable to create session.", e);
    }
  }
}
