package com.lse.admin.command;

import java.util.List;

import org.jline.reader.LineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;

import com.lse.admin.aws.UpdateProductService;
import com.lse.admin.model.ProductStatus;

@ShellComponent
public class UpdateSalesInformation {

  @Autowired
  @Lazy
  private LineReader lineReader;

  public static final Logger log = LoggerFactory.getLogger(UpdateSalesInformation.class);

  @ShellMethod(value = "Register sales detail about a product.", key = "register-sale")
  public String update() {

    // 1. read product sku--------------------------------------------
    Integer productSKU = 0;

    do {
      String productSKU_ = lineReader.readLine("Product SKU: ");
      if (StringUtils.hasText(productSKU_)) {
        try {
          productSKU = Integer.parseInt(productSKU_);
        } catch (NumberFormatException e) {
          log.error("Please provide an integer value");
        }

      } else {
        log.error("Invalid product sku, did you enter empty string?");
      }
    } while (productSKU.intValue() == 0);

    // 2. read product sold price--------------------------------------------
    Double soldAt = 0.0;
    do {
      String soldAt_ = lineReader.readLine("Sold At (Type RESET to 0):");
      if (StringUtils.hasText(soldAt_) && !soldAt_.equalsIgnoreCase("RESET")) {
        try {
          soldAt = Double.parseDouble(soldAt_);
        } catch (NumberFormatException e) {
          log.error("Please provide a double value");
        }

      } else if (StringUtils.hasText(soldAt_) && soldAt_.equalsIgnoreCase("RESET")) {
        break;
      } else {
        log.error("Invalid sold at value, did you enter empty string?");
      }
    } while (soldAt.doubleValue() == 0.0);

    // 3. read buyer name --------------------------------------------
    String buyerName = "NA";
    String buyerName_ = lineReader.readLine("Buyer Name:");
    if (StringUtils.hasText(buyerName_) && !buyerName_.equalsIgnoreCase("NA")) {
      buyerName = buyerName_;
    } else {
      log.info("Defaulting to NA for Buyer's Name!");
    }

    // 4. read buyer cell number --------------------------------------------
    String buyerCellNumber = "NA";
    String buyerCellNumber_ = lineReader.readLine("Buyer Cell Number:");
    if (StringUtils.hasText(buyerCellNumber_) && !buyerCellNumber.equalsIgnoreCase("NA")) {
      buyerCellNumber = buyerCellNumber_;
    } else {
      log.info("Defaulting to NA for Buyer Cell Number!");
    }

    // 5. read number of installments--------------------------------------------
    Double numberOfInstallments = 0.0;
    do {
      String numberOfInstallments_ = lineReader.readLine("Number Of Granted Installment(s) - NA for none :");
      if (StringUtils.hasText(numberOfInstallments_) && !numberOfInstallments_.equalsIgnoreCase("NA")) {
        try {
          numberOfInstallments = Double.parseDouble(numberOfInstallments_);
          log.info("You have provided following number of granted installments - " + numberOfInstallments);
        } catch (NumberFormatException e) {
          log.error("Invalid value for number of installments provided.");
        }
      } else {
        log.info("Defaulting to 0 for Number of Granted Installments!");
        break;
      }
    } while (numberOfInstallments.doubleValue() == 0.0);

    // 6. read current installment number --------------------------------------------
    Double currentInstallmentNumber = 0.0;
    do {
      String currentInstallmentNumber_ = lineReader.readLine("Current Installment Number - NA for none :");
      if (StringUtils.hasText(currentInstallmentNumber_) && !currentInstallmentNumber_.equalsIgnoreCase("NA")) {
        try {
          currentInstallmentNumber = Double.parseDouble(currentInstallmentNumber_);
          log.info("You have provided following value for Current Installment Number - " + currentInstallmentNumber);
        } catch (NumberFormatException e) {
          log.error("Invalid value for current installment number provided.");
        }
      } else {
        log.info("Defaulting to 0 for Current Installment Number!");
        break;
      }
    } while (currentInstallmentNumber.doubleValue() == 0.0);

    // 7. read current installment amount --------------------------------------------
    Double currentInstallmentAmount = 0.0;
    do {
      String currentInstallmentAmount_ = lineReader.readLine("Current Installment Amount - NA for none :");
      if (StringUtils.hasText(currentInstallmentAmount_) && !currentInstallmentAmount_.equalsIgnoreCase("NA")) {
        try {
          currentInstallmentAmount = Double.parseDouble(currentInstallmentAmount_);
          log.info("You have provided following value for Current Installment Amount - " + currentInstallmentAmount);
        } catch (NumberFormatException e) {
          log.error("Invalid value for current installment amount provided.");
        }
      } else {
        log.info("Defaulting to 0 for Current Installment Amount - proceeding");
        break;
      }
    } while (currentInstallmentAmount.doubleValue() == 0.0);

    // 8. read status --------------------------------------------
    ProductStatus productStatus = ProductStatus.NEW;
    String productStatus_ = lineReader.readLine("Status (Default is NEW):");
    if (StringUtils.hasText(productStatus_) && ProductStatus.valueOf(productStatus_) != ProductStatus.NEW) {
      productStatus = ProductStatus.valueOf(productStatus_);
    } else {
      log.info("Defaulting to NEW for Buyer Cell Number!");
    }

    List<String> updatedNumberOfProductsWithSoldAtUpdated = UpdateProductService.exceute(productSKU, soldAt, buyerName, buyerCellNumber, numberOfInstallments,
        currentInstallmentNumber, currentInstallmentAmount, productStatus);

    return "number of updated product : " + updatedNumberOfProductsWithSoldAtUpdated.size();

  }

}
