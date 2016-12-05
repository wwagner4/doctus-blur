package net.entelijan

import java.util.Random

import doctus.core.{DoctusCanvas, DoctusGraphics}
import doctus.core.color.DoctusColorWhite
import doctus.core.util.{DoctusPoint, DoctusVector}

trait BlurTemplateCommon {

  def canvas: DoctusCanvas

  val ran = new java.util.Random

  var imgRan: Random = createImgRan

  def createImgRan = new java.util.Random(20349803L)

  protected def drawWhiteBackground(g: DoctusGraphics): Unit = {
    g.noStroke()
    g.fill(DoctusColorWhite, 255)
    g.rect(0, 0, canvas.width, canvas.height)
  }

  protected def createShapes(size: Double, off: DoctusVector, dir: DrawDirection, config: BlurConfig): List[Shape] = {
    val pixImages = config.images
    val pi = pixImages(imgRan.nextInt(pixImages.size))
    val pir = pi.width.toDouble / pi.height
    val cnt = (math.pow(size, 0.2) * 5000.0 * config.densityFactor).toInt
    val poimg = PointImageGenerator.createPointImage(pi, cnt)
    def ranAngle: Double = ran.nextDouble() * math.Pi
    poimg.points.map { pos =>
      val xoff = if (pir < 1) 0.5 * pir else 0.5
      val x =
        if (dir == DD_LeftToRight) pos.x
        else if (pir < 1) pir - pos.x
        else 1.0 - pos.x
      val pos1 = DoctusPoint((x - xoff) * size, pos.y * size) + off
      val stroke = math.max(size / 2000, 0.1)
      val length = size / 150
      Line(pos1, length, stroke, ranAngle, 0)
      //println("len=%.2f" format length)
      //      Circle(pos1, length, ran)
    }
  }




}
