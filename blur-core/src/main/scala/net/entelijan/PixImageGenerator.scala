package net.entelijan

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import scala.BigDecimal
import java.io.File
import doctus.core.util.DoctusPoint
import java.util.Random
import scala.annotation.tailrec
import java.io.InputStream

case class PixImage(width: Int, height: Int, pixels: Seq[Double])

object PixImageGenerator {

  val random = new Random()

  def createPixImage(imgStream: InputStream): PixImage = {
    val bi: BufferedImage = ImageIO.read(imgStream)
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

}
