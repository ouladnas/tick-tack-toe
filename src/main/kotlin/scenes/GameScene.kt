package scenes

import Board
import TextSpriter
import Window
import kotlin.math.min

class GameScene(val window: Window) : Scene() {
  private val boardTexture = SpriteRenderer.texture("board.png")
  private val crossTexture = SpriteRenderer.texture("cross.png")
  private val circleTexture = SpriteRenderer.texture("circle.png")
  private val textTexture = SpriteRenderer.texture("text.png")

  fun renderBoard() {
    val scoreTextHeight = 100
    val scoreBoxHeight = scoreTextHeight * 2

//    TextSpriter.drawSlice("dash", 0, 0)

    val boardSize = min(window.innerWidth, window.innerHeight) - scoreBoxHeight
    val boardLeft = (window.innerWidth - boardSize) / 2
    val boardTop = scoreBoxHeight

    SpriteRenderer.sprite(boardTexture, boardLeft, boardTop, boardSize, boardSize)
  }

  override fun render() {
    renderBoard()
  }

  override fun update() {
  }

  override fun init() {
  }
}