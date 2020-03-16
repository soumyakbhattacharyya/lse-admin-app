package com.lse.admin.command;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.jline.reader.LineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;

import com.lse.admin.aws.FindStockService;
import com.lse.admin.model.Product;

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
    result.forEach(new Consumer<Product>() {

      @Override
      public void accept(Product t) {
        System.out.println(t.getCode() + "--" + t.getType() + "--" + t.getDescription() + "--" + t.getBuyUnitPrice() + "--" + t.getSoldAt());
      }
    });

    return "Command executed successfully";

  }

  @ShellMethod(value = "Finds unsold stock.", key = "find-unsold-stock")
  public String findUn() {

    // 1. read product sku--------------------------------------------
    String status = "NEW";
    List<Product> result = findStockService.exceute(status);
    Collections.sort(result);
    result.forEach(new Consumer<Product>() {

      @Override
      public void accept(Product t) {
        System.out.println(t.getCode() + "--" + t.getType() + "--" + t.getDescription() + "--" + t.getBuyUnitPrice() + "--" + t.getSoldAt());
      }
    });

    return "Command executed successfully";

  }

}
