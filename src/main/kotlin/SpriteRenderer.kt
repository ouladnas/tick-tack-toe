import org.joml.Math
import org.lwjgl.opengl.GL41.*
import org.lwjgl.BufferUtils
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import java.net.URL
import java.nio.ByteBuffer
import java.nio.file.Paths

data class Texture(val id: Int, val width: Int, val height: Int)

object SpriteRenderer {
  private const val MAX_SPRITES = 2048
  private const val MAX_VERTICES = MAX_SPRITES * 4
  private const val MAX_INDICES = MAX_SPRITES * 6
  private const val FLOAT_PER_VERTEX = 5
  private const val MAX_TEXTURES = 8

  private var vao = 0
  private var vbo = 0
  private var ibo = 0

  private var shader = 0

  private var vertexValues = FloatArray(MAX_VERTICES * FLOAT_PER_VERTEX)
  private var vertexCount = 0

  private var indexValues = IntArray(MAX_INDICES)
  private var indexCount = 0

  private val textures = IntArray(MAX_TEXTURES)
  private val textureCache = mutableMapOf<String, Texture>()
  private var textureCount = 0

  private var width = 800
  private var height = 600

  fun onBeginFrame() {
    vertexCount = 0
    indexCount = 0

    glClear(GL_COLOR_BUFFER_BIT)
    glViewport(0, 0, width, height)

    glDisable(GL_CULL_FACE)
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
  }

  fun onEndFrame() {
    glUseProgram(shader)

    for (i in 0 until textureCount) {
      glActiveTexture(GL_TEXTURE0 + i)
      glBindTexture(GL_TEXTURE_2D, textures[i])
    }

    glBindVertexArray(vao)

    val usedVertexCount = vertexCount * FLOAT_PER_VERTEX
    val vertexBuffer = BufferUtils.createFloatBuffer(usedVertexCount)
    vertexBuffer.put(vertexValues, 0, usedVertexCount)
    vertexBuffer.flip()

    glBindBuffer(GL_ARRAY_BUFFER, vbo)
    glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuffer)

    val indexBuffer = BufferUtils.createIntBuffer(indexCount)
    indexBuffer.put(indexValues, 0, indexCount)
    indexBuffer.flip()

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
    glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, indexBuffer)

    glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0L)
  }

  private fun verifyNoErrors() {
    val error = glGetError()
    val message = when (error) {
      GL_NO_ERROR -> return
      GL_INVALID_ENUM -> "Invalid enum"
      GL_INVALID_VALUE -> "Invalid value"
      GL_INVALID_OPERATION -> "Invalid operation"
      GL_INVALID_FRAMEBUFFER_OPERATION -> "Invalid framebuffer operation"
      GL_OUT_OF_MEMORY -> "Out of memory"
      GL_STACK_UNDERFLOW -> "Stack underflow"
      GL_STACK_OVERFLOW -> "Stack overflow"
      else -> "Unknown error"
    }
    println("OpenGL error: $message")
  }

  fun onFrameResize(width: Int, height: Int) {
    this.width = width
    this.height = height
    verifyNoErrors()
  }

  fun dump() {
    println("vertexCount: $vertexCount")
    println("indexCount: $indexCount")
    println("textureCount: $textureCount")

    println("vertexValues(${vertexValues.size}): ${vertexValues.slice(0 until vertexCount * FLOAT_PER_VERTEX).toList()}")
    println("indexValues(${indexValues.size}): ${indexValues.slice(0 until indexCount).toList()}")
    println("textures(${textures.size}): ${textures .toList()}")

    val usedVertexCount = vertexCount * FLOAT_PER_VERTEX
    val vertexBuffer = BufferUtils.createFloatBuffer(usedVertexCount)
    vertexBuffer.put(vertexValues, 0, usedVertexCount)
    vertexBuffer.flip()

    val vertexData = FloatArray(usedVertexCount)
    vertexBuffer.get(vertexData)

    val indexBuffer = BufferUtils.createIntBuffer(indexCount)
    indexBuffer.put(indexValues, 0, indexCount)
    indexBuffer.flip()

    val indexData = IntArray(indexCount)
    indexBuffer.get(indexData)

    println("vertexBuffer: ${vertexData.toList()}")
    println("indexBuffer: ${indexData.toList()}")
  }

  fun free() {
    glDeleteBuffers(vbo)
    glDeleteBuffers(ibo)
    glDeleteVertexArrays(vao)
    glDeleteProgram(shader)
  }

  fun init() {
    glClearColor(0f, 0f, 0f, 1f)

    vao = glGenVertexArrays()
    glBindVertexArray(vao)

    vbo = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, vbo)

    glBufferData(GL_ARRAY_BUFFER, (MAX_VERTICES * FLOAT_PER_VERTEX * Float.SIZE_BYTES).toLong(), GL_STATIC_DRAW)

    val stride = FLOAT_PER_VERTEX * Float.SIZE_BYTES
    val positionOffset = 0L
    val texCoordOffset = 2L * Float.SIZE_BYTES
    val texIndexOffset = 4L * Float.SIZE_BYTES

    glEnableVertexAttribArray(0)
    glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, positionOffset)

    glEnableVertexAttribArray(1)
    glVertexAttribPointer(1,
      2, GL_FLOAT, false, stride, texCoordOffset)

    glEnableVertexAttribArray(2)
    glVertexAttribPointer(2, 1, GL_FLOAT, false, stride, texIndexOffset)

    ibo = glGenBuffers()
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)

    glBufferData(GL_ELEMENT_ARRAY_BUFFER, (MAX_INDICES * Int.SIZE_BYTES).toLong(), GL_STATIC_DRAW)

    shader = glCreateProgram()

    val vert = createShaderModule(GL_VERTEX_SHADER, "shaders/sprite.vert")
    val frag = createShaderModule(GL_FRAGMENT_SHADER, "shaders/sprite.frag")

    linkShaderProgram(shader, vert, frag)

