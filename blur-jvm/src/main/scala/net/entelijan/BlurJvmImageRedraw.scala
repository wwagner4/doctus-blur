package net.entelijan

import java.awt.image.BufferedImage
import java.io.File
import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.scene._
import javafx.scene.canvas.Canvas
import javafx.stage.Stage
import javax.imageio.ImageIO

import doctus.core.template._
import doctus.jvm._
import doctus.jvm.awt.{DoctusBufferedImage, DoctusTemplateCanvasBufferedImage}

object BlurJvmImageRedraw extends App {

  val mode = BM_REDRAW(0)
  val width = 15000
  val height = 10000

  val home = new File(System.getProperty("user.home"))

  val file = new File(home, "blur_%d_%d.png" format(width, height))


  println("in start")

  //val canvasFx = new Canvas(width, height)

  val sched = DoctusSchedulerJvm
  //val canvas = DoctusTemplateCanvasFx(canvasFx)

  val bi1 = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
  val dbi = DoctusBufferedImage(bi1)
  val canvas1 = DoctusTemplateCanvasBufferedImage(dbi)

  //val grp = new Group()
  //grp.getChildren.add(canvasFx)

  val pers = ImgPersistorJvm

  // Common to all platforms
  val templ = BlurDoctusTemplate(canvas1, sched, pers, mode)
  DoctusTemplateController(templ, sched, canvas1)
  canvas1.repaint()

  println("writing file")
  //val wi = grp.snapshot(new SnapshotParameters(), null)
  //val bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
  //val biout = SwingFXUtils.fromFXImage(wi, bi)
  ImageIO.write(bi1, "png", file)
  println("wrote to '%s'" format file)

  System.exit(0)
}
