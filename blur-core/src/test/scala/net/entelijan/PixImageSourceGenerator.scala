package net.entelijan

case class PixImageConfig(urlStrings: Seq[String])

object PixImageConfigFactory {

  def a1: PixImageConfig = {
    val urlStrings = List("a1/g10a.png", "a1/g11a.png", "a1/g2a.png", "a1/g4a.png", "a1/g7a.png", "a1/g9a.png")
    PixImageConfig(urlStrings)
  }

}

object PixImageSourceGenerator extends App {

  val urlStrings = List("a1/g10a.png", "a1/g11a.png", "a1/g2a.png", "a1/g4a.png", "a1/g7a.png", "a1/g9a.png")

  case class SrcVal(name: String, pixels: Seq[Double])

  case class SrcInner(name: String, vals: Seq[SrcVal])

  case class SrcImage(name: String, width: Int, height: Int, inners: Seq[SrcInner])

  def createPixImages(urlStrings: Seq[String]): Seq[PixImage] = urlStrings map { urlStr =>
    val url = getClass.getClassLoader.getResource(urlStr)
    require(url != null, "resource '%s' does not exist" format urlStr)
    val in = url.openStream()
    val pi = PixImageGenerator.createPixImage(in)
    in.close()
    pi
  }

  val pis = createPixImages(urlStrings)

  val imgModels = createImgModel(pis)

  val imagesStr = formatImages(imgModels)

  val out = s"""package net.entelijan

object PixImageHolder {

$imagesStr

}"""

  println(out)

  def formatImages(imgs: Seq[SrcImage]): String = {
    imgs.map { img =>
      val nam = img.name
      val w = img.width
      val h = img.height
      val inner = formatInners(img.inners)
      val combi = formatCombi(img.inners)
      s"""
$inner
   val $nam = PixImage($w, $h, List($combi).flatten)
"""

    }.mkString("")
  }

  def formatCombi(inners: Seq[SrcInner]): String = {
    inners.map { inner =>
      val innerNam = inner.name
      inner.vals.map { v => s"$innerNam.${v.name}" }.mkString(", ")
    }.mkString(",\n       ")
  }

  def formatInners(inners: Seq[SrcInner]): String = {
    inners.map { inner =>
      val nam = inner.name
      val vals = formatVals(inner.vals)
      s"""
  private object $nam {
$vals    
  }   
      """
    }.mkString("")
  }
  
  def formatVals(vals: Seq[SrcVal]): String = {
    vals.map { _val =>
      val nam = _val.name
      val pxs = _val.pixels
      s"""    val $nam = $pxs"""
    }.mkString("\n")
  }

  def createImgModel(pixImages: Seq[PixImage]): Seq[SrcImage] = {
    pixImages.zipWithIndex.map {
      case (pi, index) =>
        val name = s"img%04d" format index
        val inners = createInnerModel(index, pi.pixels)
        SrcImage(name, pi.width, pi.height, inners)
    }
  }

  def createInnerModel(indexImg: Int, pixels: Seq[Double]): Seq[SrcInner] = {
    pixels.grouped(2000).zipWithIndex.toList.map {
      case (grp, index) =>
        val name = s"Inner%04d_%04d" format (indexImg, index)
        val vals = createVals(grp)
        SrcInner(name, vals)
    }
  }

  def createVals(pixels: Seq[Double]): Seq[SrcVal] = {
    pixels.grouped(50).zipWithIndex.toList.map {
      case (grp, index) =>
        val name = s"img%04d" format index
        SrcVal(name, grp)
    }
  }

}