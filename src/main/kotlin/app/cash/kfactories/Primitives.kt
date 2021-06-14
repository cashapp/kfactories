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

import java.util.Random

private val random = Random()

/**
 * @return a new boolean.
 */
fun newBoolean(): Boolean {
  return random.nextBoolean()
}

/**
 * @return a new integer.
 */
fun newInt(): Int {
  return random.nextInt()
}

/**
 * Given a min and a max, create a new integer.
 *
 * @param min the minimum value.
 * @param max the maximum value.
 * @return a value between min and max.
 */
fun newInt(min: Int? = null, max: Int? = null): Int {
  val minValue = min ?: Int.MIN_VALUE
  val maxValue = max ?: Int.MAX_VALUE

  require(minValue <= maxValue) {
    "The minimum value ($min) cannot higher than the maximum value ($max)."
  }

  val intRange = maxValue - minValue + 1
  return if (intRange < 0) {
    minValue + random.nextInt(Int.MAX_VALUE)
  } else {
    minValue + random.nextInt(intRange)
  }
}

/**
 * Given a range, create a new integer.
 *
 * @param range the range to use when creating the integer.
 * @return random integer within the given range.
 */
fun newInt(range: IntRange): Int {
  return range.random()
}

/**
 * Given a min and a max, create a new long.
 *
 * @param min the minimum value.
 * @param max the maximum value.
 * @return a value between min and max.
 */
fun newLong(min: Long? = null, max: Long? = null): Long {
  val minValue = min ?: Long.MIN_VALUE
  val maxValue = max ?: Long.MAX_VALUE

  if (minValue == Long.MIN_VALUE && maxValue == Long.MAX_VALUE) {
    return random.nextLong()
  }

  require(minValue <= maxValue) {
    "The minimum value ($min) cannot higher than the maximum value ($max)."
  }

  val longRange = maxValue - minValue + 1
  return if (longRange < 0) {
    minValue + (random.nextFloat() * Long.MAX_VALUE).toLong()
  } else {
    minValue + (random.nextFloat() * longRange).toLong()
  }
}

/**
 * Given a long range, create a new integer.
 *
 * @param range the range to use when creating the long.
 * @return random long within the given range.
 */
fun newLong(range: LongRange): Long {
  return range.random()
}

/**
 * Creates a non-empty string that has a number of characters between the provided
 * range (defaults to 1-255).
 *
 * @param range the range representing the possible number of characters.
 * @return the string
 */
fun newString(range: IntRange = 1..255): String {
  val characters = ('a'..'z') + ('A'..'Z') + ('0'..'9')
  return (1..newInt(range)).map { characters.random() }.joinToString("")
}