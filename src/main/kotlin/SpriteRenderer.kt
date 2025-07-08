import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL41.*
import org.lwjgl.system.MemoryUtil.NULL
import org.joml.Matrix3x2f
import org.joml.Matrix4f
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer

data class Sprite(val x: Int, val y: Int, val width: Int, val height: Int) {
  val transform = Matrix3x2f().identity()
}

/**
 * This is a class wrapping LWJGL's GLFW and OpenGL code.
 * It is used to render the game's graphics using sprites.
 * It does not support text or shaders or multiple windows or any fancy shit.
 */
object SpriteRenderer {
  private val window: Long
  private val sprites = emptyList<Sprite>()
  private val vao: Int // Vertex Array Object
  private val vbo: Int // Vertex Buffer Object
  private val ebo: Int // Index Buffer Object
  private val sbo: Int // Sprite Instanced Buffer Object
  private val mbo: Int // Matrix Instanced Buffer Object
  private val projection = Matrix4f()
  private val view = Matrix4f()
  private val program: Int

  init {
    if (!glfwInit()) {
      throw IllegalStateException("Unable to initialize GLFW")
    }

    window = glfwCreateWindow(640, 480, "Hello World!", NULL, NULL)

    if (window == NULL)
      throw IllegalStateException("Failed to create the GLFW window")
    
    glfwMakeContextCurrent(window)
    GL.createCapabilities()

    glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

    vao = glGenVertexArrays()
    vbo = glGenBuffers()
    ebo = glGenBuffers()
    sbo = glGenBuffers()
    mbo = glGenBuffers()

    program = glCreateProgram()
    val vs = createShader(GL_VERTEX_SHADER, "shaders/vertex.glsl")
    val fs = createShader(GL_FRAGMENT_SHADER, "shaders/fragment.glsl")
    glAttachShader(program, vs)
    glAttachShader(program, fs)
    glLinkProgram(program)

    if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
      throw IllegalStateException("Program link error: ${glGetProgramInfoLog(program)}")
    }

    glDeleteShader(vs)
    glDeleteShader(fs)

    glBindVertexArray(vao)
    glBindBuffer(GL_ARRAY_BUFFER, vbo)

    val vertices = floatArrayOf(
      0.0f, 0.0f,
      1.0f, 0.0f,
      1.0f, 1.0f,
      0.0f, 1.0f
    )

    glBufferData(GL_ARRAY_BUFFER, vertices.toFloatBuffer(), GL_STATIC_DRAW)

    glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0)
    glEnableVertexAttribArray(0)
    glVertexAttribDivisor(0, 0)

    glBindBuffer(GL_ARRAY_BUFFER, sbo)

    glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0)
    glEnableVertexAttribArray(1)
    glVertexAttribDivisor(1, 1)

    glBindBuffer(GL_ARRAY_BUFFER, mbo)

    val unit = 2
    val columns = 3
    val location = 2
    val size = Float.SIZE_BYTES * unit
    for (i in 0..2) {
      glVertexAttribPointer(i + location, unit, GL_FLOAT, false, (size * columns), (i * size).toLong())
      glEnableVertexAttribArray(i + location)
      glVertexAttribDivisor(i + location, 1)
    }

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)

    val indices = intArrayOf(
      0, 1, 2,
      2, 3, 0
    )

    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.toIntBuffer(), GL_STATIC_DRAW)
  }

  private fun readResourceFile(path: String): String {
    return SpriteRenderer::class.java.getResource(path)?.readText() ?: ""
  }

  private fun createShader(type: Int, path: String): Int {
    val shader = glCreateShader(type)
    glShaderSource(shader, readResourceFile(path))
    glCompileShader(shader)
    if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
      throw IllegalStateException("Shader compilation error: ${glGetShaderInfoLog(shader)}")
    }
    return shader
  }

  fun createSprite(x: Int, y: Int, width: Int, height: Int): Sprite {
    return Sprite(x, y, width, height)
  }

  fun render() {
    glClear(GL_COLOR_BUFFER_BIT)
    glfwSwapBuffers(window)
  }

  val done: Boolean
    get() = glfwWindowShouldClose(window)

  fun close() {
    glfwFreeCallbacks(window)
    glfwDestroyWindow(window)
    glfwTerminate()
    glfwSetErrorCallback(null)?.free()
  }
}

fun FloatArray.toFloatBuffer(): FloatBuffer {
  val buffer = MemoryUtil.memAllocFloat(this.size)
  buffer.put(this)
  buffer.flip()
  return buffer
}

fun IntArray.toIntBuffer(): IntBuffer {
  val buffer = MemoryUtil.memAllocInt(this.size)
  buffer.put(this)
  buffer.flip()
  return buffer
}