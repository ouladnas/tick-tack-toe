import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL

fun main() {
 WindowManager.init().use {
   WindowManager.window(640, 480, "Hello LWJGL").use { window ->

     SpriteRenderer.init()

     val logo = SpriteRenderer.texture("cross.png")

//     println("Logo: ${logo.width}x${logo.height} ${logo.id}")

     while (!window.dismissing) {
       SpriteRenderer.onBeginFrame()

       SpriteRenderer.sprite(logo, 100, 100, 256)

       SpriteRenderer.onEndFrame()
       window.flip()
     }

     SpriteRenderer.free()
   }
 }
}
