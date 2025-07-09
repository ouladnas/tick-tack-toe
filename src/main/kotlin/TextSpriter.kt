import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

data class TextSlice(val x: Int, val y: Int, val width: Int, val height: Int)

enum class TextAlignment {
  LEFT,
  CENTER,
  RIGHT
}

object TextSpriter {
  private var textTexture: Texture? = null

  fun init() {
    textTexture = SpriteRenderer.texture("text.png")
  }

  fun drawSlice(id: String, x: Int, y: Int, width: Int, height: Int) {
    val texture = textTexture!!
    val slice = slices[id]!!

    val sx = slice.x
    val sy = slice.y
    val sw = slice.width
    val sh = slice.height

    SpriteRenderer.sprite(texture, x, y, width, height, sx, sy, sw, sh)
  }

  fun drawSlice(id: String, x: Int, y: Int) {
    val texture = textTexture!!
    val slice = slices[id]!!

    val sx = slice.x
    val sy = slice.y
    val sw = slice.width
    val sh = slice.height

    SpriteRenderer.sprite(texture, x, y, sw, sh, sx, sy, sw, sh)
  }

  fun drawScore(score: Int, x: Int, y: Int, height: Int, alignment: TextAlignment) {
    val text = score.toString()
    val offset = 0
  }

  private val slices = ResourceUtil.loadResourceJson("text_sprites.json").run {
    if (this !is JsonObject) throw IllegalStateException("Failed to load text sprite data")

    val map = mutableMapOf<String, TextSlice>()

    for ((key, value) in this) {
      if (value !is JsonArray) throw IllegalStateException("Failed to load text sprite data")

      val x = value[0].jsonPrimitive.intOrNull ?: 0
      val y = value[1].jsonPrimitive.intOrNull ?: 0
      val width = value[2].jsonPrimitive.intOrNull ?: 0
      val height = value[3].jsonPrimitive.intOrNull ?: 0

      map[key] = TextSlice(x, y, width, height)
    }

    map
  }
}