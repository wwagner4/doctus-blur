package net.entelijan

import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLCanvasElement
import doctus.scalajs._
import doctus.core.template._

@JSExport("BlurScalaJs")
object BlurScalaJs {

  @JSExport
  def main() {

    val canvasElem: HTMLCanvasElement = dom.document.getElementById("canvas").asInstanceOf[HTMLCanvasElement]

    val canvas = DoctusTemplateCanvasScalajs(canvasElem)
    val sched = DoctusSchedulerScalajs

    // Common to all platforms
    val templ = net.entelijan.BlurDoctusTemplate(canvas, sched)
    DoctusTemplateController(templ, sched, canvas)


  }

}