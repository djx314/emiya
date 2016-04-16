package org.xarcher.emiya

import java.awt.{Color, Font, Toolkit}
import java.awt.datatransfer.{DataFlavor, Transferable, UnsupportedFlavorException}
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import net.coobird.thumbnailator.Thumbnails
import net.coobird.thumbnailator.filters.{Canvas, Caption}
import net.coobird.thumbnailator.geometry.Positions

object CopyPic {

  def pic(content: String): Unit = {

    val aaa = ImageIO.read(new File("/home/djx314/soft/idea-IC-145.258.11/bin/idea.png"))
    val targetWidth = aaa.getWidth
    val targetHeight = aaa.getHeight
    val bbb = Thumbnails.of(aaa).size(targetWidth, targetHeight)

    // Set up the caption properties
    val caption = content
    val font = new Font("Monospaced", Font.PLAIN, 14)
    val c = Color.black
    val position = Positions.BOTTOM_CENTER
    val insetPixels = 5

    // Apply caption to the image
    val filter = new Caption(caption, font, c, position, insetPixels)
    val colorFilter = new Canvas(Math.max(14 * (content.getBytes("UTF-8").length + content.length) / 4 + 10, targetWidth), targetHeight + 20, Positions.TOP_CENTER, Color.WHITE)
    val captionedImage = bbb.addFilter(colorFilter).addFilter(filter).asBufferedImage

    val trans = new Transferable {

      override def getTransferDataFlavors(): Array[DataFlavor] = {
        println(Array[DataFlavor](DataFlavor.imageFlavor))
        Array[DataFlavor](DataFlavor.imageFlavor)
      }

      override def isDataFlavorSupported(flavor: DataFlavor): Boolean = {
        println(DataFlavor.imageFlavor.equals(flavor))
        DataFlavor.imageFlavor.equals(flavor)
      }

      override def getTransferData(flavor: DataFlavor): AnyRef = {
        if(isDataFlavorSupported(flavor)) {
          println(1111)
          captionedImage
        } else
          throw new UnsupportedFlavorException(flavor)
      }

    }

    //Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null)
    println(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.imageFlavor).asInstanceOf[BufferedImage])

  }

}