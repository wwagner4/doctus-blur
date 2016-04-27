package net.entelijan

import doctus.core._
import doctus.core.util._
import doctus.core.template._
import doctus.core.color._

case class PixImage(width: Int, height: Int, pixels: Seq[Double])

trait Shape {

  def draw(g: DoctusGraphics): Unit

}

case class Line(
    pos: DoctusPoint,
    length: Double,
    weight: Double,
    angle: Double,
    blur: Int) extends Shape {

  def draw(g: DoctusGraphics): Unit = {
    val vek = DoctusVector(0, length / 2.0).rot(angle)
    val p1 = pos + vek
    val p2 = pos - vek
    val strokeFacs = (1 to (blur + 1)).toList
    val alphas = strokeFacs.map { _ => 255 * math.pow(2, -blur) }
    val asum = alphas.sum
    val anorm = alphas.map { a => a / asum * 255 }
    strokeFacs.zip(anorm).foreach {
      case (strokeFac, alpha) =>
        g.stroke(DoctusColorBlack, alpha)
        g.strokeWeight(weight + (strokeFac - 1) * 5)
        g.line(p1, p2)
    }

  }

}

case class BlurDoctusTemplate(canvas: DoctusCanvas, sche: DoctusScheduler) extends DoctusTemplate {

  override def frameRate = None

  val ran = new java.util.Random

  lazy val pixImages = List(PixImageHolder.img0001, PixImageHolder.img0002, PixImageHolder.img0004, PixImageHolder.img0005)

  var shapes: List[Shape] = List.empty[Shape]

  var startPoint = DoctusPoint(0, 0)

  def createRandomLines(size: Double, off: DoctusVector): List[Shape] = {
    //println("size:" + size)
    val pi = pixImages(ran.nextInt(pixImages.size))
    val cnt = (size * size * 0.05).toInt
    val poimg = PointImageGenerator.createPointImage(pi, cnt)
    def ranAngle: Double = ran.nextDouble() * math.Pi
    poimg.points.map { pos =>
      val pos1 = DoctusPoint(pos.x * size, pos.y * size) + off
      Line(pos1, size / 50, size / 500, ranAngle, 0)
    }
  }

  def draw(g: DoctusGraphics): Unit = {

    g.noStroke()
    g.fill(DoctusColorWhite, 50)
    g.rect(0, 0, canvas.width, canvas.height)

    shapes.foreach { l => l.draw(g) }
  }

  def pointableDragged(pos: DoctusPoint): Unit = ()

  def pointablePressed(pos: DoctusPoint): Unit = {
    startPoint = pos
  }

  def pointableReleased(pos: DoctusPoint): Unit = {
    val vec = startPoint - pos
    val size = math.abs(vec.y)
    val off = startPoint - DoctusPoint(0, 0)
    shapes = createRandomLines(size, off)
    canvas.repaint()
  }

  def keyPressed(code: DoctusKeyCode): Unit = ()

}

