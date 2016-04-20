package net.entelijan

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import scala.BigDecimal
import java.io.File
import doctus.core.util.DoctusPoint
import java.util.Random
import scala.annotation.tailrec

case class PointImage(name: String, points: List[DoctusPoint], aspectRatio: Double)

trait IPointImageGenerator {

  def createPointImage(file: File, pointCnt: Int): PointImage
  
}

object PointImageGenerator extends {

  case class PixImage(width: Int, height: Int, pixels: Seq[Double])

  val random = new Random()
  def ran(): Double = random.nextDouble()

  def createPointImage(file: File, pointCnt: Int): PointImage = {
    val piximg = createPixImage(file)
    val name = extractName(file.getName)
    val ratio = piximg.width.toDouble / piximg.height
    val points = createPointImg(piximg, pointCnt)
    PointImage(name, points, ratio)
  }

  def extractName(fileName: String): String = {
    val len = fileName.length()
    fileName.substring(0, len - 4)
  }

  def createPixImage(img: File): PixImage = {
    val bi: BufferedImage = ImageIO.read(img)
    val pixels = for (x <- 0 until bi.getWidth; y <- 0 until bi.getHeight) yield {
      val color = bi.getRGB(x, y);
      val red = (color >>> 16) & 0xFF
      val green = (color >>> 8) & 0xFF
      val blue = (color >>> 0) & 0xFF
      val re = (red.toDouble * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255
      BigDecimal(re).setScale(4, BigDecimal.RoundingMode.HALF_UP).toDouble
    }
    PixImage(bi.getWidth, bi.getHeight, pixels)
  }

  def createPointImg(pi: PixImage, pointCnt: Int): List[DoctusPoint] = {

    @tailrec
    def createPoints(cnt: Int, points: List[DoctusPoint], img: PixImage): List[DoctusPoint] = {

      def isPoint(x: Double, y: Double, img: PixImage): Boolean = {
        val i: Int = math.floor(x * img.width).toInt
        val j: Int = math.floor(y * img.height).toInt
        val index = i * img.height + j
        val bright = img.pixels(index)
        val ranBright = ran()
        ranBright > bright
      }

      if (cnt == 0) points
      else {
        val x = ran();
        val y = ran();
        if (isPoint(x, y, img)) {
          val r = img.width.toDouble / img.height
          val p =
            if (r < 1.0) DoctusPoint((1 - r) / 2 + x * r, y)
            else DoctusPoint(x, (r - 1) / 2 + y / r)
          createPoints(cnt - 1, p :: points, img)
        } else {
          createPoints(cnt, points, img)
        }
      }
    }

    createPoints(pointCnt, List.empty[DoctusPoint], pi)
  }

}
