package net.entelijan

import java.util.Random

import doctus.core._
import doctus.core.color._
import doctus.core.template._
import doctus.core.util._

sealed trait DrawDirection

case object DD_LeftToRight extends DrawDirection

case object DD_RightToLeft extends DrawDirection

case class PixImage(width: Int, height: Int, pixels: Seq[Double])

case class ImgData(ratio: Double, events: Seq[ImgEvent], cfg: BlurConfig) {
  def addEvent(event: ImgEvent): ImgData = {
    val newEvents = events :+ event
    this.copy(events = newEvents)
  }
}

case class ImgEvent(x: Double, y: Double, size: Double, direction: DrawDirection)


sealed trait BlurConfig {

  def images: Seq[PixImage]

  def realizeDirections: Boolean

  def densityFactor: Double

}

case object BC_Giacometti extends BlurConfig {
  def images = List(
    PixImageHolder.img0001,
    PixImageHolder.img0002,
    PixImageHolder.img0004,
    PixImageHolder.img0005)

  def realizeDirections = true

  def densityFactor = 1.0
}

case object BC_Buddha extends BlurConfig {
  def images = List(
    PixImageHolderB1.img0000,
    PixImageHolderB1.img0001,
    PixImageHolderB1.img0002,
    PixImageHolderB1.img0003,
    PixImageHolderB1.img0004,
    PixImageHolderB1.img0005,
    PixImageHolderB1.img0006,
    PixImageHolderB1.img0007,
    PixImageHolderB1.img0008,
    PixImageHolderB1.img0009)

  def realizeDirections = false

  def densityFactor = 6.0

}

sealed trait BlurMode

case class BM_DRAW(cfg: BlurConfig) extends BlurMode

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

case class Circle(pos: DoctusPoint, diameter: Double) extends Shape {

  def draw(g: DoctusGraphics): Unit = {
    val alpha = 255 * math.pow(1.08, -diameter)
    g.fill(DoctusColorBlack, alpha)
    g.ellipse(pos, diameter, diameter)
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

case class BlurDoctusTemplate(canvas: DoctusCanvas, sche: DoctusScheduler, pers: ImgPersitor, mode: BlurMode) extends DoctusTemplate {

  sealed trait GuiState {

    def config: BlurConfig

  }

  case class GS_DRAWING(config: BlurConfig) extends GuiState

  case class GS_MSG(msg: String, config: BlurConfig) extends GuiState

  case class GS_CLEAR(config: BlurConfig) extends GuiState

  case class GS_LOAD(id: Int) extends GuiState {

    def config: BlurConfig = throw new IllegalStateException("config not defined in GS_LOAD")

  }

  override def frameRate = None

  var config = Option.empty[BlurConfig]

  private var guiState: GuiState = mode match {
    case BM_DRAW(cfg) =>
      config = Some(cfg)
      GS_MSG("Hit the space button to start", cfg)
    case BM_REDRAW(id) => GS_LOAD(id)
  }

  private val ran = new java.util.Random

  private val imgRan: Random = new java.util.Random(20349803L)

  private var shapes: List[Shape] = List.empty[Shape]

  private var startPoint = DoctusPoint(0, 0)

  private var imgEvents: Seq[ImgEvent] = createNewImageEvents

  private def createNewImageEvents: Seq[ImgEvent] = {
    List.empty[ImgEvent]
  }

  private def createShapes(size: Double, off: DoctusVector, dir: DrawDirection, config: BlurConfig): List[Shape] = {
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
//      Line(pos1, length, stroke, ranAngle, 0)
      //println("len=%.2f" format length)
      Circle(pos1, length)
    }
  }

  def draw(g: DoctusGraphics): Unit = {
    guiState match {
      case GS_DRAWING(_) =>
        shapes.foreach { l => l.draw(g) }
      case GS_MSG(msg, _) =>
        drawTextBox(g, msg)
      case GS_CLEAR(cfg) =>
        drawWhiteBackground(g)
        guiState = GS_DRAWING(cfg)
      case GS_LOAD(id) =>
        println("loading data")
        val data: ImgData = pers.load(id)
        val w = canvas.width.toDouble
        val h = canvas.height.toDouble
        val r = w / h
        val (xscale, yscale, xoff, yoff) = if (data.ratio > r) {
          (w, w / data.ratio, 0.0, (h - w / data.ratio) * 0.5)
        } else {
          (h * data.ratio, h, (w - h * data.ratio) * 0.5, 0.0)
        }
        drawWhiteBackground(g)
        data.events.foreach { evt =>
          val si = evt.size * yscale
          val x = evt.x * xscale + xoff
          val y = evt.y * yscale + yoff
          shapes = createShapes(si, DoctusVector(x, y), evt.direction, data.cfg)
          println("writing shapes")
          shapes.foreach { l => l.draw(g) }
        }
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
    val dir =
      if (guiState.config.realizeDirections) {
        if (startPoint.x > pos.x) DD_LeftToRight
        else DD_RightToLeft
      } else {
        DD_RightToLeft
      }
    shapes = createShapes(size, off, dir, guiState.config)
    imgEvents = imgEvents :+ ImgEvent(off.x, off.y, size, dir)
    canvas.repaint()
  }

  def createImgData: ImgData = {
    val w = canvas.width
    val h = canvas.height
    val events = this.imgEvents.map { e =>
      val s = e.size / h
      val x = e.x / w
      val y = e.y / h
      ImgEvent(x, y, s, e.direction)
    }
    val r = w.toDouble / h
    ImgData(r, events, guiState.config)
  }

  def keyPressed(code: DoctusKeyCode): Unit = {
    guiState match {
      case GS_DRAWING(cfg) =>
        val imgData = createImgData
        val id = pers.save(imgData)
        imgEvents = createNewImageEvents
        guiState = GS_MSG("Saved to %d" format id, cfg)
        canvas.repaint()
      case GS_MSG(_, cfg) =>
        guiState = GS_CLEAR(cfg)
        canvas.repaint()
      case GS_CLEAR(cfg) =>
        guiState = GS_DRAWING(cfg)
        canvas.repaint()
      case GS_LOAD(id) => // Nothing to do here
    }
  }

}

