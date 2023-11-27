import helperClasses.{DfCreator, GetDfSchemaAsCreateTable, SchemaCreation, StringManipulation, WriteToSql}
import org.apache.spark.sql.functions._
import sessionProvider.SparkProvider

import java.sql.DriverManager
import com.typesafe.config.ConfigFactory

object App extends SparkProvider with StringManipulation with SchemaCreation {
  def main(args: Array[String]): Unit = {

    //Config load to get configurations to connect to postgres
    val config = ConfigFactory.load()
    val rawDbConn = config.getString("database.rawDbUrl")
    val dbuser = config.getString("database.user")
    val dbpassword = config.getString("database.password")

    //Files location to be used on the program
    val googlePlayStoreCsv = "~/../../Data/googlePlaystore.csv"
    val googlePlayStoreUserReviewCsv = "~/../../Data/userReviews.csv"

    //1. Creation of Dataframe from raw data and store it to SQL as raw data with text fields
    //Creation of Dataframes using structured pre-defined schema
    val googlePSDf = DfCreator.dfWithSchemaFromCsv(googlePlayStoreCsv)
    val googlePSURDf = DfCreator.dfWithSchemaFromCsv(googlePlayStoreUserReviewCsv)

    WriteToSql.writeToSql(
      googlePSDf,
      rawDbConn,
      getTableNameSourceFile(googlePlayStoreCsv),
      dbuser,
      dbpassword
    )

    WriteToSql.writeToSql(
        googlePSURDf,
        rawDbConn,
        getTableNameSourceFile(googlePlayStoreUserReviewCsv),
        dbuser,
        dbpassword
    )

    googlePSDf
      .withColumn("App_id", monotonically_increasing_id() + 1)
      .withColumn("App", trim(col("App")))
      .withColumn("Category", col("Category"))
      .withColumn("Rating", col("Rating"))
      .withColumn("Reviews", col("Reviews"))
      .withColumn("Size", sizeToBytes(col("Size")))
      .withColumn("Installs", installsLong(col("Installs")))
      .withColumn("Type", col("Type"))
      .withColumn("Price", col("Price"))
      .withColumn("Content_rating", col("Content Rating"))
      .withColumn("Genres", col("Genres"))
      .withColumn("Last_updated", to_date(col("Last Updated"), "MMMM d, yyyy"))
      .withColumn("Current_ver", col("Current Ver"))
      .withColumn("Android_ver", col("Android Ver"))
      .drop("Content Rating", "Last Updated", "Current Ver", "Android Ver")
      .show(10)

    googlePSURDf.show(10)
    //2. Creation of Dataframe with correct schema and data cleaned to match said schema, addition of app_id field

    /*
    val schemaPSDfToSql = GetDfSchemaAsCreateTable
      .getSchemaAsCreateTableQuery(googlePSDf, getName(googlePlayStoreCsv))

    val schemaPSURDfToSql = GetDfSchemaAsCreateTable
      .getSchemaAsCreateTableQuery(googlePSURDf, getName(googlePlayStoreUserReviewCsv))


    val userRatingCsvSchema = userRatingSchema()
    val googlePlaystoreCsvSchema = googlePlaystoreSchema()

    val connection = DriverManager.getConnection(rawDbConn, dbuser, dbpassword)
    try {
      val statement = connection.createStatement()
      statement.execute(schemaPSDfToSql)
    } finally {
      connection.close()
    }*/


  }
}
