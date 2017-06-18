package loki256.github.com.userairport.utils

import java.io.{BufferedWriter, File, FileWriter}

object IOUtils {

  def writeStringsToFile(iterator: Iterator[String], fileName: String) = {
    val file = new File(fileName)
    val bw = new BufferedWriter(new FileWriter(file))
    iterator.foreach { res =>
      bw.write(res)
    }
    bw.close()
  }

}
