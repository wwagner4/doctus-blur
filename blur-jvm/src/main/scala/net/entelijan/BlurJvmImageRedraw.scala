package net.entelijan

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import doctus.core.template._
import doctus.jvm._
import doctus.jvm.awt.{DoctusBufferedImage, DoctusTemplateCanvasBufferedImage}

case class RedrawConfig(info: String, width: Int, height: Int, id: Int)

object BlurJvmImageRedraw extends App {

  val cfgAllSizes = {
    List(5, 8, 11).flatMap { id =>
      val info = "allsizes"
      List(
        RedrawConfig(info + "SMALL", 1500, 700, id),
        RedrawConfig(info + "A4", 3564, 2520, id),
        RedrawConfig(info + "A3", 5040, 3564, id),
        RedrawConfig(info, 15000, 8000, id))
    }
  }

  val cfgAllImages = {
    (0 to 0).map(i => RedrawConfig("allimg", 2500, 1500, i)).toParArray
  }

  cfgAllSizes.toParArray.foreach {
    redraw
  }
  System.exit(0)

  def redraw(cfg: RedrawConfig) {
    val home = new File(System.getProperty("user.home"))
    val out = new File(home, "blur_out")
    out.mkdirs()
    val file = new File(out, "blur_%s_%04d_%d_%d.png" format(cfg.info, cfg.id, cfg.width, cfg.height))

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
