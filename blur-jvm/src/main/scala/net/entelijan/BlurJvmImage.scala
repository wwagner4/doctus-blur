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

object BlurJvmImageRedraw extends App {

  class BlurAppRedraw extends BlurImageApp {
    def mode = BM_REDRAW(0)
    val width = 5000
    val height = 2500
  }

  println("before launch")
  Application.launch(classOf[BlurAppRedraw], args: _*)
  println("after launch")
}

trait BlurImageApp extends Application {

  def mode: BlurMode
  def width: Int
  def height: Int

  val file = new File("/Users/wwagner4/tmp/blur1.png")



  override def start(stage1: Stage) {
    println("in start")

    val canvasFx = new Canvas(width, height)

    val sched = DoctusSchedulerJvm
    val canvas = DoctusTemplateCanvasFx(canvasFx)

    val grp = new Group()
    grp.getChildren.add(canvasFx)

    val pers = ImgPersistorJvm

    // Common to all platforms
    val templ = BlurDoctusTemplate(canvas, sched, pers, mode)
    DoctusTemplateController(templ, sched, canvas)

    println("writing file")
    val wi = grp.snapshot(new SnapshotParameters(), null)
    val bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val biout = SwingFXUtils.fromFXImage(wi, bi)
    ImageIO.write(biout, "png", file)

    System.exit(0)
  }
}
