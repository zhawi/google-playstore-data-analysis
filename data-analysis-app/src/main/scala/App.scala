import helperClasses.{DfCreator, DfTransformations, GetDfSchemaAsCreateTable, SchemaCreation, StringManipulation, WriteToSql}
import org.apache.spark.sql.functions._
import sessionProvider.SparkProvider

import java.sql.DriverManager
import com.typesafe.config.ConfigFactory

object App extends SparkProvider with StringManipulation with SchemaCreation {
  def main(args: Array[String]): Unit = {

    /* ---------------------------------------------------------------------------------------------------------
    Config load to get configurations to connect to postgres
       --------------------------------------------------------------------------------------------------------- */

    val config = ConfigFactory.load()
    val rawDbConn = config.getString("database.rawDbUrl")
    val cleanDbConn = config.getString("database.cleanDbUrl")
    val dbuser = config.getString("database.user")
    val dbpassword = config.getString("database.password")

    //Files location to be used on the program
    val googlePlayStoreCsv = "~/../../Data/googlePlaystore.csv"
    val googlePlayStoreUserReviewCsv = "~/../../Data/userReviews.csv"

    /* ---------------------------------------------------------------------------------------------------------
    1. Creation of Dataframe from raw data
       --------------------------------------------------------------------------------------------------------- */

    //Creation of Dataframes using structured pre-defined schema
    val googlePSDf = DfCreator.dfWithSchemaFromCsv(googlePlayStoreCsv)
    val googlePSURDf = DfCreator.dfWithSchemaFromCsv(googlePlayStoreUserReviewCsv)

    /* ---------------------------------------------------------------------------------------------------------
    2. store dataframes to SQL database rawDb as raw data with text fields and name equal to the file
       --------------------------------------------------------------------------------------------------------- */

    //Googleplaystore app table with raw data
    WriteToSql.writeToSql(
      googlePSDf,
      rawDbConn,
      getTableNameSourceFile(googlePlayStoreCsv),
      dbuser,
      dbpassword
    )

    //Googleplaystore apps with user review table with raw data
    WriteToSql.writeToSql(
        googlePSURDf,
        rawDbConn,
        getTableNameSourceFile(googlePlayStoreUserReviewCsv),
        dbuser,
        dbpassword
    )
    /* ---------------------------------------------------------------------------------------------------------
    3. Creation of Dataframes with correct schema and data cleaned to match said schema, addition of app_id field
       --------------------------------------------------------------------------------------------------------- */

    //- Cleaned google playstore data (without duplicates and using App as a unique value), added also app_id
    //Give also correct type to columns, fixed values to be presented as double correctly and long
    val googlePSDfWithAppId = DfCreator.dfWithSpecificColumnsPS(googlePSDf)

    //- Cleaned user review (removed duplicates based on a combination of app name and unique reviews text)
    //Correct type to columns, fixed values with 2 decimal round for sentiments
    val googlePSURDfCleaned = DfCreator.dfWithSpecificColumnsPSUR(googlePSURDf)


    //- Added app_id field to match correct app to the app_id corresponding on playstore dataframe
    val googlePSURDfWithAppId = DfCreator.dfUrWithMatchingAppIdFromDfPs(googlePSURDfCleaned, googlePSDfWithAppId)

    //- Removed none existing values to generate better value when working with the data
    val columnsToCheck = Seq("Sentiment_Polarity", "Sentiment_Subjectivity")
    val googlePSURWithoutNulls = DfTransformations.removeNanValues(googlePSURDfWithAppId, columnsToCheck)

    /* ---------------------------------------------------------------------------------------------------------
    4. store dataframes to SQL database cleanDb as clean data
    with corect type field and name equal to the file + clean, creation of 3 tables:
    - google playstore app data: without duplicates, with an app_id column and correct types with proper formatting
    - google playstore app user review: without duplicates and correct types with proper formatting
    - google playstore app user review with app_id: adittion of app_id and removal of null values
       --------------------------------------------------------------------------------------------------------- */


    WriteToSql.writeToSql(
      googlePSDfWithAppId,
      cleanDbConn,
      getTableNameSourceFile(googlePlayStoreCsv) + "clean",
      dbuser,
      dbpassword
    )

    WriteToSql.writeToSql(
      googlePSURDfCleaned,
      cleanDbConn,
      getTableNameSourceFile(googlePlayStoreUserReviewCsv) + "clean",
      dbuser,
      dbpassword
    )

    WriteToSql.writeToSql(
      googlePSURWithoutNulls,
      cleanDbConn,
      getTableNameSourceFile(googlePlayStoreUserReviewCsv) + "cleanWithId",
      dbuser,
      dbpassword
    )

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
