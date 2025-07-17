package scenes

import Game
import GameState
import MouseButton
import SpriteRenderer
import Tile
import Window
import kotlin.math.min

class GameScene(val window: Window, public val game: Game) : Scene() {
  private val boardTexture = SpriteRenderer.texture("board.png")
  private val crossTexture = SpriteRenderer.texture("cross.png")
  private val circleTexture = SpriteRenderer.texture("circle.png")
  private val textTexture = SpriteRenderer.texture("text.png")

  fun renderBoard() {
    val scoreTextHeight = 0
    val scoreBoxHeight = scoreTextHeight * 2
    val boardSize = min(window.innerWidth, window.innerHeight) - scoreBoxHeight
    val boardLeft = (window.innerWidth - boardSize) / 2
    val boardTop = scoreBoxHeight
    val boardLineSize = ((64f / 1000f) * boardSize).toInt()
    val boardSquareSize = (boardSize - boardLineSize * 2) / 3


    SpriteRenderer.sprite(boardTexture, boardLeft, boardTop, boardSize, boardSize)

    val board = game.board

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

  override fun update(): Scene {
    when (game.getState()) {
      GameState.DRAW -> return EndGameScene(this, "draw")
      GameState.WON -> return EndGameScene(this, "victory")
      else -> return this
    }
  }

  private var initialized = false
  private var stopped = false

  override fun stop() {
    stopped = true
  }

  override fun resume() {
    stopped = false

    game.start()

    if (initialized) return
    initialized = true

    window.onMouseDown { mx, my, button ->
      if (button != MouseButton.LEFT) return@onMouseDown
      if (stopped) return@onMouseDown

      val scoreTextHeight = 0
      val scoreBoxHeight = scoreTextHeight * 2
      val boardSize = min(window.innerWidth, window.innerHeight) - scoreBoxHeight
      val boardLeft = (window.innerWidth - boardSize) / 2
      val boardTop = scoreBoxHeight
      val boardLineSize = ((64f / 1000f) * boardSize).toInt()
      val boardSquareSize = (boardSize - boardLineSize * 2) / 3

      val board = game.board

      board.toSquareList().forEach { (x, y, tile) ->
        if (tile != Tile.EMPTY) return@forEach

        val left = boardLeft + x * (boardSquareSize + boardLineSize)
        val top = boardTop + y * (boardSquareSize + boardLineSize)

        if (isInRect(mx, my, left, top, boardSquareSize, boardSquareSize)) {
          game.play(x, y)
          return@forEach

        }
      }
    }
  }

}

fun isInRect(x: Int, y: Int, left: Int, top: Int, width: Int, height: Int): Boolean {
  return x >= left && x < left + width && y >= top && y < top + height
}