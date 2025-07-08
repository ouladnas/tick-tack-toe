import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryUtil.NULL

fun main() {
  if (!glfwInit()) {
    throw IllegalStateException("Unable to initialize GLFW")
  }

  val window = glfwCreateWindow(640, 480, "Hello World!", NULL, NULL)

  if (window == NULL) {
    throw IllegalStateException("Failed to create the GLFW window")


}