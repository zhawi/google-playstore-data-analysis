package helperClasses

import org.apache.spark.sql.DataFrame
import sessionProvider.SparkProvider

//Object created to write to sql using spark dataframe
object WriteToSql extends SparkProvider {

  def writeToSql(df: DataFrame, postgresString: String, tableName: String, user: String, password: String): Unit = {
    df.write
      .format("jdbc")
      .option("url", postgresString)
      .option("dbtable", tableName)
      .option("user", user)
      .option("password", password)
      .mode("overwrite") // or "append", if proper ETL is created
      .save()
  }
}
