package net.entelijan

import doctus.core._
import doctus.core.util._
import doctus.core.template._
import doctus.core.color._

case class PixImage(width: Int, height: Int, pixels: Seq[Double])

case class ImgData(ratio: Double, events: Seq[ImgEvent]) {
  def addEvent(event: ImgEvent): ImgData = {
    val newEvents = events :+ event
    this.copy(events = newEvents)
  }
}

case class ImgEvent(x: Double, y: Double, size: Double)

sealed trait BlurMode

case object BM_DRAW extends BlurMode
case class BM_REDRAW(id: Int) extends BlurMode


trait ImgPersitor {

  /**
    * Saves the image data and returns the id
    * for loading that data
    */
  def save(data: ImgData): Int

  def load(id: Int): ImgData
}

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

case class BlurDoctusTemplate(canvas: DoctusCanvas, sche: DoctusScheduler, pers: ImgPersitor, mode: BlurMode) extends DoctusTemplate {

  sealed trait GuiState

  case object GS_DRAWING extends GuiState
  case class GS_MSG(msg: String) extends GuiState
  case object GS_CLEAR extends GuiState
  case class GS_LOAD(id: Int) extends GuiState

  override def frameRate = None

  var guiState: GuiState = GS_MSG("Hit the space button to start")

  val ran = new java.util.Random

  lazy val pixImages = List(PixImageHolder.img0001, PixImageHolder.img0002, PixImageHolder.img0004, PixImageHolder.img0005)

  var shapes: List[Shape] = List.empty[Shape]

  var startPoint = DoctusPoint(0, 0)

  var imgData: ImgData = createNewImageData

  private def createNewImageData: ImgData = {
    val ratio = canvas.width.toDouble / canvas.height
    ImgData(ratio, Seq.empty[ImgEvent])
  }

  private def createShapes(size: Double, off: DoctusVector): List[Shape] = {
    val pi = pixImages(ran.nextInt(pixImages.size))
    val cnt = (math.pow(size, 1.3) * 0.7).toInt
    val poimg = PointImageGenerator.createPointImage(pi, cnt)
    def ranAngle: Double = ran.nextDouble() * math.Pi
    poimg.points.map { pos =>
      val pos1 = DoctusPoint(pos.x * size, pos.y * size) + off
      Line(pos1, size / 50, size / 500, ranAngle, 0)
    }
  }

  def draw(g: DoctusGraphics): Unit = {
    guiState match {
      case GS_DRAWING =>
        shapes.foreach { l => l.draw(g) }
      case GS_MSG(msg) =>
        drawTextBox(g, msg)
      case GS_CLEAR =>
        drawWhiteBackground(g)
        guiState = GS_DRAWING
      case GS_LOAD(id) =>
        println("LOAD not impl")
    }
  }

  def drawTextBox(g: DoctusGraphics, msg: String): Unit = {
    drawWhiteBackground(g)
    val txtSize = 50
    val txtWidth = msg.length * txtSize * 0.6
    val txtHeight = txtSize * 1.3
    val xoff = (canvas.width - txtWidth) * 0.5
    val yoff = (canvas.height - txtHeight) * 0.5
    val origin = DoctusPoint(xoff, yoff)
    g.fill(DoctusColorOrange, 200)
    g.noStroke()
    g.rect(origin, txtWidth, txtHeight)
    g.fill(DoctusColorBlack, 150)
    g.textSize(txtSize)
    g.text(msg, origin + DoctusVector(10, txtSize), 0)
  }

  def drawWhiteBackground(g: DoctusGraphics): Unit = {
    g.noStroke()
    g.fill(DoctusColorWhite, 255)
    g.rect(0, 0, canvas.width, canvas.height)
  }

  def pointableDragged(pos: DoctusPoint): Unit = ()

  def pointablePressed(pos: DoctusPoint): Unit = {
    startPoint = pos
  }

  def pointableReleased(pos: DoctusPoint): Unit = {
    val vec = startPoint - pos
    val size = math.abs(vec.y)
    val off = startPoint - DoctusPoint(0, 0)
    shapes = createShapes(size, off)
    imgData = imgData.addEvent(ImgEvent(off.x, off.y, size))
    canvas.repaint()
  }

  def keyPressed(code: DoctusKeyCode): Unit = {
    guiState match {
      case GS_DRAWING =>
        val id = pers.save(imgData)
        imgData = createNewImageData
        guiState = GS_MSG("Saved to %d" format id)
        canvas.repaint()
      case GS_MSG(_) =>
        guiState = GS_CLEAR
        canvas.repaint()
      case GS_CLEAR =>
        guiState = GS_DRAWING
        canvas.repaint()
      case GS_LOAD(id) =>
        println("LOAD not impl")
    }
  }

}

