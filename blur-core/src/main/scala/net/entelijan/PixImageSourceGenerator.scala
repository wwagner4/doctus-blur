package net.entelijan

object PixImageSourceGenerator extends App {
  
  val urlStr = "a1/g10a.png"
  val url = getClass.getClassLoader.getResource(urlStr)
  require(url != null, "resource '%s' does not exist" format urlStr)
  val in = url.openStream()
  val pi = PixImageGenerator.createPixImage(in)
  println(format(pi))
  in.close()
  
  def format(pi: PixImage): String = 
s"""
package net.entelijan

object PixImageHolder {
  
  val img01 = $pi
}  
  
"""    
    
  
}