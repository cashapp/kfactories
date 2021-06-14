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
 * Extends IntRanges to support generating test values based on its lower and upper bounds.
 *
 * For instance: 10..20 will create between 10 to 20 values created by the given generator. This
 * method only works with positive ranges. Any negative ranges will result in a failure as
 * collections cannot contain less than 0 items. If the range starts with 0, it means the list
 * might not contain any customers.
 *
 * Example:
 *  // Creates between 5 to 10 valid customer objects.
 *  5..10.gen { newCustomer() }
 *
 * @param generator the generator to use to create the corresponding collection.
 * @return collection of generated values.
 */
fun <T> IntRange.gen(generator: () -> T): Collection<T> {
  val count = newInt(first, last)
  return (1..count).map { generator() }
}