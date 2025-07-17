import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.Callbacks.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL41C.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL
import kotlin.math.round

data class Window(val handle: Long): AutoCloseable {
  override fun close() {
    glfwDestroyWindow(handle)
    glfwFreeCallbacks(handle)
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

  fun onMouseDown(callback: Window.(Int, Int, MouseButton) -> Unit) {
    glfwSetMouseButtonCallback(handle) { _, btn, action, _ ->
      val button = getMouseButton(btn) ?: return@glfwSetMouseButtonCallback
      val isPressed = action == GLFW_PRESS

      if (isPressed) callback(this, mx, my, button)
    }
  }

  fun onMouseUp(callback: Window.(Int, Int, MouseButton) -> Unit) {
    glfwSetMouseButtonCallback(handle) { _, btn, action, _ ->
      val button = getMouseButton(btn) ?: return@glfwSetMouseButtonCallback
      val isPressed = action == GLFW_RELEASE

      if (isPressed) callback(this, mx, my, button)
    }
  }

  fun onMouseMove(callback: Window.(Int, Int) -> Unit) {
    glfwSetCursorPosCallback(handle) { _, x, y ->
      mx = round(x).toInt()
      my = round(y).toInt()
      callback(this, mx, my)
    }
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

enum class MouseButton {
  LEFT, RIGHT, MIDDLE
}

object WindowManager : AutoCloseable {
  fun init(): WindowManager {
    if (!glfwInit())
      throw IllegalStateException("GLFW initialization failed")

    GLFWErrorCallback.createPrint(System.err).set()

    return this
  }

  fun window(width: Int, height: Int, title: String): Window {
    glfwDefaultWindowHints()

    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
    glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE)

    val handle = glfwCreateWindow(width, height, title, NULL, NULL)
    if (handle == NULL)
      throw IllegalStateException("GLFW window creation failed")

    val window = Window(handle)

    window.useContext()
    GL.createCapabilities()

    println("GLFW version: ${glfwGetVersionString()}")
    println("OpenGL version: ${glGetString(GL_VERSION)}")
    println("OpenGL renderer: ${glGetString(GL_RENDERER)}")
    println("OpenGL vendor: ${glGetString(GL_VENDOR)}")
    println("OpenGL GLSL version: ${glGetString(GL_SHADING_LANGUAGE_VERSION)}")
    println("Window ${window.width}x${window.height} created")

    return window
  }

  override fun close() {
    glfwTerminate()
    glfwSetErrorCallback(null)?.free()
  }
}