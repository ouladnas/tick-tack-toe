class Game {
  val board = Board()
  private var turn = Tile.CROSS

  fun start() {
    turn = Tile.CROSS
    board.reset()
  }

  fun play(x: Int, y: Int) {
    board.play(x, y, turn)
    turn = if (turn == Tile.CROSS) Tile.CIRCLE else Tile.CROSS
  }

  fun getState(): GameState {
    return when {
      board.won() -> GameState.WON
      board.isFull() -> GameState.DRAW
      else -> GameState.PLAYING
    }
  }

  fun getTurn(): Tile {
    return turn
  }
}

enum class GameState {
  WON, DRAW, PLAYING
}