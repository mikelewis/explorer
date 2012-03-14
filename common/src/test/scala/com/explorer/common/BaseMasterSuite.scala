package com.explorer.common
import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.OneInstancePerTest
import scala.io.Source._

class BaseMasterSuite extends FunSpec with ShouldMatchers {
  val SampleFilesPath = "../common/src/main/resources/sample_files"

  def filePathFromSampleFiles(file: String) = {
    SampleFilesPath + "/" + file
  }

  def contentsOfSampleFile(file: String) = fromFile(filePathFromSampleFiles(file)).getLines.mkString

}