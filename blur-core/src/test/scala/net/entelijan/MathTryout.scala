package net.entelijan

object MathTryout extends App {


  def f: Unit = {
    (0 to (5000, 50)) foreach { x =>
      val y = math.max(x.toDouble / 2000, 0.1) - math.pow(x.toDouble, 1.5) / 350000
      println("%10d -> %.2f" format(x, y))
    }
  }


  // silly comment added
  
}