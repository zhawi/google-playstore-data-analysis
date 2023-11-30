package helperClasses

import org.apache.spark.sql.types._

//Trait created to create schema manually to use as a base schema when creating Dataframes
trait SchemaCreation {

  def userRatingSchema(): StructType = {
    StructType(Array(
      StructField("App", StringType, true),
      StructField("Translated_Review", StringType, true),
      StructField("Sentiment", StringType, true),
      StructField("Sentiment_polarity", DoubleType, true),
      StructField("Sentiment_subjectivity", DoubleType, true)
    ))
  }

  def googlePlaystoreSchema(): StructType = {
    StructType(Array(
      StructField("App", StringType, true),
      StructField("Category", StringType, true),
      StructField("Rating", DoubleType, true),
      StructField("Reviews", IntegerType, true),
      StructField("Size", StringType, true),
      StructField("Installs", StringType, true),
      StructField("Type", StringType, true),
      StructField("Price", StringType, true),
      StructField("Content Rating", StringType, true),
      StructField("Genres", StringType, true),
      StructField("Last Updated", DateType, true),
      StructField("Current Ver", DoubleType, true),
      StructField("Android Ver", StringType, true)
    ))
  }

}
