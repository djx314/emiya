package org.xarcher.emiya

import java.awt.{Color, Font, Toolkit}
import java.awt.datatransfer.{DataFlavor, Transferable, UnsupportedFlavorException}
import java.io.{File, FileInputStream, InputStream}
import javax.imageio.ImageIO

import net.coobird.thumbnailator.Thumbnails
import net.coobird.thumbnailator.filters.{Canvas, Caption}
import net.coobird.thumbnailator.geometry.Positions

object CopyPic {

  def pic(file: File)(content: String): Unit = {

    var inputStream: InputStream = null
    try {
      inputStream = new FileInputStream(file)
      val aaa = ImageIO.read(inputStream)
      val targetWidth = Math.min(400, aaa.getWidth)
      val targetHeight = if (aaa.getWidth <= 400) aaa.getHeight else (aaa.getHeight.toDouble / aaa.getWidth.toDouble * targetWidth.toDouble).toInt
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
          Array[DataFlavor](DataFlavor.imageFlavor)
        }

        override def isDataFlavorSupported(flavor: DataFlavor): Boolean = {
          DataFlavor.imageFlavor.equals(flavor)
        }

        override def getTransferData(flavor: DataFlavor): AnyRef = {
          if(isDataFlavorSupported(flavor)) {
            captionedImage
          } else
            throw new UnsupportedFlavorException(flavor)
        }

      }

      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null)
    } catch {
      case e: Throwable => e.printStackTrace
    } finally {
      try {
        inputStream.close
      } catch {
        case e: Exception =>
      }
    }

  }

}