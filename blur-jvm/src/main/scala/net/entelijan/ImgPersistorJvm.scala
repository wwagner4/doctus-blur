package net.entelijan

import java.io.File

/**
  * Persists and reads from the filesystem
  */
object ImgPersistorJvm extends ImgPersitor {

  import upickle.default._

  val dir = createOrOpenDir(".blur")


  override def save(data: ImgData): Int = {



    def nextId: Int = {
      val ids: Seq[Int] = IdExtractor.ids(dir.list())
      if (ids.isEmpty) 0
      else ids.max + 1
    }

    def writeToFile(id: Int, dataStr: String): Unit = {

      def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
        val p = new java.io.PrintWriter(f)
        try {
          op(p)
        } finally {
          p.close()
        }
      }

      val out = new File(dir, fileName(id))
      printToFile(out) { pw =>
        pw.print(dataStr)
      }

    }

    val id: Int = nextId
    val dataStr: String = write(data)

    writeToFile(id, dataStr)
    id
  }

  override def load(id: Int): Option[ImgData] = {
    val in = new File(dir, fileName(id))
    if (!in.canRead) {
      None
    } else {
      val dataTxt = scala.io.Source.fromFile(in).mkString
      val imgData = read[ImgData](dataTxt)
      Some(imgData)
    }
  }

  private def fileName(id: Int): String = "img%04d.txt" format id

  private def createOrOpenDir(path: String) = {
    val homeDir = new File(System.getProperty("user.home"))
    require(homeDir.canWrite, "Cannot write to home dir '%s'" format homeDir)
    val dir = new File(homeDir, path)
    if (!dir.exists()) dir.mkdirs()
    dir
  }


}

object IdExtractor {

  val Pattern = """img(\d*).txt""".r

  def ids(names: Iterable[String]): Seq[Int] = {

    names.flatMap { fnam =>
      try {
        fnam match {
          case Pattern(numStr) => Some(numStr.toInt)
          case _ => None
        }
      } catch {
        case _: Exception => None
      }
    }.toSeq
  }

}
