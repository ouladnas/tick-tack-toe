import java.net.URL
import java.nio.file.Paths
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

object ResourceUtil {

  private fun loadResource(path: String): URL {
    val resolved = Paths.get("/", path).toString()
    return ResourceUtil::class.java.getResource(resolved)
      ?: throw IllegalStateException("Resource not found: $resolved")
  }

  fun loadResourceBytes(path: String) = loadResource(path).readBytes()
  fun loadResourceText(path: String) = loadResource(path).readText()

//  inline fun <reified T> loadResourceJson(path: String): T = Json.decodeFromString<T>(loadResourceText(path))

  fun loadResourceJson(path: String): JsonElement = Json.decodeFromString(loadResourceText(path))
}