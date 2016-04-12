package net.entelijan

import doctus.core._
import doctus.core.util._
import doctus.core.template._
import doctus.core.color._

case class Line(pos: DoctusPoint)

case class BlurDoctusTemplate(canvas: DoctusCanvas) extends DoctusTemplate {

  val ran = new java.util.Random

  var rows = for (i <- 1 to 10) yield row(i)

  def row(j: Int) = for (i <- 1 to 20) yield line(j, i)

  def line(col: Int, row: Int): Line = Line(DoctusPoint(col, row))

  def draw(g: DoctusGraphics): Unit = {
    g.stroke(DoctusColorBlack, 255)
    g.strokeWeight(5)

    def drawRow(row: Seq[Line]): Unit = row.foreach { line =>
      val x = line.pos.x
      val y = line.pos.y
      g.line(x * 10, y * 10, x * 10, y * 10 + 100)
    }

    rows.foreach { row => drawRow(row) }
  }

  def pointableDragged(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointablePressed(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointableReleased(pos: DoctusPoint): Unit = () // Nothing to do here

  def keyPressed(code: DoctusKeyCode): Unit = () // Nothing to do here

}

