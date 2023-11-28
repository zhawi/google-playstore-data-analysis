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

    val googlePSDfWithAppId = DfCreator.dfWithSpecificColumnsPS(googlePSDf)
    val googlePSURDfCleaned = DfCreator.dfWithSpecificColumnsPSUR(googlePSURDf)

    googlePSDfWithAppId.filter(col("App_id") === 12).show()

    val columnsToCheck = Seq("Sentiment_Polarity", "Sentiment_Subjectivity")
    val dftest2 = DfCreator.dfUrWithMatchingAppIdFromDfPs(googlePSURDfCleaned, googlePSDfWithAppId)
    val dfCleaned = dftest2.na.drop("any", columnsToCheck)
    dfCleaned.filter(col("Sentiment") === "nan").show()
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
