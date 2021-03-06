package net.entelijan

import javafx.application.Application
import javafx.event.{Event, EventHandler}
import javafx.scene._
import javafx.scene.canvas.Canvas
import javafx.scene.paint._
import javafx.stage.Stage

import doctus.core.template._
import doctus.jvm._

object BlurJvmDraw extends App {

  class BlurAppDraw extends BlurApp {
    def config: BlurConfig = BC_Giacometti
    val width = 1000
    val height = 600
  }

  Application.launch(classOf[BlurAppDraw], args: _*)
}

trait BlurApp extends Application {

  def config: BlurConfig
  def width: Int
  def height: Int

  override def start(stage: Stage) {


    val canvasFx = new Canvas()

    val sched = DoctusSchedulerJvm
    val canvas = DoctusTemplateCanvasFx(canvasFx)

    val grp = new Group()
    grp.getChildren.add(canvasFx)

    val bgCol = Color.WHITE
    val scene = new Scene(grp, width, height, bgCol)
    canvasFx.widthProperty().bind(scene.widthProperty())
    canvasFx.heightProperty().bind(scene.heightProperty())
    val pers = ImgPersistorJvm

    // Common to all platforms
    val templ = BlurDoctusTemplate(canvas, sched, pers, config)
    DoctusTemplateController(templ, sched, canvas)

    stage.setScene(scene)

    stage.show()

    def handler[T <: Event](h: (T => Unit)): EventHandler[T] =
      new EventHandler[T] {
        override def handle(event: T): Unit = h(event)
      }

    stage.setOnCloseRequest(handler(e => System.exit(0)))


  }
}
