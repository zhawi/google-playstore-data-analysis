package helperClasses

import org.apache.spark.sql.DataFrame
import sessionProvider.SparkProvider

object DfCreator extends SparkProvider{

  def dfWithSchemaFromCsv(path: String): DataFrame = {
    spark.read.options(Map("header" -> "true", "inferSchema" -> "true")).csv(path)
  }
}
