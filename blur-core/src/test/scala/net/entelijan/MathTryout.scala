package net.entelijan

object MathTryout extends App {
  
  (0 to (50, 10)) foreach { x =>
    val y = 255 * math.pow(1.08, -x)
    println("%10d -> %.2f" format(x, y))
  }
  
  
  
  
}