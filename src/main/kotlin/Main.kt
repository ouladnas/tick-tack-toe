import scenes.GameScene

fun main() {
 WindowManager.init().use {
   WindowManager.window(640, 480, "Tick-Tack-Toe").use { window ->

     SpriteRenderer.init()

     val board = Board()
     board.set(0, 0, Tile.CROSS)
     board.set(1, 2, Tile.CIRCLE)
     val scene = GameScene(window, board)

     scene.init()

     SpriteRenderer.onFrameResize(window.innerWidth, window.innerHeight)
     window.onBufferResize() { width, height ->
       SpriteRenderer.onFrameResize(width, height)
     }

     while (!window.dismissing) {
       SpriteRenderer.onBeginFrame()

       scene.update()
       scene.render()

       SpriteRenderer.onEndFrame()
       window.flip()

//       window.dismiss()
//       SpriteRenderer.dump();
     }

     SpriteRenderer.free()
   }
 }
}
