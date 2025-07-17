package scenes

import SpriteRenderer
import kotlin.math.min

class EndGameScene(val scene: GameScene, val outcome: String) : Scene() {
  private var okClicked = false
  private var quitClicked = false

  private val okTexture = SpriteRenderer.texture("ok.png")
  private val quitTexture = SpriteRenderer.texture("quit.png")
  private val drawTexture = SpriteRenderer.texture("draw.png")
  private val victoryTexture = SpriteRenderer.texture("won.png")
  private val crossTexture = SpriteRenderer.texture("cross.png")
  private val circleTexture = SpriteRenderer.texture("circle.png")
  private val backdropTexture = SpriteRenderer.texture("backdrop.png")
  private var okTextureRect = Rect(0, 0, 0, 0)
  private var quitTextureRect = Rect(0, 0, 0, 0)

  private var initialized = false
  private var stopped = false

  override fun resume() {
    stopped = false

    if (initialized) return
    initialized = true

    okClicked = false
    quitClicked = false

    scene.window.onMouseDown { x, y, button ->
      if (button != MouseButton.LEFT) return@onMouseDown
      if (stopped) return@onMouseDown

      if (isInRect(x, y, okTextureRect.x, okTextureRect.y, okTextureRect.width, okTextureRect.height)) {
        okClicked = true
      }

      if (isInRect(x, y, quitTextureRect.x, quitTextureRect.y, quitTextureRect.width, quitTextureRect.height)) {
        quitClicked = true
      }
    }
  }

  override fun stop() {
    stopped = true
  }

  override fun render() {
    scene.render()

    val winnerTexture = when (scene.game.getTurn()) {
      Tile.CROSS -> crossTexture
      Tile.CIRCLE -> circleTexture
      else -> null
    }

    val textTexture = when (outcome) {
      "draw" -> drawTexture
      "won" -> victoryTexture
      else -> null
    }

    val window = scene.window

    SpriteRenderer.sprite(backdropTexture, 0, 0, window.innerWidth, window.innerHeight)

    winnerTexture?.run {
      val unit = min(window.innerWidth, window.innerHeight) / 5
      val size = 3 * unit
      SpriteRenderer.sprite(this, (window.innerWidth - size) / 2, (window.innerHeight - size) / 2, size, size)
    }

    textTexture?.run {
      val unit = window.innerWidth / 5
      val w = 3 * unit
      val ratio = width / height.toFloat()
      val h = (w / ratio).toInt()
      SpriteRenderer.sprite(this, (window.innerWidth - w) / 2, (window.innerHeight - h) / 2, w)
    }

    val centerX = window.innerWidth / 2
    val offset = 30

    okTexture.run {
      val h = 100
      val ratio = width / height.toFloat()
      val w = (h / ratio).toInt()
      SpriteRenderer.sprite(this, centerX + offset, window.innerHeight - h - offset, w, h)
      okTextureRect = Rect(centerX + offset, window.innerHeight - h - offset, w, h)
    }

    quitTexture.run {
      val h = 100
      val ratio = width / height.toFloat()
      val w = (h / ratio).toInt()
      SpriteRenderer.sprite(this, centerX - offset - w, window.innerHeight - h - offset, w, h)
      quitTextureRect = Rect(centerX - offset - w, window.innerHeight - h - offset, w, h)
    }
  }

  override fun update(): Scene {
    when {
      okClicked -> {
        return scene
      }
      quitClicked -> {
        scene.window.dismiss()
        return scene
      }
      else -> return this
    }
  }
}

data class Rect(val x: Int, val y: Int, val width: Int, val height: Int)