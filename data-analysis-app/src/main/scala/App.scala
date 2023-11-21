import helperClasses.{DfCreator, GetDfSchemaAsCreateTable, SplitStrings, WriteToSql, SchemaCreation}
import sessionProvider.SparkProvider

import java.sql.DriverManager
import com.typesafe.config.ConfigFactory

object App extends SparkProvider with SplitStrings with SchemaCreation {

  def main(args: Array[String]): Unit = {

    //Config load to get configurations to connect to postgres
    val config = ConfigFactory.load()
    val rawDbConn = config.getString("database.rawDbUrl")
    val dbuser = config.getString("database.user")
    val dbpassword = config.getString("database.password")

    val userRatingCsvSchema = userRatingSchema()
    val googlePlaystoreCsvSchema = googlePlaystoreSchema()


    //Files location to be used on the program
    val googlePlayStoreCsv = "~/../../Data/googlePlaystore.csv"
    val googlePlayStoreUserReviewCsv = "~/../../Data/userReviews.csv"

    //1. Creation of Dataframe from raw data and store it to SQL as raw data
    //Creation of Dataframes using structured pre-defined schema
    val googlePSDf = DfCreator.dfWithSchemaFromCsv(googlePlayStoreCsv)
    val googlePSURDf = DfCreator.dfWithSchemaFromCsv(googlePlayStoreUserReviewCsv)

    WriteToSql.writeToSql(googlePSDf, rawDbConn, getName(googlePlayStoreCsv), dbuser, dbpassword)
    WriteToSql.writeToSql(googlePSURDf, rawDbConn, getName(googlePlayStoreUserReviewCsv), dbuser, dbpassword)

    val schemaPSDfToSql = GetDfSchemaAsCreateTable
      .getSchemaAsCreateTableQuery(googlePSDf, getName(googlePlayStoreCsv))

    val schemaPSURDfToSql = GetDfSchemaAsCreateTable
      .getSchemaAsCreateTableQuery(googlePSURDf, getName(googlePlayStoreUserReviewCsv))


    //println(schemaPSDfToSql)

    /*val connection = DriverManager.getConnection(postgresConn, dbuser, dbpassword)
    try {
      val statement = connection.createStatement()
      statement.execute(schemaPSDfToSql)
    } finally {
      connection.close()
    }*/


  }
}
