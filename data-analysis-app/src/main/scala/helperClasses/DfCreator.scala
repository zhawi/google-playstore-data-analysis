package helperClasses

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.StructType
import sessionProvider.SparkProvider

//Object to create different Dataframes based on different options
object DfCreator extends SparkProvider with StringManipulation {

  def dfWithSchemaFromCsv(path: String): DataFrame = {
    spark.read
      .options(Map( "header" -> "true", "inferSchema" -> "true" ))
      .csv(path)
  }

  def dfWithCustomSchema(path: String, schema: StructType): DataFrame = {
    spark.read
      .option("header", "true")
      .schema(schema)
      .csv(path)
  }

  def dfWithSpecificColumnsPS(df: DataFrame): DataFrame = {
      val dfWithoutDuplicates = df.dropDuplicates("App")
      .withColumn("App_id", monotonically_increasing_id() + 1)
      .withColumn("App", trim(lower(col("App"))))
      .withColumn("Category", col("Category"))
      .withColumn("Rating", col("Rating"))
      .withColumn("Reviews", col("Reviews").cast("long"))
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

    val allColumns = Seq("App_id") ++ dfWithoutDuplicates.columns.filter(_ != "App_id")

    val reorderedDf = dfWithoutDuplicates.select(allColumns.map(col): _*)
    reorderedDf
  }

  def dfWithSpecificColumnsPSUR(df: DataFrame): DataFrame = {
    df.dropDuplicates("App", "Translated_Review", "Sentiment", "Sentiment_Polarity", "Sentiment_Subjectivity")
      .withColumn("App", trim(lower(col("App"))))
      .withColumn("Translated_Review", trim(col("Translated_review")))
      .withColumn("Sentiment", col("Sentiment"))
      .withColumn("Sentiment_Polarity",
        round(col("Sentiment_Polarity")
          .cast("double"), 2))
      .withColumn("Sentiment_Subjectivity",
        round(col("Sentiment_Subjectivity")
          .cast("double"), 2))
  }

  def dfUrWithMatchingAppIdFromDfPs(dfUr: DataFrame, dfPs: DataFrame): DataFrame = {

    dfUr.cache()
    dfPs.cache()
    // Joining the DataFrames on the "App" column
    val dfJoined = dfUr.join(dfPs, dfUr("App") === dfPs("App"))

    // Dropping the duplicate "App" column from dfPs
    val dfDroppedDuplicate = dfJoined.drop(dfPs("App"))

    // Selecting the "App_id" from dfPs and required columns from dfUr
    dfDroppedDuplicate.select(
      dfPs("App_id"),
      dfUr("App"),
      dfUr("Translated_Review"),
      dfUr("Sentiment"),
      dfUr("Sentiment_Polarity"),
      dfUr("Sentiment_Subjectivity")
    )
  }
}
