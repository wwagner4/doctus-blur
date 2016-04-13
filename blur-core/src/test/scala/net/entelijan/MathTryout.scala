package net.entelijan

object MathTryout extends App {
  
  (0 to 7) foreach { x => 
    val y = 255 * math.pow(2, -x)
    println("%d -> %10.4f" format(x, y))
  }
  
  
  
  
}