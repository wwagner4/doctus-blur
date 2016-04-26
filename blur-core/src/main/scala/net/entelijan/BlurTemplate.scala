package net.entelijan

import doctus.core._
import doctus.core.util._
import doctus.core.template._
import doctus.core.color._

case class PixImage(width: Int, height: Int, pixels: Seq[Double])

case class Line(
    pos: DoctusPoint,
    length: Double,
    weight: Double,
    angle: Double,
    blur: Int) {

  def draw(g: DoctusGraphics, size: Double, off: DoctusVector): Unit = {
    val pos1 = DoctusPoint(pos.x * size, pos.y * size) + off
    val vek = DoctusVector(0, length / 2.0).rot(angle)
    val p1 = pos1 + vek
    val p2 = pos1 - vek
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

  var lines: List[Line] = List.empty[Line]
  

  
  def createRandomLines(cnt: Int): List[Line] = {
    val pi = pixImages(ran.nextInt(pixImages.size))
    val poimg = PointImageGenerator.createPointImage(pi, cnt)
    def ranAngle: Double = ran.nextDouble() * math.Pi
    poimg.points.map { p =>
      Line(p, 10, 1, ranAngle, 0)
    }
  }

  def draw(g: DoctusGraphics): Unit = {

    g.noStroke()
    g.fill(DoctusColorWhite, 100)
    g.rect(0, 0, canvas.width, canvas.height)
    
    val size = 100 + ran.nextInt(900)
    val x = ran.nextInt(canvas.width)
    val y = ran.nextInt(canvas.height) - size * 0.5

    val cnt = (size * size * 0.005).toInt
    lines = createRandomLines(cnt)
    
    lines.foreach { l => l.draw(g, size, DoctusVector(x, y)) }
  }

  def pointableDragged(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointablePressed(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointableReleased(pos: DoctusPoint): Unit = () // Nothing to do here

  def keyPressed(code: DoctusKeyCode): Unit = {
    if (code == DKC_Space) {
      canvas.repaint()
    }
  }

}

