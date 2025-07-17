package scenes

abstract class Scene {
  abstract fun resume()
  abstract fun stop()
  abstract fun update(): Scene
  abstract fun render()
}