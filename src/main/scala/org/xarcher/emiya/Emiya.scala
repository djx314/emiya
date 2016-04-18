package org.xarcher.emiya

import java.io.{File, FileInputStream, InputStream}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.input.{DragEvent, MouseEvent, TransferMode}

import scala.collection.mutable.ListBuffer
import scala.util.Try
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.beans.property.BooleanProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout._

object Emiya extends JFXApp {

  object VarModel {

    trait AbsVar[T] {
        var model: T = _
        def setTo(model1: T): T = {
          model = model1
          model
        }
        def get: T = model
    }

    def empty[T] = new AbsVar[T] { }

  }

  def writeClipboard: Unit = {
    scala.collection.JavaConversions.iterableAsScalaIterable(pictureList).toList.find(_.isSelected.value).foreach {
      s =>
        CopyPic.pic(new FileInputStream(s.file))(field.get.text.value)
    }
  }

  case class SelectPicture(file: File) {
    val current = this

    val isSelected = BooleanProperty(false)

    private var inStream: InputStream = _

    val pictureImage: Image = {
      try {
        inStream = new FileInputStream(file)
        new Image(inStream)
      } finally {
        try {
          inStream.close
        } catch {
          case _: Exception =>
        }
      }
    }
    val imageView: ImageView = new ImageView {
      image = pictureImage
    }

    val removeButton: Button = new Button {
      text = "删"
      onAction = new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          pictureList -= current
        }
      }
    }
    val boxContent: VBox = new VBox {
      style = "-fx-alignment: center;"
      children = new HBox {
        style <== when (isSelected) choose
          """-fx-border-color: grey; -fx-border-width: 5; -fx-border-style: dashed;""" otherwise
          """-fx-border-color: white; -fx-border-width: 5; -fx-border-style: dashed;"""
        children = imageView
        onMouseClicked = new EventHandler[MouseEvent] {
          override def handle(event: MouseEvent): Unit = {
            scala.collection.JavaConversions.iterableAsScalaIterable(pictureList).toList.foreach { s =>
              if (s == current) {
                s.isSelected.set(true)
              } else if (s.isSelected.value == true) {
                s.isSelected.set(false)
              }
            }
          }
        }
      } :: removeButton :: Nil
    }
  }

  val field = VarModel.empty[scalafx.scene.control.TextField]
  val stageS = VarModel.empty[JFXApp.PrimaryStage]
  val sceneS = VarModel.empty[Scene]
  val parentBox = VarModel.empty[VBox]
  val pictureContent = VarModel.empty[HBox]
  val inputContent = VarModel.empty[VBox]

  def refreshWidth = {
    val autalWidth: Double = pictureList.map(_.pictureImage.getWidth + 10d).reduceOption(_ + _).getOrElse(0d) + 16
    val setWidth = Math.max(autalWidth, 300)
    stageS.get.minWidth = setWidth
    stageS.get.maxWidth = setWidth
  }

  val pictureList = ObservableBuffer.apply(ListBuffer.empty[SelectPicture])
  pictureList.onChange { (s: ObservableBuffer[SelectPicture], t: Seq[ObservableBuffer.Change]) =>
    t.foreach {
      case ObservableBuffer.Add(position, addList: Traversable[SelectPicture] @unchecked) =>
        pictureContent.get.children.addAll(addList.toList.map(_.boxContent: javafx.scene.layout.VBox): _*)
        refreshWidth
      case ObservableBuffer.Remove(position, removeList: Traversable[SelectPicture] @unchecked) =>
        pictureContent.get.children.removeAll(removeList.toList.map(_.boxContent: javafx.scene.layout.VBox): _*)
        refreshWidth
      case _ =>
    }
  }

  stage = stageS setTo new JFXApp.PrimaryStage {
    title.value = "装逼神器 0.0.3"
    height = 600
    width = 600
    focused.onChange { (_, _, newValue) =>
      if (! newValue) {
        writeClipboard
      } else {
        pictureList --= pictureList.filterNot(_.file.exists)
      }
    }

    scene = sceneS setTo new Scene {
      content = parentBox setTo new VBox {
        fillWidth = true
        children = List(
          pictureContent setTo new HBox {
            onDragOver = new EventHandler[DragEvent] {
              override def handle(event: DragEvent) {
                event.acceptTransferModes(TransferMode.MOVE)
                event.consume()
              }
            }
            onDragDropped = new EventHandler[DragEvent] {
              def handle(event: DragEvent) {
                val db = event.getDragboard()
                var success = false
                val fileList = scala.collection.JavaConversions.iterableAsScalaIterable(db.getFiles).toList
                if (! fileList.isEmpty) {
                  success = true

                  val modelsToAdd = fileList.map(SelectPicture(_)).filter { s =>
                    (! s.pictureImage.isError) &&
                      pictureList.toList.forall { t =>
                        s.file.getAbsolutePath != t.file.getAbsolutePath
                      }
                  }
                  pictureList ++= modelsToAdd

                }
                event.setDropCompleted(success)
                event.consume()
              }
            }
            children = Nil
          },
          inputContent setTo new VBox {
            style = "-fx-background-color: #336699; -fx-alignment: center; -fx-fill-width: false;"
            children = field setTo new scalafx.scene.control.TextField {
              style = "-fx-alignment: center;"
              prefWidth = 300
              text = "开始装逼"
              onMouseClicked = new EventHandler[MouseEvent] {
                override def handle(event: MouseEvent): Unit = {
                  text = ""
                }
              }
            }
          }
        )
      }
    }
  }

  pictureContent.get.prefHeight <== parentBox.get.height * 0.7
  inputContent.get.prefHeight <== parentBox.get.height * 0.3
  parentBox.get.prefHeight <== sceneS.get.height
  parentBox.get.prefWidth <== sceneS.get.width

}