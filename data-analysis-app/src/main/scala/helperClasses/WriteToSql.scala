package helperClasses

import org.apache.spark.sql.DataFrame
import sessionProvider.SparkProvider

object WriteToSql extends SparkProvider {

  def writeToSql(df: DataFrame, postgresString: String, tableName: String): Unit = {
    df.write
      .format("jdbc")
      .option("url", postgresString)
      .option("dbtable", tableName)
      .option("user", "playstoreuser")
      .option("password", "googlePlay123")
      .mode("overwrite") // or "append", depending on your needs
      .save()
  }
}
