package scenes

import Board
import SpriteRenderer
//import TextSpriter
import Window
import kotlin.math.min

class GameScene(val window: Window, val board: Board) : Scene() {
  private val boardTexture = SpriteRenderer.texture("board.png")
  private val crossTexture = SpriteRenderer.texture("cross.png")
  private val circleTexture = SpriteRenderer.texture("circle.png")
  private val textTexture = SpriteRenderer.texture("text.png")

  fun renderBoard() {
    val scoreTextHeight = 50
    val scoreBoxHeight = scoreTextHeight * 2
    val boardSize = min(window.innerWidth, window.innerHeight) - scoreBoxHeight
    val boardLeft = (window.innerWidth - boardSize) / 2
    val boardTop = scoreBoxHeight
    val boardLineSize = ((64f / 1000f) * boardSize).toInt()
    val boardSquareSize = (boardSize - boardLineSize * 2) / 3

//    TextSpriter.drawSlice("dash", 0, 0)


    SpriteRenderer.sprite(boardTexture, boardLeft, boardTop, boardSize, boardSize)

    board.toSquareList().forEach { (x, y, tile) ->
      val left = boardLeft + x * (boardSquareSize + boardLineSize)
      val top = boardTop + y * (boardSquareSize + boardLineSize)
      SpriteRenderer.sprite(
        when (tile) {
          Tile.CROSS -> crossTexture
          Tile.CIRCLE -> circleTexture
          else -> return@forEach
        }, left, top, boardSquareSize, boardSquareSize
      )
    }
  }

  override fun render() {
    renderBoard()
  }

  override fun update() {
  }

  override fun init() {
  }
}