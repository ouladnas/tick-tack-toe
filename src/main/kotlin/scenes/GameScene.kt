package scenes

import Game
import SpriteRenderer
import Window
import kotlin.math.min

class DrawScene(val boardScene: GameScene) : Scene() {
  override fun init() {}

  override fun update(): Scene {
    return this
  }

  override fun render() {
    boardScene.render()
    TODO("Render draw screen")
  }
}

class WonScene(val boardScene: GameScene) : Scene() {
  override fun init() {}

  override fun update(): Scene {
    return this
  }

  override fun render() {
    boardScene.render()
    TODO("Render victory screen")
  }
}

class GameScene(val window: Window, val game: Game) : Scene() {
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
      GameState.DRAW -> return DrawScene(this)
      GameState.WON -> return WonScene(this)
      else -> return this
    }
  }

  override fun init() {
    game.start()

    window.onMouseDown { x, y, button ->
      if (button != MouseButton.LEFT) return@onMouseDown

      val scoreTextHeight = 50
      val scoreBoxHeight = scoreTextHeight * 2
      val boardSize = min(window.innerWidth, window.innerHeight) - scoreBoxHeight
      val boardLeft = (window.innerWidth - boardSize) / 2
      val boardTop = scoreBoxHeight
      val boardLineSize = ((64f / 1000f) * boardSize).toInt()
      val boardSquareSize = (boardSize - boardLineSize * 2) / 3

      val board = game.board

      board.toSquareList().forEach { (x, y, tile) ->
        val left = boardLeft + x * (boardSquareSize + boardLineSize)
        val top = boardTop + y * (boardSquareSize + boardLineSize)

        if (isInRect(x, y, left, top, boardSquareSize, boardSquareSize)) {
          game.play(x, y, tile)
          return@forEach

        }
      }
    }
  }

  private fun isInRect(x: Int, y: Int, left: Int, top: Int, width: Int, height: Int): Boolean {
    return x >= left && x < left + width && y >= top && y < top + height
  }
}