class Game {
  val board = Board()

  fun start() {}

  fun play(x: Int, y: Int, tile: Tile) {}

  fun getState(): GameState {
    TODO()
  }

  fun getTurn(): Tile {
    TODO()
  }
}

enum class GameState {
  WON, DRAW, PLAYING
}