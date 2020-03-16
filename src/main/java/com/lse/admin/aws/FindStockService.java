package com.lse.admin.aws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.ion.IonValue;
import com.lse.admin.model.Product;

import software.amazon.qldb.QldbSession;
import software.amazon.qldb.Result;
import software.amazon.qldb.TransactionExecutor;

public class FindStockService {

  public static final Logger log = LoggerFactory.getLogger(FindStockService.class);

  public static List<Product> exceute(String status) {
    List<Product> searchResult = new ArrayList<>();
    try (QldbSession qldbSession = ConnectToLedger.createQldbSession()) {
      qldbSession.execute(txn -> {
        final List<Product> products = getProductdByStatus(txn, status);
        products.forEach(new Consumer<Product>() {

          @Override
          public void accept(Product t) {
            searchResult.add(t);

          }
        });

      }, (retryAttempt) -> log.info("Retrying due to OCC conflict..."));
    } catch (Exception e) {
      log.error("Error renewing drivers license.", e);
    }
    return searchResult;
  }

  public static List<Product> getProductdByStatus(final TransactionExecutor txn, final String status) {
    try {
      log.info("Finding product with status: {}...", status);
      String query = "";
      if ("NEW".equalsIgnoreCase(status)) {
        query = "SELECT * from Products where productStatus in (null, ?)";
      } else {
        query = "SELECT * from Products where productStatus = ?";
      }

      final List<IonValue> parameters = Collections.singletonList(Constants.MAPPER.writeValueAsIonValue(status));
      final Result result = txn.execute(query, parameters);
      if (result.isEmpty()) {
        throw new IllegalStateException("Unable to find product with status: " + status);
      }
      final List<Product> products = new ArrayList<>();
      result.iterator().forEachRemaining(row -> products.add(getProduct(row)));
      return products;
    } catch (IOException ioe) {
      throw new IllegalStateException(ioe);
    }
  }

  public static Product getProduct(final IonValue dmlResultDocument) {
    try {
      Product result = Constants.MAPPER.readValue(dmlResultDocument, Product.class);
      return result;
    } catch (IOException ioe) {
      throw new IllegalStateException(ioe);
    }
  }

  public static Product getProductdByType(final TransactionExecutor txn, final String type) {
    try {
      log.info("Finding product with type: {}...", type);
      final String query = "SELECT * from Products where type = ?";
      final List<IonValue> parameters = Collections.singletonList(Constants.MAPPER.writeValueAsIonValue(type));
      final Result result = txn.execute(query, parameters);
      if (result.isEmpty()) {
        throw new IllegalStateException("Unable to find product with type: " + type);
      }
      return Constants.MAPPER.readValue(result.iterator().next(), Product.class);
    } catch (IOException ioe) {
      throw new IllegalStateException(ioe);
    }
  }

  public static void main(String[] args) {

    List<Product> p = exceute("SOLD");

    System.out.println(p);
  }

}
