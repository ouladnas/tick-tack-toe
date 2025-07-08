import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryUtil.NULL

fun main() {
 while (!SpriteRenderer.done) {
   SpriteRenderer.render()
 }

 SpriteRenderer.close()
}