package helperClasses

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

object DfTransformations {
  def removeNanValues(df: DataFrame, columnsToCheck: Seq[String]): DataFrame = {
    df.na.drop("any", columnsToCheck)
  }

  def appPerformanceTransformations(df: DataFrame): DataFrame = {
    df
      .groupBy("App_id", "App", "Category", "Price")
      .agg(
        avg("Rating").as("Average_Rating"),
        sum("Installs").as("Total_Installs"),
        sum("Reviews").as("Total_Reviews")
      )
  }

  def userSentimentTransformation(df: DataFrame): DataFrame = {
    df
      .groupBy("App_id")
      .agg(
        count("Sentiment").as("Sentiment_Count"),
        round(avg("Sentiment_Polarity"), 2).as("Average_Polarity"),
        round(avg("Sentiment_Subjectivity"), 2).as("Average_Subjectivity")
      )
  }

  def categoryAnalysisTransformation(df: DataFrame): DataFrame = {
    df
      .withColumn("Price_Numeric",
        regexp_replace(col("Price"), "[^\\d.]", "")
          .cast("double"))
      .groupBy("Category")
      .agg(
        count("App").as("Total_Apps"),
        round(avg("Rating"), 2).as("Average_Rating"),
        round(avg("Size"), 2).as("Average_Size"),
        round(avg("Price_Numeric"), 2).as("Average_Price")
      )
  }

  def marketTrendsTransformation(df: DataFrame): DataFrame = {
    df
      .groupBy("Last_updated")
      .agg(
        round(avg("Rating"), 2).as("Average_Rating"),
        sum("Installs").as("Total_Installs"),
        sum("Reviews").as("Total_Reviews")
      )
  }


  /*
  def newTrendingAppsTransformation(df: DataFrame): DataFrame = {
    val dfWithMonthYear = df
      .withColumn("MonthYear", date_format(col("Last_updated"), "yyyy-MM"))

    // Window definition with month partition
    val windowSpec = Window.partitionBy("App").orderBy("MonthYear").rowsBetween(-1, 0)

    // Calculation of monthly sum of Installs and Reviews, then growth rate calculation
    val newTrendingApps = dfWithMonthYear
      .withColumn("Monthly_Installs", sum("Installs").over(windowSpec))
      .withColumn("Monthly_Reviews", sum("Reviews").over(windowSpec))
      .withColumn("Install_Growth",
        (
          col("Monthly_Installs") - lag("Monthly_Installs", 1)
          .over(windowSpec)) / lag("Monthly_Installs", 1)
          .over(windowSpec)
      )
      .withColumn("Review_Growth",
        (
          col("Monthly_Reviews") - lag("Monthly_Reviews", 1)
            .over(windowSpec)) / lag("Monthly_Reviews", 1)
          .over(windowSpec)
      )
      .select("App", "Install_Growth", "Review_Growth", "MonthYear")
      .filter(col("MonthYear") === "2023-11")
      .orderBy(desc("Install_Growth"), desc("Review_Growth"))

    newTrendingApps
  }
  */

}