//    TextSpriter.init()
  }

  fun texture(path: String): Texture {
    if (textureCache.containsKey(path))
      return textureCache[path]!!

    if (textureCount == MAX_TEXTURES)
      throw IllegalStateException("Maximum number of textures reached")

    val (image, width, height) = loadTextureImage(path)
    val id = glGenTextures()

    glBindTexture(GL_TEXTURE_2D, id)
    glActiveTexture(GL_TEXTURE0 + textureCount)

    textures[textureCount++] = id

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER)

    glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, floatArrayOf(0f, 0f, 0f, 0f))
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image)

    stbi_image_free(image)

    val texture = Texture(id, width, height)

    textureCache[path] = texture

    return texture
  }

  fun sprite(texture: Texture, x: Int, y: Int) {
    sprite(texture, x, y, texture.width, texture.height)
  }

  fun sprite(texture: Texture, x: Int, y: Int, width: Int) {
    val ratio = texture.width / texture.height.toFloat()
    val height = (width / ratio).toInt()
    sprite(texture, x, y, width, height)
  }

  fun sprite(texture: Texture, x: Int, y: Int, width: Int, height: Int) {
    sprite(texture, x, y, width, height, 0, 0, texture.width, texture.height)
  }

  fun sprite(texture: Texture, x: Int, y: Int, width: Int, height: Int, sx: Int, sy: Int, sw: Int, sh: Int) {
    if (vertexCount + 4 >= MAX_VERTICES || indexCount + 6 >= MAX_INDICES)
      throw IllegalStateException("Maximum number of sprites reached")

    // A --- B // ACB
    // |   / |
    // |  /  |
    // | /   |
    // C --- D // CDB

    val texWidth = texture.width.toFloat()
    val texHeight = texture.height.toFloat()

    val texId = texture.id.toFloat()
    val uvLeft = sx / texWidth
    val uvTop = sy / texHeight
    val uvRight = (sx + sw) / texWidth
    val uvBottom = (sy + sh) / texHeight

    val viewWidth = this.width.toFloat()
    val viewHeight = this.height.toFloat()

    // [0, width] -> [-1, 1]
    val vertLeft = remap(x.toFloat(), 0f, viewWidth, -1f, 1f)
    val vertRight = remap((x + width).toFloat(), 0f, viewWidth, -1f, 1f)

    // [0, height] -> [1, -1]
    val vertTop = remap(y.toFloat(), 0f, viewHeight, 1f, -1f)
    val vertBottom = remap((y + height).toFloat(), 0f, viewHeight, 1f, -1f)

    val start = vertexCount
    var index = start * FLOAT_PER_VERTEX

    indexValues[indexCount + 0] = start + 0
    indexValues[indexCount + 1] = start + 2
    indexValues[indexCount + 2] = start + 1
    indexValues[indexCount + 3] = start + 1
    indexValues[indexCount + 4] = start + 3
    indexValues[indexCount + 5] = start + 2

    indexCount += 6
    vertexCount += 4

    // A - TOP LEFT

    vertexValues[index++] = vertLeft
    vertexValues[index++] = vertTop
    vertexValues[index++] = uvLeft
    vertexValues[index++] = uvTop
    vertexValues[index++] = texId

    // B - TOP RIGHT

    vertexValues[index++] = vertRight
    vertexValues[index++] = vertTop
    vertexValues[index++] = uvRight
    vertexValues[index++] = uvTop
    vertexValues[index++] = texId

    // C - BOTTOM LEFT

    vertexValues[index++] = vertLeft
    vertexValues[index++] = vertBottom
    vertexValues[index++] = uvLeft
    vertexValues[index++] = uvBottom
    vertexValues[index++] = texId

    // D - BOTTOM RIGHT

    vertexValues[index++] = vertRight
    vertexValues[index++] = vertBottom
    vertexValues[index++] = uvRight
    vertexValues[index++] = uvBottom
    vertexValues[index] = texId
  }

  private fun loadTextureImage(path: String): Triple<ByteBuffer, Int, Int> = MemoryStack.stackPush().use {
    val width = it.mallocInt(1)
    val height = it.mallocInt(1)
    val channels = it.mallocInt(1)

    val bytes = ResourceUtil.loadResourceBytes(path)
    val direct = ByteBuffer.allocateDirect(bytes.size)
    direct.put(bytes)
    direct.flip()

    val image = stbi_load_from_memory(direct, width, height, channels, 0) ?: throw IllegalStateException("Failed to load image: $path")

    return Triple(image, width.get(0), height.get(0))
  }

  private fun linkShaderProgram(shader: Int, vmod: Int, fmod: Int) {
    glAttachShader(shader, vmod)
    glAttachShader(shader, fmod)
    glLinkProgram(shader)

    if (glGetProgrami(shader, GL_LINK_STATUS) == GL_FALSE) {
      val info = glGetProgramInfoLog(shader)
      throw IllegalStateException("Shader linking failed: $info")
    }

    glDetachShader(shader, vmod)
    glDetachShader(shader, fmod)

    glDeleteShader(vmod)
    glDeleteShader(fmod)
  }

  private fun patchShaderSource(source: String): String {
    return source.replace(Regex("#define MAX_TEXTURES.+"), "#define MAX_TEXTURES $MAX_TEXTURES")
  }

  private fun createShaderModule(type: Int, path: String): Int {
    val shader = glCreateShader(type)
    glShaderSource(shader, patchShaderSource(ResourceUtil.loadResourceText(path)))
    glCompileShader(shader)

    if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
      val info = glGetShaderInfoLog(shader)
      throw IllegalStateException("Shader compilation failed: $info")
    }
    return shader
  }
}

fun unlerp(x: Float, a: Float, b: Float): Float {
  if (a == b) return 0f
  return (x - a) / (b - a)
}

fun lerp(a: Float, b: Float, t: Float): Float {
  return a + (b - a) * t
}

fun remap(x: Float, a1: Float, b1: Float, a2: Float, b2: Float): Float {
  return lerp(a2, b2, unlerp(x, a1, b1))
}