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
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import kotlin.math.abs

class RangeTest {
  @Nested
  inner class `#gen` {
    @Nested
    inner class `with generator` {
      @Nested
      inner class `with range max set to 0` {
        @Test fun `returns no values`() {
          val range = 0..0
          val values = range.gen { newInt() }
          assertThat(values.size).isZero
        }
      }

      @Nested
      inner class `with equal range numbers` {
        @Test fun `returns exact numbers`() {
          val number = newInt(0..10)
          val range = number..number
          val values = range.gen { newInt() }
          assertThat(values.size).isEqualTo(number)
        }
      }

      @Nested
      inner class `with braod range` {
        @RepeatedTest(10) fun `returns expected number of values`() {
          val min = abs(newInt(0..100))
          val range = min..1000
          val values = range.gen { newInt() }
          assertThat(values.size).isBetween(range.first, range.last)
        }
      }
    }
  }
}