package com.lse.admin.aws;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.ion.IonValue;
import com.lse.admin.model.Product;

import software.amazon.qldb.QldbSession;
import software.amazon.qldb.TransactionExecutor;

public class RegisterProductService {

  public static final Logger log = LoggerFactory.getLogger(RegisterProductService.class);

  public static List<String> insertDocuments(final TransactionExecutor txn, final String tableName, final Object document) {
    log.info("Inserting some documents in the {} table...", tableName);
    try {
      final String query = String.format("INSERT INTO %s ?", tableName);
      final IonValue ionDocument = Constants.MAPPER.writeValueAsIonValue(document);
      final List<IonValue> parameters = Collections.singletonList(ionDocument);
      return QldbHelper.getDocumentIdsFromDmlResult(txn.execute(query, parameters));
    } catch (IOException ioe) {
      throw new IllegalStateException(ioe);
    }
  }

  public static void execute(Product product) {
    // Product product = new Product(UUID.randomUUID().toString(), "DUMMY-1", "DUMMY-1", "DUMMY-1", 5, QldbHelper.convertToLocalDate("2023-09-25"), "ABC");

    try (QldbSession qldbSession = ConnectToLedger.createQldbSession()) {

      qldbSession.execute(txn -> {
        List<String> documentIds = insertDocuments(txn, Constants.PRODUCT_TABLE_NAME, product);
      }, (retryAttempt) -> log.info("Retrying due to OCC conflict..."));
      log.info("Documents inserted successfully!");
    } catch (Exception e) {
      log.error("Error inserting or updating documents.", e);
    }
  }

  public static void execute(List<Product> product) {
    product.stream().forEach(new Consumer<Product>() {

      @Override
      public void accept(Product t) {
        execute(t);
      }
    });
  }

}
