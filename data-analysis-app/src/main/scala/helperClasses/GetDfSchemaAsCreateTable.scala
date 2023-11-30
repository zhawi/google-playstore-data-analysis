package helperClasses

import sessionProvider.SparkProvider

import org.apache.spark.sql.DataFrame

//Object used to create tables in SQL using create table sql queries
object GetDfSchemaAsCreateTable extends SparkProvider{

  def getSchemaAsCreateTableQuery(df: DataFrame, tableName: String): String = {

    val schema = df.schema

    val columns = schema.fields.map { field =>
      val fieldName = field.name.replace(" ", "_")
      val fieldType = field.dataType.simpleString match {
        case "integer" => "INTEGER"
        case "long" => "BIGINT"
        case "double" => "DOUBLE PRECISION"
        case "float" => "REAL"
        case "short" => "SMALLINT"
        case "string" => "TEXT"
        case "boolean" => "BOOLEAN"
        case "timestamp" => "TIMESTAMP"
        case _ => "TEXT" // Default to TEXT for types that are not mapped
      }
      s"$fieldName $fieldType"
    }

    s"CREATE TABLE IF NOT EXISTS $tableName (${columns.mkString(", ")})"
  }

}
