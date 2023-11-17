package sessionProvider
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

trait SparkProvider {

  val spark: SparkSession = initializeSpark("klcdi-sql-loader")
  lazy val sc: SparkContext = spark.sparkContext

  def initializeSpark(appName: String,
                      logLevel: String = "WARN"): SparkSession = {
    val defaultConf = new SparkConf()
      .setAppName(appName)
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.sql.parquet.compression.codec", "snappy")
      .set("spark.sql.parquet.mergeSchema", "false")
      .set("spark.sql.sources.partitionOverwriteMode", "dynamic")
      .set("spark.log.level", logLevel)

    val conf = {
      if (defaultConf.contains("spark.master")) defaultConf
      else defaultConf.setMaster("local[*]")
    }

    SparkSession.builder.config(conf).getOrCreate
  }

}
