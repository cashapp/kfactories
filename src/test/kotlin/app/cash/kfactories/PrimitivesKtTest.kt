/*
 * Copyright (C) 2021 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.cash.kfactories

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs

class PrimitivesTest {
  @Nested
  inner class `#newBoolean` {
    @Nested
    inner class `when called` {
      @RepeatedTest(10) fun `returns new boolean`() {
        val value = newBoolean()
        assertThat(value).isNotNull
        assertThat(value).isInstanceOf(java.lang.Boolean::class.java)
      }
    }

    @Nested
    inner class `when called multiple times` {
      @RepeatedTest(10) fun `returns different values`() {
        val values = (100..200).gen { newBoolean() }
        assertThat(values.distinct().size).isEqualTo(2)
      }
    }
  }

  @Nested
  inner class `#newInt` {
    @Nested
    inner class `when called` {
      @RepeatedTest(10) fun `returns new integer`() {
        val value = newInt()
        assertThat(value).isNotNull
        assertThat(value).isInstanceOf(java.lang.Integer::class.java)
      }
    }

    @Nested
    inner class `when called multiple times` {
      @RepeatedTest(10) fun `returns different values`() {
        val values = (100..200).gen { newInt() }
        // Collision is possible, so guaranteeing there are at least 1/2 of the
        // number of minimum values should suffice. The error case here is if there's
        // only one distinct value.
        assertThat(values.distinct().size).isGreaterThan(50)
      }
    }
  }

  @Nested
  inner class `#newInt(int, int)` {
    @Nested
    inner class `when called` {
      @Nested
      inner class `with min set` {
        @RepeatedTest(10) fun `returns only values starting after min and before max int`() {
          val min = newInt()
          assertThat(newInt(min = min)).isBetween(min, Int.MAX_VALUE)
        }
      }

      @Nested
      inner class `with max set` {
        @RepeatedTest(10) fun `returns only values starting after min and before max int`() {
          val max = newInt()
          assertThat(newInt(max = max)).isBetween(Int.MIN_VALUE, max)
        }
      }

      @Nested
      inner class `with min and max` {
        @RepeatedTest(10) fun `returns only values starting after min and before max int`() {
          val int = newInt()
          val otherInt = newInt()
          if (int < otherInt) {
            assertThat(newInt(int, otherInt)).isBetween(int, otherInt)
          } else {
            assertThat(newInt(otherInt, int)).isBetween(otherInt, int)
          }
        }
      }

      @Nested
      inner class `with min and max inverted` {
        @RepeatedTest(10) fun `throws exception`() {
          val int = newInt()
          val otherInt = newInt()
          if (int < otherInt) {
            assertThrows<IllegalArgumentException> { newInt(otherInt, int) }
          } else {
            assertThrows<IllegalArgumentException> { newInt(int, otherInt) }
          }
        }
      }
    }
  }

  @Nested
  inner class `#newInt(range)` {
    @Nested
    inner class `when called` {
      @Nested
      inner class `with min and max set` {
        @RepeatedTest(10) fun `returns only values between min and max`() {
          // Kotlin Ranges can only be positive.
          val int = abs(newInt())
          val otherInt = abs(newInt())
          if (int < otherInt) {
            assertThat(newInt(int..otherInt)).isBetween(int, otherInt)
          } else {
            assertThat(newInt(otherInt..int)).isBetween(otherInt, int)
          }
        }
      }
    }
  }

  @Nested
  inner class `#newLong(long, long)` {
    @Nested
    inner class `when called` {
      @Nested
      inner class `with min set` {
        @RepeatedTest(10) fun `returns only values starting after min and before max long`() {
          val min = newLong()
          assertThat(newLong(min = min)).isBetween(min, Long.MAX_VALUE)
        }
      }

      @Nested
      inner class `with max set` {
        @RepeatedTest(10) fun `returns only values starting after min and before max long`() {
          val max = newLong()
          assertThat(newLong(max = max)).isBetween(Long.MIN_VALUE, max)
        }
      }

      @Nested
      inner class `with min and max` {
        @RepeatedTest(10) fun `returns only values starting after min and before max long`() {
          val long = newLong()
          val otherLong = newLong()
          if (long < otherLong) {
            assertThat(newLong(long, otherLong)).isBetween(long, otherLong)
          } else {
            assertThat(newLong(otherLong, long)).isBetween(otherLong, long)
          }
        }
      }

      @Nested
      inner class `with min and max inverted` {
        @RepeatedTest(10) fun `throws exception`() {
          val long = newLong()
          val otherLong = newLong()
          if (long < otherLong) {
            assertThatThrownBy { newLong(otherLong, long) }
              .isInstanceOf(IllegalArgumentException::class.java)
          } else {
            assertThatThrownBy { newLong(long, otherLong) }
              .isInstanceOf(IllegalArgumentException::class.java)
          }
        }
      }
    }
  }

  @Nested
  inner class `#newString` {
    @Nested
    inner class `with no ranges` {
      @RepeatedTest(10) fun `returns a string with exactly or less than 255 characters`() {
        assertThat(newString()).hasSizeBetween(1, 255)
      }
    }

    @Nested
    inner class `with range` {
      @RepeatedTest(10) fun `returns a string within the range`() {
        val lowerBound = newInt(1..100)
        val higherBound = lowerBound + newInt(1..100)
        assertThat(newString(lowerBound..higherBound)).hasSizeBetween(lowerBound, higherBound)
      }
    }
  }
}