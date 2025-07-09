import org.lwjgl.opengl.GL41.*
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.stb.STBImage.*
import java.nio.FloatBuffer
import java.nio.IntBuffer
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
  private var projection = Matrix4f()
  private var projectionLocation = 0
  private var projectionBuffer = FloatBuffer.allocate(16)

  private var vertexValues = FloatArray(MAX_VERTICES * FLOAT_PER_VERTEX)
  private var vertexCount = 0

  private var indexValues = IntArray(MAX_INDICES)
  private var indexCount = 0

  private var textures = IntArray(MAX_TEXTURES)
  private var textureCount = 0

  fun onBeginFrame() {
    vertexCount = 0
    indexCount = 0
    glClear(GL_COLOR_BUFFER_BIT)
  }

  fun onEndFrame() {
    val vertexBuffer = BufferUtils.createFloatBuffer(vertexCount * FLOAT_PER_VERTEX).put(vertexValues).flip()
    val indexBuffer = BufferUtils.createIntBuffer(indexCount).put(indexValues).flip()

    glBindBuffer(GL_ARRAY_BUFFER, vbo)
    glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuffer)

    indexBuffer.position(indexCount)
    indexBuffer.flip()

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
    glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, indexBuffer)

    glUseProgram(shader)
    glBindVertexArray(vao)
    glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0L)
  }

  fun free() {
    glDeleteBuffers(vbo)
    glDeleteBuffers(ibo)
    glDeleteVertexArrays(vao)
    glDeleteProgram(shader)
  }

  fun init() {
    vao = glGenVertexArrays()
    glBindVertexArray(vao)

    vbo = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, vbo)

    glBufferData(GL_ARRAY_BUFFER, (MAX_VERTICES * FLOAT_PER_VERTEX * Float.SIZE_BYTES).toLong(), GL_STATIC_DRAW)

    glEnableVertexAttribArray(0)
    glVertexAttribPointer(0, 2, GL_FLOAT, false, FLOAT_PER_VERTEX * Float.SIZE_BYTES, 0L)

    glEnableVertexAttribArray(1)
    glVertexAttribPointer(1, 3, GL_FLOAT, false, FLOAT_PER_VERTEX * Float.SIZE_BYTES, 2L * Float.SIZE_BYTES)

    ibo = glGenBuffers()
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)

    glBufferData(GL_ELEMENT_ARRAY_BUFFER, (MAX_INDICES * Int.SIZE_BYTES).toLong(), GL_STATIC_DRAW)

    projection.ortho2D(0f, 800f, 600f, 0f)
    shader = glCreateProgram()

    val vert = createShaderModule(GL_VERTEX_SHADER, "shaders/sprite.vert")
    val frag = createShaderModule(GL_FRAGMENT_SHADER, "shaders/sprite.frag")

    linkShaderProgram(shader, vert, frag)

    glUseProgram(shader)
    projectionLocation = glGetUniformLocation(shader, "u_projection")
    glUniformMatrix4fv(projectionLocation, false, projection.get(projectionBuffer))
  }

  fun texture(path: String): Texture {
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

//    stbi_image_free(image)

    return Texture(id, width, height)
  }

  fun sprite(texture: Texture, x: Int, y: Int) {
    sprite(texture, x, y, texture.width, texture.height)
  }

  fun sprite(texture: Texture, x: Int, y: Int, width: Int) {
    sprite(texture, x, y, width, (texture.height * (width / texture.width.toFloat())).toInt())
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

    val vertLeft = x.toFloat()
    val vertRight = (x + width).toFloat()
    val vertTop = (y + height).toFloat()
    val vertBottom = y.toFloat()

    var start = vertexCount
    var index = start * FLOAT_PER_VERTEX

    indexValues[indexCount + 0] = start++
    indexValues[indexCount + 1] = start++
    indexValues[indexCount + 2] = start++
    indexValues[indexCount + 3] = start++
    indexValues[indexCount + 4] = start++
    indexValues[indexCount + 5] = start

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

    val bytes = loadResourceBytes(path)
    val direct = ByteBuffer.allocateDirect(bytes.size)
    direct.put(bytes)
    direct.flip()

    val image = stbi_load_from_memory(direct, width, height, channels, 0) ?: throw IllegalStateException("Failed to load image: $path")

    return Triple(image, width.get(0), height.get(0))
  }

  private fun loadResource(path: String): URL {
    val resolved = Paths.get("/", path).toString()
    return SpriteRenderer::class.java.getResource(resolved)
      ?: throw IllegalStateException("Resource not found: $resolved")
  }

  private fun loadResourceBytes(path: String) = loadResource(path).readBytes()
  private fun loadResourceText(path: String) = loadResource(path).readText()

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
    glShaderSource(shader, patchShaderSource(loadResourceText(path)))
    glCompileShader(shader)

    if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
      val info = glGetShaderInfoLog(shader)
      throw IllegalStateException("Shader compilation failed: $info")
    }
    return shader
  }
}
