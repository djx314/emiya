package org.xarcher.emiya

import java.io.FileInputStream
import javafx.event.EventHandler
import javafx.scene.input.{DragEvent, MouseEvent, TransferMode}

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout._
import scalafx.scene.paint.Color

object HelloStageDemo extends JFXApp {

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

  val field = VarModel.empty[scalafx.scene.control.TextField]
  val stageS = VarModel.empty[JFXApp.PrimaryStage]
  val sceneS = VarModel.empty[Scene]
  val parentBox = VarModel.empty[VBox]
  val pictureContent = VarModel.empty[HBox]
  val inputContent = VarModel.empty[VBox]

  stage = stageS setTo new JFXApp.PrimaryStage {
    title.value = "装逼神器 0.0.1"
    width = 300
    height = 600

    scene = sceneS setTo new Scene {
      content = parentBox setTo new VBox {
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
                  val imageList = fileList.map { s =>
                    import ImageView._
                    new ImageView {
                      image = new Image(new FileInputStream(s))
                    }: javafx.scene.image.ImageView
                  }
                  event.getTarget.asInstanceOf[javafx.scene.layout.HBox].children.addAll(imageList: _*)
                }
                event.setDropCompleted(success)
                event.consume()
              }
            }
            children = Nil
          },
          inputContent setTo new VBox {
            style = "-fx-background-color: #336699;"
            children = field setTo new scalafx.scene.control.TextField {
              alignment = Pos.Center
              text = "22"
              text.onChange((_, _, newText) => {
                CopyPic.pic(newText)
              })
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
  field.get.translateY <== inputContent.get.height / 2 - field.get.height / 2
  parentBox.get.prefHeight <== sceneS.get.height
  parentBox.get.prefWidth <== sceneS.get.width

}