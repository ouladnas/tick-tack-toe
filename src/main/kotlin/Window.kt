import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWCursorPosCallback
import org.lwjgl.glfw.GLFWMouseButtonCallback
import org.lwjgl.system.MemoryStack
import kotlin.math.round

data class Window(val handle: Long): AutoCloseable {
  override fun close() {
    glfwDestroyWindow(handle)
    Callbacks.glfwFreeCallbacks(handle)
  }

  val monitor get() = glfwGetWindowMonitor(handle)
  val dpi get() = MemoryStack.stackPush().use {
    val x = it.mallocFloat(1)
    val y = it.mallocFloat(1)
    glfwGetWindowContentScale(handle, x, y)
    Pair(x.get(0), y.get(0))
  }

  fun dismiss() = glfwSetWindowShouldClose(handle, true)
  val dismissing: Boolean get() = glfwWindowShouldClose(handle)

  fun show() = glfwShowWindow(handle)
  fun hide() = glfwHideWindow(handle)
  fun flip() {
    glfwSwapBuffers(handle)
    glfwPollEvents()
  }

  val visible: Boolean get() = glfwGetWindowAttrib(handle, GLFW_VISIBLE) == GLFW_TRUE
  val size: Pair<Int, Int> get() = MemoryStack.stackPush().use {
    val width = it.mallocInt(1)
    val height = it.mallocInt(1)
    glfwGetWindowSize(handle, width, height)
    Pair(width.get(0), height.get(0))
  }
  val position: Pair<Int, Int> get() = MemoryStack.stackPush().use {
    val x = it.mallocInt(1)
    val y = it.mallocInt(1)
    glfwGetWindowPos(handle, x, y)
    Pair(x.get(0), y.get(0))
  }

  val innerSize: Pair<Int, Int> get() = MemoryStack.stackPush().use {
    val width = it.mallocInt(1)
    val height = it.mallocInt(1)
    glfwGetFramebufferSize(handle, width, height)
    Pair(width.get(0), height.get(0))
  }

  val width: Int get() = size.first
  val height: Int get() = size.second

  val innerWidth: Int get() = innerSize.first
  val innerHeight: Int get() = innerSize.second

  val x: Int get() = position.first
  val y: Int get() = position.second

  fun move(x: Int, y: Int) = glfwSetWindowPos(handle, x, y)
  fun resize(width: Int, height: Int) = glfwSetWindowSize(handle, width, height)

  fun useContext() = glfwMakeContextCurrent(handle)

  fun onBufferResize(callback: Window.(Int, Int) -> Unit) {
    glfwSetFramebufferSizeCallback(handle) { _, width, height ->
      callback(this, width, height)
    }
  }

  fun onResize(callback: Window.(Int, Int) -> Unit) {
    glfwSetWindowSizeCallback(handle) { _, width, height ->
      callback(this, width, height)
    }
  }

  private var mx = 0
  private var my = 0

  fun onMouseDown(callback: Window.(Int, Int, MouseButton) -> Unit): GLFWMouseButtonCallback? {
    return glfwSetMouseButtonCallback(handle) { _, btn, action, _ ->
      val button = getMouseButton(btn) ?: return@glfwSetMouseButtonCallback
      val isPressed = action == GLFW_PRESS

      if (isPressed) callback(this, mx, my, button)
    }
  }

  fun onMouseUp(callback: Window.(Int, Int, MouseButton) -> Unit): GLFWMouseButtonCallback? {
    return glfwSetMouseButtonCallback(handle) { _, btn, action, _ ->
      val button = getMouseButton(btn) ?: return@glfwSetMouseButtonCallback
      val isPressed = action == GLFW_RELEASE

      if (isPressed) callback(this, mx, my, button)
    }
  }

  fun onMouseMove(callback: Window.(Int, Int) -> Unit): GLFWCursorPosCallback? {
    return glfwSetCursorPosCallback(handle) { _, x, y ->
      mx = round(x * dpi.first).toInt()
      my = round(y * dpi.second).toInt()
      callback(this, mx, my)
    }
  }

  init {
    onMouseMove { _, _ -> println("Mouse: ($mx, $my) $dpi") }
  }

  companion object {
    fun getMouseButton(button: Int) = when (button) {
      GLFW_MOUSE_BUTTON_LEFT -> MouseButton.LEFT
      GLFW_MOUSE_BUTTON_RIGHT -> MouseButton.RIGHT
      GLFW_MOUSE_BUTTON_MIDDLE -> MouseButton.MIDDLE
      else -> null
    }
  }
}