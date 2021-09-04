# kfactories

Set of factories and utils to run an
effective [smart monkey](https://en.wikipedia.org/wiki/Monkey_testing#Smart_monkey_tests)
and [property-based testing](https://www.infoq.com/presentations/property-based-testing/) strategy.

In short, monkeys have a strong understanding of the different entities and their multiple states to
generate them. By rotating all values, they focus on ensuring your system is robust to valid and
known state changes and behaves as expected.

For instance: a customer can have a first name, last name, email address, and phone number, and at
least one of these fields is required. As you run the tests, you will always get a unique
combination that represents a valid customer. For example, the first run might have all customer
fields set. Another run might only have the email.

## Motivations

At Square, we use resources and objects that can have very complex states.

When you consume an API, you might make some assumptions about what is and is not present on these
objects. For example, in the case of the customer resource mentioned above, if your tests always use
a static view of a customer with a fixed set first name field, your tests will never get checked
against all the different variants of a valid customer.

As such, tests are not thoroughly covering edge cases. Resources can also be nested. For instance: a
customer can have cards and addresses, each of which has its own set of assumptions.

How do you write tests that enable you to verify there isn't a bug in your codebase, given all these
different states? That's where smart monkeys and property-based testing come to the rescue. They
know the valid state for each resource, and they can set all of this for you.

If an assumption changes (for instance: a first name was previously required and no longer is), by
changing this assumption in the corresponding customer factory, we will be able to detect other
places where code needs adjustments.

### Example

The most common scenario of the problem described above is the following: a mock value is created
and passed down to the function. This mock doesn't know which properties on the object are optional.

```kotlin
fun sendEmail(customer: Customer, message: String) {
  emailService.sendEmail(customer.getEmail(), message);
}

@Test fun sendEmailSuccess() {
  val customer = Customer.toBuilder()
    .setFirstName("Michaël")
    .setEmail("michael@gmail.com")
    .build();

  sendEmail(customer, "Hello!");
  assertThat(emailService.emailSentTo(customer.getEmail())).isTrue;
}
```

An update is made to include the user's first name in the to field. The code can be modified as:

```kotlin
fun sendEmail(customer: Customer, message: String) {
  emailService.sendEmail(customer.getFirstName(), customer.getEmail(), message);
}
```

While the original tests are passing, this makes the expectation that the first name is guaranteed,
which might not be accurate and lead to severe production issues.

## Usage

### Primitives

Before talking about objects, let's talk about primitives. We have a few primitives you can use:

* `newBoolean`: returns either true or false.
* `newInt(min=MIN_INT, max=MAX_INT)`: returns an integer between min and max.
* `newInt(n..m)`: returns an integer within the given range.
* `newLong(min=MIN_LONG, max=MAX_LONG)`: returns a long between min and max.
* `newLong(n..m)`: returns a long within the provided range.
* `newString(n..m)`: creates a string with `n` to `m` characters.

### Utils

#### maybe

Given an object, returns the object or return null. This feature is particularly useful to indicate
a field is optional on a resource.

Example:

```kotlin
val customer = Customer(
  id = newCustomerId(),
  phone = maybe(newPhoneNumber())
)
```

#### Generator

Extension to IntRange to create between n and m values. This is particularly useful when creating
list of values. For instance: a class has between 20 to 25 students:

```kotlin
val students = 20..25.gen { newStudent() }
```

#### Plucking values

There are two methods to use: `pluck` (which will pluck a single value for a given collection), and
`pluckMany` which returns one or more values.

```kotlin
// returns one student from the list.
val student = pluck(students)

// returns one student from the passed-in values.
val student = pluck(student1, student2, student3)

// returns between 1 and 2 students from the list.
val students = pluckMany(1..2, students)

// returns between 1 and 2 from the passed in students.
val students = pluckMany(
  1..2, student1, student2, student3, student4, student5, student6
)
```

#### Apply callbacks

There are two methods available for this: `applyOne`, which applies one of the callback in the
provided list, and `applyMany` which applies one of the callback from the given list. This is useful
when working with objects that have multiple dimensions that might be dependent on one another (
think of `oneOf` from proto).

```kotlin
// The student will either have a name, or an email or a phone.
val student = Student.Builder().applyOne(
  { it.setName(newStudentName()) },
  { it.setEmail(newStudentEmail()) },
  { it.setPhone(newStudentPhone()) }
).build()

// The student will have at least two fields set and a max of 4.
val student = Student.Builder().applyMany(
  2..4,
  { it.setName(newStudentName()) },
  { it.setEmail(newStudentEmail()) },
  { it.setPhone(newStudentPhone()) },
  { it.setGrade(newGrade()) },
  { it.setCity(newStudentCity()) },
).build()
```

## Recommendation

While this is a library and you can use it as you wish, we found our codebase to be more accessible
and maintainable when we follow a set of principles and general structure for our factories.

### Principles

* Each object in your codebase must have a factory. It helps you and future maintainers to not have
  to guess what is the valid representation of the object.

* Each factory method must be small. In practice: this means nested objects must have their own
  factories.

* Factories should be easily identifiable and follow a repeated pattern. We recommend using the
  prefix `new` in front of all your factory methods. E.g. `newCustomer`, `newInt`, `newLong`.

### Factory structure

We use named params to set default generators based on what represents a valid object, and we allow
the caller to override them when they see fit. The alternative is to have the factory do its work,
then use `copy` to override field values.

```kotlin
fun newCustomer(
  id: CustomerId = newCustomerId(),
  fullName: String? = maybe(newCustomerFullName()),
  phone: PhoneNumber? = maybe(newCustomerPhoneNumber()),
  email: EmailAddress = newCustomerEmail()
) = Customer(
  id = id,
  fullName = fulName,
  phone = phone,
  email = email
)
```

It gives the opportunity of the caller to override what they care about without impacting the other
fields or having to copy the generated data object. For instance, if a test requires the phone
number to be present:

```kotlin
val customer = newCustomer(phone = newCustomerPhoneNumber())
```

Or absent:

```kotlin
val customer = newCustomer(phone = null)
```

# License

```
Copyright 2021 Square, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
