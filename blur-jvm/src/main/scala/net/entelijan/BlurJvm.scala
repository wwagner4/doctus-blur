package net.entelijan

import doctus.jvm._
import doctus.core.template._
import doctus.core._
import java.util.ArrayList
import java.util.List
import java.util.Random
import javafx.application.Application
import javafx.scene._
import javafx.stage.Stage
import javafx.scene.canvas.Canvas
import javafx.application._
import javafx.scene._
import javafx.scene.paint._
import javafx.stage.Stage
import javafx.stage.WindowEvent
import javafx.event.EventHandler
import javafx.event.ActionEvent
import javafx.event.Event

object BlurJvmRedraw extends App {

  class BlurAppRedraw extends BlurApp {
    def mode = BM_REDRAW(8)
  }

  Application.launch(classOf[BlurAppRedraw], args: _*)
}

object BlurJvmDraw extends App {

  class BlurAppDraw extends BlurApp {
    def mode = BM_DRAW
  }

  Application.launch(classOf[BlurAppDraw], args: _*)
}

trait BlurApp extends Application {

  def mode: BlurMode

  override def start(stage: Stage) {

    val width = 1000
    val height = 520

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
    val templ = BlurDoctusTemplate(canvas, sched, pers, mode)
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
