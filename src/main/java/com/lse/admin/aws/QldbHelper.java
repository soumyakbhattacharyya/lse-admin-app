package com.lse.admin.aws;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.ion.Decimal;
import com.amazon.ion.IonString;
import com.amazon.ion.IonStruct;
import com.amazon.ion.IonValue;
import com.lse.admin.aws.qldb.DmlResultDocument;
import com.lse.admin.aws.qldb.QldbRevision;

import software.amazon.qldb.QldbSession;
import software.amazon.qldb.Result;
import software.amazon.qldb.TransactionExecutor;

public class QldbHelper {

  public static final Logger log = LoggerFactory.getLogger(FindStockService.class);

  public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Converts a date string with the format 'yyyy-MM-dd' into a {@link java.util.Date} object.
   *
   * @param date
   *          The date string to convert.
   * @return {@link java.time.LocalDate} or null if there is a {@link ParseException}
   */
  public static synchronized LocalDate convertToLocalDate(String date) {
    return LocalDate.parse(date, DATE_TIME_FORMAT);
  }

  /**
   * Convert the result set into a list of IonValues.
   *
   * @param result
   *          The result set to convert.
   * @return a list of IonValues.
   */
  public static List<IonValue> toIonValues(Result result) {
    final List<IonValue> valueList = new ArrayList<>();
    result.iterator().forEachRemaining(valueList::add);
    return valueList;
  }

  /**
   * Get the document ID of a particular document.
   *
   * @param txn
   *          A transaction executor object.
   * @param tableName
   *          Name of the table containing the document.
   * @param identifier
   *          The identifier used to narrow down the search.
   * @param value
   *          Value of the identifier.
   * @return the list of document IDs in the result set.
   */
  public static String getDocumentId(final TransactionExecutor txn, final String tableName, final String identifier, final String value) {
    try {
      final List<IonValue> parameters = Collections.singletonList(Constants.MAPPER.writeValueAsIonValue(value));
      final String query = String.format("SELECT metadata.id FROM _ql_committed_%s AS p WHERE p.data.%s = ?", tableName, identifier);
      Result result = txn.execute(query, parameters);
      if (result.isEmpty()) {
        throw new IllegalStateException("Unable to retrieve document ID using " + value);
      }
      return getStringValueOfStructField((IonStruct) result.iterator().next(), "id");
    } catch (IOException ioe) {
      throw new IllegalStateException(ioe);
    }
  }

  /**
   * Get the document by ID.
   *
   * @param qldbSession
   *          A QLDB session.
   * @param tableName
   *          Name of the table to insert documents into.
   * @param documentId
   *          The unique ID of a document in the Person table.
   * @return a {@link QldbRevision} object.
   * @throws IllegalStateException
   *           if failed to convert parameter into {@link IonValue}.
   */
  public static QldbRevision getDocumentById(QldbSession qldbSession, String tableName, String documentId) {
    try {
      final List<IonValue> parameters = Collections.singletonList(Constants.MAPPER.writeValueAsIonValue(documentId));
      final String query = String.format("SELECT c.* FROM _ql_committed_%s AS c BY docId WHERE docId = ?", tableName);
      Result result = qldbSession.execute(query, parameters);
      if (result.isEmpty()) {
        throw new IllegalStateException("Unable to retrieve document by id " + documentId + " in table " + tableName);
      }
      return Constants.MAPPER.readValue(result.iterator().next(), QldbRevision.class);
    } catch (IOException ioe) {
      throw new IllegalStateException(ioe);
    }
  }

  /**
   * Return a list of modified document IDs as strings from a DML {@link Result}.
   *
   * @param result
   *          The result set from a DML operation.
   * @return the list of document IDs modified by the operation.
   */
  public static List<String> getDocumentIdsFromDmlResult(final Result result) {
    final List<String> strings = new ArrayList<>();
    result.iterator().forEachRemaining(row -> strings.add(getDocumentIdFromDmlResultDocument(row)));
    return strings;
  }

  public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
    return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  /**
   * Convert the given DML result row's document ID to string.
   *
   * @param dmlResultDocument
   *          The {@link IonValue} representing the results of a DML operation.
   * @return a string of document ID.
   */
  public static String getDocumentIdFromDmlResultDocument(final IonValue dmlResultDocument) {
    try {
      DmlResultDocument result = Constants.MAPPER.readValue(dmlResultDocument, DmlResultDocument.class);
      return result.getDocumentId();
    } catch (IOException ioe) {
      throw new IllegalStateException(ioe);
    }
  }

  /**
   * Get the String value of a given {@link IonStruct} field name.
   * 
   * @param struct
   *          the {@link IonStruct} from which to get the value.
   * @param fieldName
   *          the name of the field from which to get the value.
   * @return the String value of the field within the given {@link IonStruct}.
   */
  public static String getStringValueOfStructField(final IonStruct struct, final String fieldName) {
    return ((IonString) struct.get(fieldName)).stringValue();
  }

  /**
   * Convert the given double to a decimal value.
   *
   * @param num
   *          The double to convert.
   * @return the decimal value of the double.
   */
  private static synchronized Decimal convertToDecimal(final double num) {
    return Decimal.valueOf(num);
  }

  /**
   * Pretty print all elements in the provided {@link Result}.
   *
   * @param result
   *          {@link Result} from executing a query.
   */
  public static void printDocuments(final Result result) {
    result.iterator().forEachRemaining(row -> log.info(row.toPrettyString()));
  }

  /**
   * Pretty print all elements in the provided list of {@link IonStruct}.
   *
   * @param documents
   *          List of documents to print.
   */
  public static void printDocuments(final List<IonStruct> documents) {
    documents.forEach(row -> log.info(row.toPrettyString()));
  }

}
