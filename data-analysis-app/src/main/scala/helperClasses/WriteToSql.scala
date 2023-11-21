package helperClasses

import org.apache.spark.sql.DataFrame
import sessionProvider.SparkProvider

object WriteToSql extends SparkProvider {

  def writeToSql(df: DataFrame, postgresString: String, tableName: String, user: String, password: String): Unit = {
    df.write
      .format("jdbc")
      .option("url", postgresString)
      .option("dbtable", tableName)
      .option("user", user)
      .option("password", password)
      .mode("overwrite") // or "append", depending on your needs
      .save()
  }
}
