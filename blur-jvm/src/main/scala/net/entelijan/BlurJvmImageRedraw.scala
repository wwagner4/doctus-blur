package net.entelijan

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import doctus.core.template._
import doctus.jvm._
import doctus.jvm.awt.{DoctusBufferedImage, DoctusTemplateCanvasBufferedImage}

case class RedrawConfig(width: Int, height: Int, id: Int)

object BlurJvmImageRedraw extends App {

  val cfgList1 = {
    val id = 0
    List(
      RedrawConfig(1500, 700, id),
      RedrawConfig(2500, 1500, id),
      RedrawConfig(8000, 3500, id),
      RedrawConfig(15000, 8000, id)).toParArray
  }

  val cfgList2 = {
    List(
      RedrawConfig(2500, 1500, 0),
      RedrawConfig(2500, 1500, 1),
      RedrawConfig(2500, 1500, 2),
      RedrawConfig(2500, 1500, 3),
      RedrawConfig(2500, 1500, 4),
      RedrawConfig(2500, 1500, 5),
      RedrawConfig(2500, 1500, 6),
      RedrawConfig(2500, 1500, 7)).toParArray
  }

  cfgList2.foreach {
    redraw
  }
  System.exit(0)

  def redraw(cfg: RedrawConfig) {
    val home = new File(System.getProperty("user.home"))
    val out = new File(home, "blur_out")
    out.mkdirs()
    val file = new File(out, "blur_%04d_%d_%d.png" format(cfg.id, cfg.width, cfg.height))

    println("in start")
    val sched = DoctusSchedulerJvm
    val bi = new BufferedImage(cfg.width, cfg.height, BufferedImage.TYPE_BYTE_GRAY)
    val canvas = DoctusTemplateCanvasBufferedImage(DoctusBufferedImage(bi))
    val pers = ImgPersistorJvm

    val templ = BlurDoctusTemplate(canvas, sched, pers, BM_REDRAW(cfg.id))
    DoctusTemplateController(templ, sched, canvas)
    canvas.repaint()

    println("writing file")
    ImageIO.write(bi, "png", file)
    println("wrote to '%s'" format file)

  }

}
