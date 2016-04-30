package net.entelijan

/**
  * Scalajs implementation. Not yet implemented
  */
object ImgPersistorScalajs extends ImgPersitor {

  override def save(data: ImgData): Unit = {
    throw new IllegalStateException("Not yet implemented")
  }

  override def load(id: Int): ImgData = {
    throw new IllegalStateException("Not yet implemented")
  }
}
