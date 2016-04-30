package net.entelijan

import org.scalatest.FunSuite

class BlurFunSuite extends FunSuite {

  test("extract ids from one file") {
    val fnams = List("img0000.txt")
    val ids = IdExtractor.ids(fnams)

    assert(ids.size === 1)
    assert(ids.head === 0)
  }

  test("extract ids from many files ordered") {
    val fnams = List("img0000.txt", "img0010.txt")
    val ids = IdExtractor.ids(fnams)

    assert(ids.size === 2)
    assert(ids.max === 10)
  }

  test("extract ids from many files unordered") {
    val fnams = List("img0020.txt", "img0011.txt", "img0010.txt")
    val ids = IdExtractor.ids(fnams)

    assert(ids.size === 3)
    assert(ids.max === 20)
  }

  test("extract ids with non blur files") {
    val fnams = List("img0020.txt", "img0011.txt", ".dummy.txt", "i have some blanks.txt", "no_ext", "other_ext.png")
    val ids = IdExtractor.ids(fnams)

    assert(ids.size === 2)
    assert(ids.max === 20)
  }

}