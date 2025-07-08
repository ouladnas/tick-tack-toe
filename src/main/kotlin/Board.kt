class Board {
  private val tiles = Array(9) { Tile.EMPTY }
  private val winningArrays = listOf(
    Triple(1, 2, 3),
    Triple(4, 5, 6),
    Triple(7, 8, 9),
    Triple(1, 4, 7),
    Triple(2, 5, 8),
    Triple(3, 6, 9),
    Triple(1, 5, 9),
    Triple(3, 5, 7),
  )

  fun reset() {
    tiles.fill(Tile.EMPTY)
  }

  fun isFull(): Boolean {
    return tiles.all { it != Tile.EMPTY }
  }

  fun get(x: Int, y: Int): Tile {
    return tiles[y * 3 + x]
  }

  fun set(index: Int, tile: Tile): Boolean {
    if(!isTileEmpty(index))
      return false

    tiles[index] = tile
    return true
  }

  private fun isTileEmpty(index: Int): Boolean = tiles[index] == Tile.EMPTY

  fun isWinning(): Boolean {
    winningArrays.forEach { it ->
      if(isNotEmptyAndSameTile(it))
        return true
    }
    return false
  }

  private fun isNotEmptyAndSameTile(it: Triple<Int, Int, Int>): Boolean {
    val first = tiles[it.first]
    val second = tiles[it.second]
    val third = tiles[it.third]

    return first != Tile.EMPTY && first == second && second == third
  }
}

enum class Tile {
    X, O, EMPTY
}