class Board {
  private val tiles = Array(9) { Tile.EMPTY }

  companion object {
    private val WIN_LINE_CANDIDATES = listOf(
      Triple(1, 2, 3),
      Triple(4, 5, 6),
      Triple(7, 8, 9),
      Triple(1, 4, 7),
      Triple(2, 5, 8),
      Triple(3, 6, 9),
      Triple(1, 5, 9),
      Triple(3, 5, 7),
    )
  }

  fun reset() = tiles.fill(Tile.EMPTY)

  fun isFull(): Boolean = tiles.all { it != Tile.EMPTY }

  fun get(x: Int, y: Int): Tile = tiles[y * 3 + x]

  fun set(x: Int, y: Int, tile: Tile) {
    tiles[y * 3 + x] = tile
  }

  fun play(index: Int, tile: Tile): Boolean {
    if(!isTileEmpty(index))
      return false
    tiles[index] = tile
    return true
  }

  fun play(x: Int, y: Int, tile: Tile): Boolean = play(y * 3 + x, tile)
  fun won(): Boolean = WIN_LINE_CANDIDATES.any { evaluatWetherLineIsWon(it) }

  private fun isTileEmpty(index: Int): Boolean = tiles[index] == Tile.EMPTY

  private fun evaluatWetherLineIsWon(candidate: Triple<Int, Int, Int>): Boolean {
    val first = tiles[candidate.first]
    val second = tiles[candidate.second]
    val third = tiles[candidate.third]
    return first != Tile.EMPTY && first == second && second == third
  }
}

enum class Tile {
  EMPTY,
  CROSS,
  CIRCLE
}