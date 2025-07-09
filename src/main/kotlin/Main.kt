import kotlin.math.min

fun main() {
 WindowManager.init().use {
   WindowManager.window(640, 480, "Tick-Tack-Toe").use { window ->

     SpriteRenderer.init()

     val logo = SpriteRenderer.texture("board.png")

     SpriteRenderer.onFrameResize(window.innerWidth, window.innerHeight)
     window.onBufferResize() { width, height ->
       SpriteRenderer.onFrameResize(width, height)
     }

     while (!window.dismissing) {
       SpriteRenderer.onBeginFrame()

       val width = min(window.innerWidth, window.innerHeight)
       val x = (window.innerWidth - width) / 2
       val y = (window.innerHeight - width) / 2

       SpriteRenderer.sprite(logo, x, y, width, width)

       SpriteRenderer.onEndFrame()
       window.flip()

//       window.dismiss()
//       SpriteRenderer.dump();
     }

     SpriteRenderer.free()
   }
 }
}
