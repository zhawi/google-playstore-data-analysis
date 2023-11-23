package helperClasses

import org.apache.spark.sql.functions._
import org.apache.spark.sql.Column
trait StringManipulation {

  def getTableNameSourceFile(fileNamePath: String): String = {
    val listString = fileNamePath.split("/")
    listString.last.substring(0, listString.last.lastIndexOf("."))
  }

  def sizeToBytes(size: Column): Column = {
    when(size.endsWith("M"), regexp_replace(size, "M", "").cast("double") * 1000000)
      .when(size.endsWith("k"), regexp_replace(size, "k", "").cast("double") * 1000)
      .when(size === "Varies with device", lit(null).cast("double"))
      .otherwise(size.cast("double"))
  }

  def installsInteger(installs: Column): Column = {
      regexp_replace(
        regexp_replace(
          col("installs"), ",", ""), "\\+$", "")
        .cast("long")
  }

}
