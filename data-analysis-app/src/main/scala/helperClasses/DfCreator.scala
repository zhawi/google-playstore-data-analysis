package helperClasses

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.StructType
import sessionProvider.SparkProvider

object DfCreator extends SparkProvider{

  def dfWithSchemaFromCsv(path: String): DataFrame = {
    spark.read
      .options(Map( "header" -> "true", "inferSchema" -> "true" ))
      .csv(path)
  }

  def dfWithCustomSchema(path: String, schema: StructType): DataFrame = {
    spark.read
      .option("header", "true")
      .schema(schema)
      .csv(path)

  }
}
