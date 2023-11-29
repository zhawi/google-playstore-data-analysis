package helperClasses

import org.apache.spark.sql.DataFrame

object DfTransformations {
  def removeNanValues(df: DataFrame, columnsToCheck: Seq[String]): DataFrame = {
    df.na.drop("any", columnsToCheck)
  }

}
