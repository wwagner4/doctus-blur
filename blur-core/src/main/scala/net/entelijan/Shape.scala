package net.entelijan

import java.util.Random

import doctus.core.DoctusGraphics
import doctus.core.color.DoctusColorBlack
import doctus.core.util.{DoctusPoint, DoctusVector}

trait Shape {

  def draw(g: DoctusGraphics): Unit

}

case class Circle(pos: DoctusPoint, diameter: Double, ran: Random) extends Shape {

  def draw(g: DoctusGraphics): Unit = {
    g.noStroke()
    val alpha = 5 * math.pow(1.06, -diameter)
    g.fill(DoctusColorBlack, alpha)
    val d = diameter * ran.nextDouble() * 8
    g.ellipse(pos, d, d)
  }

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

