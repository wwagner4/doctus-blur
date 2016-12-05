package net.entelijan

import java.util.Random

import doctus.core.{DoctusCanvas, DoctusGraphics, DoctusKeyCode}
import doctus.core.template.DoctusTemplate
import doctus.core.util.{DoctusPoint, DoctusVector}

case class BlurTemplateReload(canvas: DoctusCanvas, data: ImgData) extends BlurTemplateCommon with DoctusTemplate {

  override def frameRate = None

  override def draw(g: DoctusGraphics): Unit = {
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
      val shapes = createShapes(si, DoctusVector(x, y), evt.direction, data.cfg)
      println("writing shapes")
      shapes.foreach { _.draw(g) }
    }

  }

  override def keyPressed(code: DoctusKeyCode): Unit = ()

  override def pointablePressed(pos: DoctusPoint): Unit = ()

  override def pointableReleased(pos: DoctusPoint): Unit = ()

  override def pointableDragged(pos: DoctusPoint): Unit = ()

}
