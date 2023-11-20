import helperClasses.{DfCreator, GetDfSchemaAsCreateTable, SplitStrings, WriteToSql}
import sessionProvider.SparkProvider
import java.sql.DriverManager

object App extends SparkProvider with SplitStrings{

  def main(args: Array[String]): Unit = {
    val googlePlayStoreCsv = "~/../../Data/googlePlaystore.csv"
    val googlePlayStoreUserReviewCsv = "~/../../Data/userReviews.csv"
    val postgresConn = "jdbc:postgresql://localhost:5432/playstoredb"
    val user = "playstoreuser"
    val password = "googlePlay123"


    val googlePSDf = DfCreator.dfWithSchemaFromCsv(googlePlayStoreCsv)
    val googlePSURDf = DfCreator.dfWithSchemaFromCsv(googlePlayStoreUserReviewCsv)

    googlePSDf.printSchema()
    googlePSURDf.printSchema()

    val schemaPSDfToSql = GetDfSchemaAsCreateTable
      .getSchemaAsCreateTableQuery(googlePSDf, getName(googlePlayStoreCsv))

    val schemaPSURDfToSql = GetDfSchemaAsCreateTable
      .getSchemaAsCreateTableQuery(googlePSURDf, getName(googlePlayStoreUserReviewCsv))

    //WriteToSql.writeToSql(googlePSDf, postgresConn, getName(googlePlayStoreCsv))

    println(schemaPSDfToSql)

    val connection = DriverManager.getConnection(postgresConn, user, password)
    try {
      val statement = connection.createStatement()
      statement.execute(schemaPSDfToSql)
    } finally {
      connection.close()
    }


  }
}
