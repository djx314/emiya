package org.xarcher.emiya

import java.awt.{ Color, Font, Toolkit }
import java.awt.datatransfer.{ DataFlavor, Transferable, UnsupportedFlavorException }
import java.io._
import javax.imageio.ImageIO
import javax.imageio.stream.MemoryCacheImageInputStream

import net.coobird.thumbnailator.Thumbnails
import net.coobird.thumbnailator.filters.{ Canvas, Caption }
import net.coobird.thumbnailator.geometry.Positions

import scalafx.scene.image.Image
import scalafx.scene.input.{ Clipboard, ClipboardContent }

object CopyPic {

  def pic(file: File)(content: String): Unit = {

    val inputStream: InputStream = new FileInputStream(file)
    val formatNameStrean: InputStream = new FileInputStream(file)
    val outStream = new ByteArrayOutputStream()
    try {
      val formatName = ImageIO.getImageReaders(ImageIO.createImageInputStream(formatNameStrean)).next().getFormatName

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

      val captionedImage = bbb.addFilter(colorFilter).addFilter(filter).asBufferedImage()
      val clipborad = Clipboard.systemClipboard
      val clipboardContent = new ClipboardContent()
      clipboardContent.putImage {
        ImageIO.write(captionedImage, formatName, outStream)
        val is = new ByteArrayInputStream(outStream.toByteArray())
        new Image(is)
      }
      clipborad.content = clipboardContent
    } catch {
      case e: Throwable => e.printStackTrace
    } finally {
      try {
        inputStream.close
      } catch {
        case e: Exception =>
          e.printStackTrace
      }
      try {
        outStream.close
      } catch {
        case e: Exception =>
          e.printStackTrace
      }
      try {
        formatNameStrean.close
      } catch {
        case e: Exception =>
          e.printStackTrace
      }
    }

  }

}