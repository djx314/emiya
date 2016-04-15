package org.xarcher.emiya

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.layout._

object HelloStageDemo extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title.value = "装逼神器"
    width = 300
    height = 100

    val field = new scalafx.scene.control.TextField {
      alignment = Pos.Center
      text.onChange((_, _, newText) => {
        CopyPic.pic(newText)
      })
      onMouseClicked = new EventHandler[MouseEvent] {
        override def handle(event: MouseEvent): Unit = {
          text = ""
        }
      }
    }

    scene = new Scene {
      content = new VBox {
        children = field
      }
    }

    field.translateX <== scene.width / 2 - field.width / 2
    field.translateY <== scene.height / 2 - field.height / 2
  }


}