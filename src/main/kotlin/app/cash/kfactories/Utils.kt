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

/**
 * Selects a random value from a list and returns it.
 *
 * Usage example: in your tests, when you generate a Card, you might want to switch brands to
 * guarantee the code works regardless of the brand.
 *
 * @param values the list to select the value from.
 * @return one of the value.
 */
fun <T> pluck(values: Collection<T>): T {
  return values.toList().random()
}

/**
 * Selects a random value from a list and returns it.
 *
 * Usage example: in your tests, when you generate a Card, you might want to switch brands to
 * guarantee the code works regardless of the brand.
 *
 * @param values the list to select the value from.
 * @return one of the value.
 */
fun <T> pluck(vararg values: T): T {
  return pluck(values.toList())
}

/**
 * Selects random values from a list. Please note that this could result in returning a list that
 * might not have a single value and the list is not sorted the way it was given.
 *
 * Usage example: a search operation can take a list of filters, there might be one or more
 * filters. Using {@link #pluckMany(Range, Collection)} guarantee the code works regardless of
 * the values in the list and their order.
 *
 * @param values the list to select values from.
 * @return n values from the list (n being between 0 and the maximum number of entries).
 */
fun <T> pluckMany(values: Collection<T>): Collection<T> {
  return pluckMany(0..values.size, values)
}

/**
 * Selects random values from a list. Please note that this could result in returning a list that
 * might not have a single value and the list is not sorted the way it was given.
 *
 * Usage example: a search operation can take a list of filters, there might be one or more
 * filters. Using {@link #pluckMany(Range, Collection)} guarantee the code works regardless of
 * the values in the list and their order.
 *
 * @param values the list to select values from.
 * @return n values from the list (n being between 0 and the maximum number of entries).
 */
fun <T> pluckMany(vararg values: T): Collection<T> {
  return pluckMany(values.toList())
}

/**
 * Selects random values from a list. Please note that this could result in returning a list that
 * might not have a single value and the list is not sorted the way it was given.
 *
 * Usage example: a search operation can take a list of filters, there might be one or more
 * filters. Using {@link #pluckMany(Range, Collection)} guarantee the code works regardless of the
 * values in the list and their order.
 *
 * @param range the number of elements to returns.
 * @param values the list to select values from.
 * @return n values from the list. This might return an empty list.
 */
fun <T> pluckMany(range: IntRange, vararg values: T): Collection<T> {
  return pluckMany(range, values.toList())
}

/**
 * Selects random values from a list. Please note that this could result in returning a list that
 * might not have a single value and the list is not sorted the way it was given.
 *
 * Usage example: a search operation can take a list of filters, there might be one or more
 * filters. Using {@link #pluckMany(Range, Collection)} guarantee the code works regardless of the
 * values in the list and their order.
 *
 * @param range the number of elements to returns.
 * @param values the list to select values from.
 * @return n values from the list. This might return an empty list.
 */
fun <T> pluckMany(range: IntRange, values: Collection<T>): Collection<T> {
  if (values.isEmpty()) {
    return listOf()
  }

  val start = 0
  var end: Int = newInt(range.first, values.size)

  if (range.last > 0 && end - start > range.last) {
    end = range.last
  }

  return values.toList().shuffled().subList(0, end)
}

/**
 * Given a value, either return the value or a null value. This is to mimic datasets that can be
 * inputted by api consumers or people. For instance: a Customer might or might not have a name.
 *
 * <pre>
 *  CreateCustomerRequest.toBuilder()
 *    .setOrClearName(maybe(newString())
 *    .build()
 * </pre>
 *
 * @param value the value to maybe return.
 * @return value or null.
 */
fun <T> maybe(value: T): T? {
  return value.takeIf { newBoolean() }
}

/**
 * Calls one of the given callbacks and applies it to the scoped object.
 *
 * For objects with specific conditions (such as having at least one required field set amongst
 * several), this method provides the ability to call one callback from the provided list.
 *
 * Usage:
 * ```
 *  person.applyOne(
 *    { person -> person.setName("Michael") },
 *    { person -> person.setCashTag("$michael") },
 *    { person -> person.setEmail("mortali@squareup.com") })
 * ```
 *
 * @param methods the callbacks to apply to the object. The returned value is the new version of
 *    the given object.
 * @return the updated object.
 */
fun <T> T.applyOne(vararg methods: (builder: T) -> T): T {
  return applyMany(1..1, *methods)
}

/**
 * Calls n to m of the given callbacks and applies it to the scoped object.
 *
 * For objects with specific conditions (such as having at least one required field set amongst
 * several), this method provides the ability to call one callback from the provided list.
 *
 * Usage: guarantee the person as at least 2 required fields.
 * ```
 *  person.applyMany(1..3,
 *    { person -> person.setName("Michael") },
 *    { person -> person.setCashTag("$michael") },
 *    { person -> person.setEmail("mortali@squareup.com") })
 * ```
 *
 * @param methods the callbacks to apply to the object. The returned value is the new version of
 *    the given object.
 * @return the updated object.
 */
fun <T> T.applyMany(range: IntRange, vararg methods: (result: T) -> T): T {
  var result = this
  pluckMany(range, methods.toList()).forEach {
    result = it.invoke(result)
  }
  return result
}