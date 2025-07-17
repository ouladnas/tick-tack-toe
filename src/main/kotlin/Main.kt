import scenes.GameScene
import scenes.Scene

fun main() {
 WindowManager.init().use {
   WindowManager.window(640, 480, "Tick-Tack-Toe").use { window ->

     SpriteRenderer.init()

     val game = Game()

     var scene: Scene = GameScene(window, game)

     scene.resume()

     SpriteRenderer.onFrameResize(window.innerWidth, window.innerHeight)
     window.onBufferResize { width, height ->
       SpriteRenderer.onFrameResize(width, height)
     }

     while (!window.dismissing) {
       SpriteRenderer.onBeginFrame()

       val next = scene.update()

       if (next != scene) {
         scene.stop()
         scene = next
         scene.resume()
       }

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
