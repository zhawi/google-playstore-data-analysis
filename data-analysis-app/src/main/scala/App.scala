import helperClasses.{DfCreator, DfTransformations, SchemaCreation, StringManipulation, WriteToSql}
import sessionProvider.SparkProvider
import com.typesafe.config.ConfigFactory

object App extends SparkProvider with StringManipulation with SchemaCreation {
  def main(args: Array[String]): Unit = {

    /* ---------------------------------------------------------------------------------------------------------
    Config load to get configurations to connect to postgres
       --------------------------------------------------------------------------------------------------------- */


    val config = ConfigFactory.load()
    val rawDbConn = config.getString("database.rawDbUrl")
    val cleanDbConn = config.getString("database.cleanDbUrl")
    val enrichedDbConn = config.getString("database.enrichedDbUrl")
    val dbUser = config.getString("database.user")
    val dbPassword = config.getString("database.password")

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

    //Google playStore apps table with raw data
    WriteToSql.writeToSql(
      googlePSDf,
      rawDbConn,
      getTableNameSourceFile(googlePlayStoreCsv),
      dbUser,
      dbPassword
    )

    //Google playStore apps with user review table with raw data
    WriteToSql.writeToSql(
        googlePSURDf,
        rawDbConn,
        getTableNameSourceFile(googlePlayStoreUserReviewCsv),
        dbUser,
        dbPassword
    )


    /* ---------------------------------------------------------------------------------------------------------
    3. Creation of Dataframes with correct schema and data cleaned to match said schema, addition of app_id field
       --------------------------------------------------------------------------------------------------------- */


    //- Cleaned google playStore data (without duplicates and using App as a unique value), added also app_id
    //Give also correct type to columns, fixed values to be presented as double correctly and long
    val googlePSDfWithAppId = DfCreator.dfWithSpecificColumnsPS(googlePSDf)

    //- Cleaned user review (removed duplicates based on a combination of app name and unique reviews text)
    //Correct type to columns, fixed values with 2 decimal round for sentiments
    val googlePSURDfCleaned = DfCreator.dfWithSpecificColumnsPSUR(googlePSURDf)


    //- Added app_id field to match correct app to the app_id corresponding on playStore dataframe
    val googlePSURDfWithAppId = DfCreator.dfUrWithMatchingAppIdFromDfPs(googlePSURDfCleaned, googlePSDfWithAppId)

    //- Removed none existing values to generate better value when working with the data
    val columnsToCheck = Seq("Sentiment_Polarity", "Sentiment_Subjectivity")
    val googlePSURWithoutNulls = DfTransformations.removeNanValues(googlePSURDfWithAppId, columnsToCheck)


    /* ---------------------------------------------------------------------------------------------------------
    4. store dataframes to SQL database cleanDb as clean data
    with correct type field and name equal to the file + clean, creation of 3 tables:
       - google playStore app data: without duplicates, with an app_id column and correct types with proper formatting
       - google playStore app user review: without duplicates and correct types with proper formatting
       - google playStore app user review with app_id: addition of app_id and removal of null values
       --------------------------------------------------------------------------------------------------------- */


    WriteToSql.writeToSql(
      googlePSDfWithAppId,
      cleanDbConn,
      getTableNameSourceFile(googlePlayStoreCsv) + "clean",
      dbUser,
      dbPassword
    )

    WriteToSql.writeToSql(
      googlePSURDfCleaned,
      cleanDbConn,
      getTableNameSourceFile(googlePlayStoreUserReviewCsv) + "clean",
      dbUser,
      dbPassword
    )

    WriteToSql.writeToSql(
      googlePSURWithoutNulls,
      cleanDbConn,
      getTableNameSourceFile(googlePlayStoreUserReviewCsv) + "cleanWithId",
      dbUser,
      dbPassword
    )


    /* ---------------------------------------------------------------------------------------------------------
    5. Creation of new tables to be consumed to create dashboards, this will be stored in the enriched db
    creation of 5 tables:
        - appPerformance: Table with average rating for each app, sum of installs and sum of reviews;
        - userSentiment: Sentiment distribution per app, average polarity and subjectivity
        - categoryAnalysis: Total apps per Category, average rating, size and price per Category
        - marketTrends: Trends overtime (this is done but we have no incremental of data as of today)
        - newTrendingApps: Identify apps with significant recent growth in reviews and Installs
        (this is done but we have no incremental of data as of today)
        --------------------------------------------------------------------------------------------------------- */


     /* ---------------------------------------------------------------------------------------------------------
        i. Creation of app performance dataframe and store it on postgres sql server
        --------------------------------------------------------------------------------------------------------- */


    val appPerformanceDf = DfTransformations.appPerformanceTransformations(googlePSDfWithAppId)

    WriteToSql.writeToSql(
      appPerformanceDf,
      enrichedDbConn,
      "appperformance",
      dbUser,
      dbPassword
    )


     /* ---------------------------------------------------------------------------------------------------------
        ii. Creation of userSentiment and userSentimentDistribution and store it on postgres sql server
        --------------------------------------------------------------------------------------------------------- */


    val userSentimentDf = DfTransformations.userSentimentTransformation(googlePSURWithoutNulls)

    WriteToSql.writeToSql(
      userSentimentDf,
      enrichedDbConn,
      "usersentiment",
      dbUser,
      dbPassword
    )

    val userSentimentDistributionDf = DfTransformations.userSentimentDistributionTransformation(googlePSURWithoutNulls)

    WriteToSql.writeToSql(
      userSentimentDistributionDf,
      enrichedDbConn,
      "appSentimentDistribution",
      dbUser,
      dbPassword
    )


    /* ---------------------------------------------------------------------------------------------------------
        iii. Creation of categoryAnalysis and store it on postgres sql server
        --------------------------------------------------------------------------------------------------------- */


    val categoryAnalysisDf = DfTransformations.categoryAnalysisTransformation(googlePSDfWithAppId)

    WriteToSql.writeToSql(
      categoryAnalysisDf,
      enrichedDbConn,
      "categoryanalysis",
      dbUser,
      dbPassword
    )

    /* ---------------------------------------------------------------------------------------------------------
        iv. Creation of marketTrends and store it on postgres sql server
        --------------------------------------------------------------------------------------------------------- */


    val marketTrendsDf = DfTransformations.marketTrendsTransformation(googlePSDfWithAppId)

    WriteToSql.writeToSql(
      marketTrendsDf,
      enrichedDbConn,
      "markettrends",
      dbUser,
      dbPassword
    )

    /* ---------------------------------------------------------------------------------------------------------
       v. Creation of newTrendingApps and store it on postgres sql server
       --------------------------------------------------------------------------------------------------------- */

    /* this is gonna be commented out for future reference if I decide to make this a proper ETL solution

    val newTrendingAppsDf = DfTransformations.newTrendingAppsTransformation(googlePSDfWithAppId)

    WriteToSql.writeToSql(
      newTrendingAppsDf,
      enrichedDbConn,
      "newtrendingapps",
      dbUser,
      dbPassword
    )
     */
  }
}
