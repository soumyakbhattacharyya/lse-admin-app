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

public class ComputeProfitSplitService {

  public static final Logger log = LoggerFactory.getLogger(ComputeProfitSplitService.class);

  public static List<String> updateSoldAt(final TransactionExecutor txn, final int productSKU, final double soldAt, String buyerName, String buyerCellNumber,
      Double numberOfInstallments, Double currentInstallmentNumber, Double currentInstallmentAmount) {
    try {
      log.info("Updating sold at cost for the product: {}...", productSKU);

      List<String> docIds = new ArrayList<>();

      final String query = "UPDATE Products AS d SET d.soldAt = ?, d.buyerName = ?, d.buyerCellNumber = ?, d.numberOfInstallments = ?, d.currentInstallmentNumber = ?, d.currentInstallmentAmount = ? "
          + "WHERE d.code = ?";
      final List<IonValue> parameters = new ArrayList<>();

      parameters.add(Constants.MAPPER.writeValueAsIonValue(soldAt));
      parameters.add(Constants.MAPPER.writeValueAsIonValue(buyerName));
      parameters.add(Constants.MAPPER.writeValueAsIonValue(buyerCellNumber));
      parameters.add(Constants.MAPPER.writeValueAsIonValue(numberOfInstallments));
      parameters.add(Constants.MAPPER.writeValueAsIonValue(currentInstallmentNumber));
      parameters.add(Constants.MAPPER.writeValueAsIonValue(currentInstallmentAmount));
      parameters.add(Constants.MAPPER.writeValueAsIonValue(productSKU));
      Result result = txn.execute(query, parameters);
      if (!result.isEmpty()) {
        docIds = QldbHelper.getDocumentIdsFromDmlResult(result);
        return docIds;
      }
    } catch (IOException ioe) {
      throw new IllegalStateException(ioe);
    }
    throw new AssertionError("Unreachable state, while attempting updating.");
  }

  public static List<String> exceute(final int productSKU, final double soldAt, String buyerName, String buyerCellNumber, Double numberOfInstallments,
      Double currentInstallmentNumber, Double currentInstallmentAmount) {
    List<String> listOfProductsUpdated = new ArrayList<>();
    try (QldbSession qldbSession = ConnectToLedger.createQldbSession()) {
      qldbSession.execute(txn -> {
        final Product product = getProductdFromSKUNumber(txn, productSKU);
        log.info("Proceeding to update product : " + product);
        List<String> temp = updateSoldAt(txn, productSKU, soldAt, buyerName, buyerCellNumber, numberOfInstallments, currentInstallmentNumber, currentInstallmentAmount);
        temp.stream().forEach(new Consumer<String>() {

          @Override
          public void accept(String t) {
            listOfProductsUpdated.add(t);

          }
        });

      }, (retryAttempt) -> log.info("Retrying due to OCC conflict..."));
    } catch (Exception e) {
      log.error("Error renewing drivers license.", e);
    }
    return listOfProductsUpdated;
  }

  public static Product getProductdFromSKUNumber(final TransactionExecutor txn, final int productSKU) {
    try {
      log.info("Finding product with product SKU: {}...", productSKU);
      final String query = "SELECT * from Products where code =  ?";
      final List<IonValue> parameters = Collections.singletonList(Constants.MAPPER.writeValueAsIonValue(productSKU));
      final Result result = txn.execute(query, parameters);
      if (result.isEmpty()) {
        throw new IllegalStateException("Unable to find product with SKU number: " + productSKU);
      }
      return Constants.MAPPER.readValue(result.iterator().next(), Product.class);
    } catch (IOException ioe) {
      throw new IllegalStateException(ioe);
    }
  }

  public static void main(String[] args) {

    // List<Product> descrition = exceute(19093, 9000.05);
    // System.out.println(descrition);

  }

}
