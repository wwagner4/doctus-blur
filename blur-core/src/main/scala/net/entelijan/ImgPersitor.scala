package net.entelijan

trait ImgPersitor {

  /**
    * Saves the image data and returns the id
    * for loading that data
    */
  def save(data: ImgData): Int

  /**
    * Loads an image
    * If there is any reason the image cannot be loaded
    * None is returned
    */
  def load(id: Int): Option[ImgData]
}

