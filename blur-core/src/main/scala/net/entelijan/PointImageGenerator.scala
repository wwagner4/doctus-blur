package net.entelijan

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import scala.BigDecimal
import java.io.File
import doctus.core.util.DoctusPoint
import java.util.Random
import scala.annotation.tailrec
import java.io.InputStream

case class PointImage(points: List[DoctusPoint], aspectRatio: Double)

object PointImageGenerator extends {

  val random = new Random()
  def ran(): Double = random.nextDouble()

  def createPointImage(piximg: PixImage, pointCnt: Int): PointImage = {
    val ratio = piximg.width.toDouble / piximg.height
    val points = createPoints(pointCnt, List.empty[DoctusPoint], piximg)
    PointImage(points, ratio)
  }

  @tailrec
  private def createPoints(cnt: Int, points: List[DoctusPoint], img: PixImage): List[DoctusPoint] = {

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

}
