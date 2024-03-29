package org.xarcher.emiya

import java.io.{ File, FileInputStream, InputStream }

import scala.language.postfixOps
import scala.util.Try
import scalafx.scene.input.{ DragEvent, TransferMode }
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.beans.property.BooleanProperty
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.image.{ Image, ImageView }
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout._

object Emiya extends JFXApp3 {

  object VarModel {

    trait AbsVar[T] {
      var model: T = _
      def setTo(model1: T): T = {
        model = model1
        model
      }
      def get: T = model
    }

    def empty[T] = new AbsVar[T] {}

  }

  def writeClipboard: Unit =
    pictureList.find(_.isSelected.value).foreach {
      s =>
        CopyPic.pic(s.file)(field.get.text.value)
    }

  case class SelectPicture(file: File) {
    val current = this

    val isSelected = BooleanProperty(false)

    val pictureImage: Image = {
      var inStream: InputStream = null
      try {
        inStream = new FileInputStream(file)
        new Image(inStream)
      } finally {
        Try {
          inStream.close
        }
      }
    }
    val imageView: ImageView = new ImageView {
      image = pictureImage
      fitWidth <== min(pictureImage.width, 400)
      fitHeight <== when(pictureImage.width > 400) choose (this.fitWidth * pictureImage.height.toDouble / pictureImage.width.toDouble) otherwise (pictureImage.height)
    }

    val removeButton: Button = new Button {
      text = "删"
      handleEvent(ActionEvent.Action) {
        (e: ActionEvent) =>
          pictureList -= current
          ()
      }
    }
    val boxContent: VBox = new VBox {
      style = "-fx-alignment: center;"
      children = new HBox {
        style <== when(isSelected) choose
          """-fx-border-color: grey; -fx-border-width: 5; -fx-border-style: dashed;""" otherwise
          """-fx-border-color: white; -fx-border-width: 5; -fx-border-style: dashed;"""
        children = imageView
        handleEvent(MouseEvent.MouseClicked) {
          e: MouseEvent =>
            scala.collection.JavaConverters.iterableAsScalaIterable(pictureList).toList.foreach { s =>
              if (s == current) {
                s.isSelected.set(true)
              } else if (s.isSelected.value == true) {
                s.isSelected.set(false)
              }
            }
        }
      } :: removeButton :: Nil
    }
  }

  val field = VarModel.empty[scalafx.scene.control.TextField]
  val stageS = VarModel.empty[JFXApp3.PrimaryStage]
  val sceneS = VarModel.empty[Scene]
  val parentBox = VarModel.empty[VBox]
  val pictureContent = VarModel.empty[HBox]
  val inputContent = VarModel.empty[VBox]

  def refreshWidth = {
    val autalWidth: Double = pictureList.map(_.imageView.fitWidth.value + 10d).reduceOption(_ + _).getOrElse(0d) + 16
    val setWidth = Math.max(autalWidth, 300)
    stageS.get.minWidth = setWidth
    stageS.get.maxWidth = setWidth
  }

  val pictureList = ObservableBuffer.apply(List.empty[SelectPicture]: _*)
  pictureList.onChange { (s: ObservableBuffer[SelectPicture], t: Seq[ObservableBuffer.Change[SelectPicture]]) =>
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

  override def start(): Unit = {
    stage = stageS setTo new JFXApp3.PrimaryStage {
      title.value = "装逼神器 0.0.3"
      height = 600
      width = 600
      //minWidth <== pictureList.map(_.imageView.fitWidth.value + 10d).reduceOption(_ + _).getOrElse(0d) + 16
      focused.onChange { (_, _, newValue) =>
        if (!newValue)
          writeClipboard
        else
          pictureList --= pictureList.filterNot(_.file.exists)
      }

      scene = sceneS setTo new Scene {
        content = parentBox setTo new VBox {
          fillWidth = true
          children = List(
            pictureContent setTo new HBox {
              handleEvent(DragEvent.DragOver) {
                e: DragEvent =>
                  e.acceptTransferModes(TransferMode.Move)
                  e.consume()
              }
              handleEvent(DragEvent.DragDropped) {
                e: DragEvent =>
                  val db = e.dragboard
                  var success = false
                  val fileList = db.files
                  if (!fileList.isEmpty) {
                    success = true
                    val modelsToAdd = fileList.map(SelectPicture(_)).filter { s =>
                      (!s.pictureImage.isError) &&
                        pictureList.toList.forall { t =>
                          s.file.getAbsolutePath != t.file.getAbsolutePath
                        }
                    }
                    pictureList ++= modelsToAdd
                  }
                  e dropCompleted = success
                  e.consume
              }
              children = Nil
            },

            inputContent setTo new VBox {
              style = "-fx-background-color: #336699; -fx-alignment: center; -fx-fill-width: false;"
              children = field setTo new scalafx.scene.control.TextField {
                style = "-fx-alignment: center;"
                prefWidth = 300
                text = "开始装逼"
                handleEvent(MouseEvent.MouseClicked) {
                  e: MouseEvent =>
                    text = ""
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

}