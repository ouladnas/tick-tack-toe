package scenes

abstract class Scene {
  abstract fun init()
  abstract fun update()
  abstract fun render()
}