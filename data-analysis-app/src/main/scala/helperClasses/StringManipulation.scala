package helperClasses

import org.apache.spark.sql.functions._
import org.apache.spark.sql.Column

//Trait with different functions to help manage strings
trait StringManipulation {

  def getTableNameSourceFile(fileNamePath: String): String = {
    val listString = fileNamePath.split("/")
    listString.last.substring(0, listString.last.lastIndexOf("."))
  }

  def sizeToBytes(size: Column): Column = {
    when(size.endsWith("M"), regexp_replace(size, "M", "").cast("long") * 1000000)
      .when(size.endsWith("k"), regexp_replace(size, "k", "").cast("long") * 1000)
      .when(size === "Varies with device", lit(null).cast("long"))
      .otherwise(size.cast("long"))
  }

  def installsLong(installs: Column): Column = {
      regexp_replace(
        regexp_replace(
          col("installs"), ",", ""), "\\+$", "")
        .cast("long")
  }

}
