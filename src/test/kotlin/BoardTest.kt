import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class BoardTest {

 @Test
 fun isWinning() {
  val borad = Board()
  assertThat(borad.isWinning()).isFalse()
 }
}