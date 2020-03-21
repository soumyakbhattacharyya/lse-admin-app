package com.lse.admin.command;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import org.jline.reader.LineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

import com.lse.admin.aws.FindStockService;
import com.lse.admin.model.Product;

import lombok.val;

@ShellComponent
public class FindStock {

  @Autowired
  @Lazy
  private LineReader lineReader;
  private FindStockService findStockService;

  public static final Logger log = LoggerFactory.getLogger(FindStock.class);

  @ShellMethod(value = "Finds sold stock.", key = "find-sold-stock")
  public String findSoldStock() {

    // 1. read product sku--------------------------------------------
    String status = "SOLD";
    List<Product> result = findStockService.exceute(status);
    Collections.sort(result);
    Double totalBoughtCost = sumOfBoughtCost(result);
    Double totalSoldCost = sumOfSoldCost(result);
    System.out.println("Total number of sold stock : " + (null != result ? result.size() : 0));
    System.out.println("Total bought at value in INR : " + (totalBoughtCost));
    System.out.println("Total sold at value in INR : " + (totalSoldCost));

    formatAsTable(result);
    return "Command executed successfully";

  }

  private Double sumOfSoldCost(List<Product> result) {
    Double totalBoughtCost = result.stream().collect(Collectors.summingDouble(new ToDoubleFunction<Product>() {

      @Override
      public double applyAsDouble(Product value) {
        return value.getSoldAt();
      }
    }));
    return totalBoughtCost;
  }

  private Double sumOfBoughtCost(List<Product> result) {
    Double totalBoughtCost = result.stream().collect(Collectors.summingDouble(new ToDoubleFunction<Product>() {

      @Override
      public double applyAsDouble(Product value) {
        return value.getBuyUnitPrice();
      }
    }));
    return totalBoughtCost;
  }

  @ShellMethod(value = "Finds unsold stock.", key = "find-unsold-stock")
  public String findUn() {

    // 1. read product sku--------------------------------------------
    String status = "NEW";
    List<Product> result = findStockService.exceute(status);
    Collections.sort(result);
    Double totalBoughtCost = sumOfBoughtCost(result);
    System.out.println("Total number of unsold stock : " + (null != result ? result.size() : 0));
    System.out.println("Total bought at value in INR : " + (totalBoughtCost));

    formatAsTable(result);

    return "Command executed successfully";

  }

  private void formatAsTable(List<Product> result) {
    LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
    headers.put("code", "Code");
    headers.put("type", "Type");
    headers.put("description", "Description");
    headers.put("buyUnitPrice", "Bought-At");
    headers.put("suggestedUnitPrice", "Suggested-Price");
    headers.put("soldAt", "Sold-At");
    headers.put("profit", "Profit");
    headers.put("buyerName", "Buyer-Name");
    headers.put("numberOfInstallments", "No.-of-Installments");
    headers.put("currentInstallmentNumber", "Current-Installments");
    headers.put("currentInstallmentAmount", "Recovered-Amount");
    TableModel model = new BeanListTableModel<>(result, headers);

    TableBuilder tableBuilder = new TableBuilder(model);
    tableBuilder.addInnerBorder(BorderStyle.oldschool);
    tableBuilder.addHeaderBorder(BorderStyle.oldschool);
    System.out.println(tableBuilder.build().render(80));
  }

}
