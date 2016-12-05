package net.entelijan


sealed trait BlurConfig {

  def images: Seq[PixImage]
  def realizeDirections: Boolean
  def densityFactor: Double
}



case object BC_Giacometti extends BlurConfig {
  def images = List(
    PixImageHolder.img0001,
    PixImageHolder.img0002,
    PixImageHolder.img0004,
    PixImageHolder.img0005)

  def realizeDirections = true

  def densityFactor = 1.0
}

case object BC_Buddha extends BlurConfig {
  def images = List(
    PixImageHolderB1.img0000,
    PixImageHolderB1.img0001,
    PixImageHolderB1.img0002,
    PixImageHolderB1.img0003,
    PixImageHolderB1.img0004,
    PixImageHolderB1.img0005,
    PixImageHolderB1.img0006,
    PixImageHolderB1.img0007,
    PixImageHolderB1.img0008,
    PixImageHolderB1.img0009)

  def realizeDirections = false

  def densityFactor = 3.0

}

case object BC_C extends BlurConfig {
  def images = List(
    PixImageHolderC1.img0000,
    PixImageHolderC1.img0001,
    PixImageHolderC1.img0002,
    PixImageHolderC1.img0003,
    PixImageHolderC1.img0004,
    PixImageHolderC1.img0005,
    PixImageHolderC1.img0006,
    PixImageHolderC1.img0007,
    PixImageHolderC1.img0008,
    PixImageHolderC1.img0009,
    PixImageHolderC1.img0010,
    PixImageHolderC1.img0011,
    PixImageHolderC1.img0012,
    PixImageHolderC1.img0013,
    PixImageHolderC1.img0014)

  def realizeDirections = true

  def densityFactor = 3.0

}

