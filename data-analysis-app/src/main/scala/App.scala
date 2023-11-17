import helperClasses.{DfCreator, GetDfSchemaAsCreateTable, SplitStrings, WriteToSql}
import sessionProvider.SparkProvider

object App extends SparkProvider with SplitStrings{

  def main(args: Array[String]): Unit = {
    val googlePlayStoreCsv = "~/../../Data/googlePlaystore.csv"
    val googlePlayStoreUserReviewCsv = "~/../../Data/userReviews.csv"
    val postgresConn = "jdbc:postgresql://localhost:5432/playstoredb"

    val googlePSDf = DfCreator.dfWithSchemaFromCsv(googlePlayStoreCsv)
    val googlePSURDf = DfCreator.dfWithSchemaFromCsv(googlePlayStoreUserReviewCsv)

    val schemaPSDfToSql = GetDfSchemaAsCreateTable
      .getSchemaAsCreateTableQuery(googlePSDf, getName(googlePlayStoreCsv))

    val schemaPSURDfToSql = GetDfSchemaAsCreateTable
      .getSchemaAsCreateTableQuery(googlePSURDf, getName(googlePlayStoreUserReviewCsv))

    WriteToSql.writeToSql(googlePSDf, postgresConn, "playstoredb", getName(googlePlayStoreCsv))

  }
}
