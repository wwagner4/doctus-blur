package net.entelijan

import doctus.core._
import doctus.core.util._
import doctus.core.template._
import doctus.core.color._

case class Line(
    pos: DoctusPoint,
    length: Double,
    weight: Double,
    angle: Double,
    blur: Int) {

  def draw(g: DoctusGraphics, canvas: DoctusCanvas): Unit = {
    val pos1 = DoctusPoint(pos.x * canvas.width, pos.y * canvas.height)
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

  val ran = new java.util.Random

  var lines: List[Line] = createRandomLines(200)

  sche.start(updateLines, 100)
  
  def updateLines(): Unit = {
    lines = nextLines(lines)
  }

  
  def createRandomLines(cnt: Int): List[Line] = {

    def ranPos: DoctusPoint = DoctusPoint(ran.nextDouble(), ran.nextDouble())

    def ranAngle: Double = ran.nextDouble() * math.Pi

    (1 to cnt).toList.map { _ =>
      Line(ranPos, 100, 2, ranAngle, 3)
    }
  }

  def nextLines(current: List[Line]): List[Line] = {
    current.map { l =>
      val angle = l.angle
      val next = (angle + 0.1) % math.Pi
      l.copy(angle = next)
    }
  }

  def draw(g: DoctusGraphics): Unit = {

    g.noStroke()
    g.fill(DoctusColorWhite, 255)
    g.rect(0, 0, canvas.width, canvas.height)
    
    lines.foreach { l => l.draw(g, canvas) }
  }

  def pointableDragged(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointablePressed(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointableReleased(pos: DoctusPoint): Unit = () // Nothing to do here

  def keyPressed(code: DoctusKeyCode): Unit = () // Nothing to do here

}

