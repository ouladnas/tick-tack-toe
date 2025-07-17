package scenes

abstract class Scene {
  abstract fun init()
  abstract fun update(): Scene
  abstract fun render()
}