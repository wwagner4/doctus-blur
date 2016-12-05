package net.entelijan

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


case class BlurDoctusTemplate(canvas: DoctusCanvas, sche: DoctusScheduler, pers: ImgPersitor, blurConfig: BlurConfig)
  extends DoctusTemplate with BlurTemplateCommon {

  sealed trait GuiState {

    def config: BlurConfig

  }

  case class GS_DRAWING(config: BlurConfig) extends GuiState

  case class GS_MSG(msg: String, config: BlurConfig) extends GuiState

  case class GS_CLEAR(config: BlurConfig) extends GuiState


  override def frameRate = None

  var config = Option.empty[BlurConfig]

  private var guiState: GuiState = GS_MSG("Hit the space button to start", blurConfig)

  private var shapes: List[Shape] = List.empty[Shape]

  private var startPoint = DoctusPoint(0, 0)

  private var imgEvents: Seq[ImgEvent] = createNewImageEvents

  private def createNewImageEvents: Seq[ImgEvent] = {
    List.empty[ImgEvent]
  }

  def draw(g: DoctusGraphics): Unit = {
    guiState match {
      case GS_MSG(msg, _) =>
        drawTextBox(g, msg)
      case GS_CLEAR(cfg) =>
        drawWhiteBackground(g)
        guiState = GS_DRAWING(cfg)
      case GS_DRAWING(_) =>
        shapes.foreach { l => l.draw(g) }
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
        imgRan = createImgRan
        guiState = GS_DRAWING(cfg)
        canvas.repaint()
    }
  }

}

