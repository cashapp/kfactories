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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UtilsTest {
  @Nested
  inner class `#pluck` {
    @Nested
    inner class `with collection` {
      @Test fun `returns value`() {
        val values = (1..10).gen { newInt() }
        val value = pluck(values)
        assertThat(values).contains(value)
      }
    }

    @Nested
    inner class `with static values` {
      @Test fun `returns value`() {
        val firstValue = newInt()
        val secondValue = newInt()
        val thirdValue = newInt()
        val value = pluck(firstValue, secondValue, thirdValue)
        assertThat(listOf(firstValue, secondValue, thirdValue)).contains(value)
      }
    }
  }

  @Nested
  inner class `#pluck many` {
    @Nested
    inner class `with collection` {
      @Test fun `returns values`() {
        val values = (1..100).gen { newInt() }
        val results = pluckMany(values)
        assertThat(values).containsAll(results)
      }

      @Nested
      inner class `with ranges` {
        private lateinit var range: IntRange
        private lateinit var values: Collection<Int>
        private lateinit var results: Collection<Int>

        @BeforeEach fun before() {
          range = 0..50
          values = (1..100).gen { newInt() }
          results = pluckMany(range, values)
        }

        @Test fun `returns values in given range`() {
          assertThat(results.size).isBetween(range.first, range.last)
        }

        @Test fun `returns values`() {
          assertThat(values).containsAll(results)
        }
      }
    }

    @Nested
    inner class `with static values` {
      @Test fun `returns value`() {
        val firstValue = newInt()
        val secondValue = newInt()
        val thirdValue = newInt()
        val results = pluckMany(firstValue, secondValue, thirdValue)
        assertThat(listOf(firstValue, secondValue, thirdValue)).containsAll(results)
      }
    }
  }

  @Nested
  inner class `#maybe` {
    @Nested
    inner class `with object` {
      @Test fun `might return null`() {
        val value = newInt()
        assertThat((0..100).map { maybe(value) }).contains(null)
      }

      @Test fun `might return object`() {
        val value = newInt()
        assertThat((0..100).map { maybe(value) }).contains(value)
      }
    }
  }

  @Nested
  inner class `#applyOne` {
    @Nested
    inner class `with object` {
      @Test fun `runs at least one function`() {
        val number = newInt()
        val newNumber = number.applyOne(
          { it.plus(newInt(10, 100)) },
          { it.minus(newInt(10, 100)) }
        )
        assertThat(number).isNotEqualTo(newNumber)
        assertThat(number).isBetween(newNumber - 100, newNumber + 100)
      }
    }
  }

  @Nested
  inner class `#applyMany` {
    @Nested
    inner class `with object` {
      private lateinit var invocations: java.util.HashMap<Int, Int>
      private lateinit var functions: Array<(Int) -> Int>
      private var number: Int = 0
      private var newNumber: Int = 0

      @BeforeEach fun before() {
        number = newInt()

        invocations = HashMap()
        functions = (1..newInt(0, 1000))
          .map { Pair(it, newInt(10, 1000)) }
          .map {
            { value: Int ->
              invocations[it.first] = it.second
              value.plus(it.second)
            }
          }.toTypedArray()

        newNumber = number.applyMany(0..functions.size, *functions)
      }

      @Test fun `returns a different number`() {
        assertThat(number).isNotEqualTo(newNumber)
      }

      @Test fun `returns the expected number`() {
        assertThat(newNumber).isEqualTo(number + invocations.values.sum())
      }
    }
  }
}