package com.lse.admin.model;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Product {

  private final String id;
  private final String code;
  private final String shortDescription;
  private final String longDescription;
  private double price;
  @JsonSerialize(using = IonLocalDateSerializer.class)
  @JsonDeserialize(using = IonLocalDateDeserializer.class)
  private final LocalDate createdOn;
  private final String createdBy;
  @JsonSerialize(using = IonLocalDateSerializer.class)
  @JsonDeserialize(using = IonLocalDateDeserializer.class)
  private LocalDate updatedOn;
  private String updatedBy;
  private ProductStatus productStatus;

  public Product(String id, String code, String shortDescription, String longDescription, double price, LocalDate createdOn, String createdBy, ProductStatus productStatus) {
    super();
    this.id = id;
    this.code = code;
    this.shortDescription = shortDescription;
    this.longDescription = longDescription;
    this.price = price;
    this.createdOn = createdOn;
    this.createdBy = createdBy;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public LocalDate getUpdatedOn() {
    return updatedOn;
  }

  public void setUpdatedOn(LocalDate updatedOn) {
    this.updatedOn = updatedOn;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public String getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public String getLongDescription() {
    return longDescription;
  }

  public LocalDate getCreatedOn() {
    return createdOn;
  }

  public String getCreatedBy() {
    return createdBy;
  }

}
