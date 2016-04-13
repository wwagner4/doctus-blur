package net.entelijan

import doctus.core._
import doctus.core.util._
import doctus.core.template._
import doctus.core.color._

case class Line(pos: DoctusPoint, weight: Double, blur: Int)

case class BlurDoctusTemplate(canvas: DoctusCanvas) extends DoctusTemplate {
  
  override def frameRate = None

  val ran = new java.util.Random

  var rows = for (i <- 0 until 5) yield row(i)

  def row(j: Int) = for (i <- 0 until 10) yield line(j, i)

  def line(col: Int, row: Int): Line = Line(DoctusPoint(30 + col * 200, (row + 1) * 45), 1 + row * 2.5, col)

  def draw(g: DoctusGraphics): Unit = {
    g.noStroke()
    g.fill(DoctusColorWhite, 255)
    g.rect(0, 0, canvas.width, canvas.height)

    def drawRow(row: Seq[Line]): Unit = row.foreach { line =>
      require(line.blur >= 0)
      
      val x = line.pos.x
      val y = line.pos.y
      val alpha = 255 * math.pow(2, -line.blur)
      println("alpha = %.2f <- %d" format (alpha, line.blur))
      (1 to (line.blur * 2 + 1)).foreach { strokeFac =>
        g.stroke(DoctusColorBlack, alpha)
        g.strokeWeight(line.weight * strokeFac)
        g.line(x, y, x, y + 40)

      }
    }

    rows.foreach { row => drawRow(row) }
  }

  def pointableDragged(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointablePressed(pos: DoctusPoint): Unit = () // Nothing to do here

  def pointableReleased(pos: DoctusPoint): Unit = () // Nothing to do here

  def keyPressed(code: DoctusKeyCode): Unit = () // Nothing to do here

}

