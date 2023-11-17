package helperClasses

trait SplitStrings {

  def getName(fileNamePath: String): String = {
    val listString = fileNamePath.split("/")
    listString.last.substring(0, listString.last.lastIndexOf("."))
  }

}
