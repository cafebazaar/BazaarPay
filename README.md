## BazaarPay Android SDK

[![Download](https://img.shields.io/jitpack/version/com.github.cafebazaar/bazaarpay)](https://jitpack.io/#cafebazaar/bazaarpay)
[![GitHub licence](https://img.shields.io/github/license/cafebazaar/BazaarPay)](https://www.apache.org/licenses/LICENSE-2.0)

BazaarPay is an all-in-one digital payment service that provides innovative payment solutions,
including [Direct debit](https://en.wikipedia.org/wiki/Direct_debit)
, [E-wallet](https://en.wikipedia.org/wiki/Digital_wallet),
and [BNPL](https://en.wikipedia.org/wiki/Buy_now,_pay_later). Our goal is to simplify money
management and help businesses increase sales.

This project is the SDK for integrating BazaarPay within your Android applications. For additional
information about BazaarPay, please visit our [website](https://bazaarpay.ir/).

### Requirements

- The SDK requires Android 4.2 (API level 17) or higher.
- You need a *Checkout Token* before starting a payment. It is a unique identifier that provides
  essential payment information. Check out [this]() documentation on how to generate one.

## Setup

### Configure the repositories

`BazaarPay` is available through the *JitPack* repository. You can declare this repository in your
build script as follows:

<details open>
<summary>Kotlin DSL</summary>

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}
```

</details>

<details>
<summary>Groovy DSL</summary>

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

</details>

### Add the dependency

After repository configuration, add a dependency on BazaarPay to your module's `build.gradle`
file:

<details open>
<summary>Kotlin DSL</summary>

```kotlin
dependencies {
    implementation("com.github.cafebazaar:bazaarpay:4.0.0")
}
```

</details>

<details>
<summary>Groovy DSL</summary>

```groovy
dependencies {
    implementation 'com.github.cafebazaar:bazaarpay:4.0.0'
}
```

</details>

## Usage

### 1. Register Payment Callback

BazaarPay uses
the [Activity Result API](https://developer.android.com/training/basics/intents/result). Register
the payment callback by calling the `registerForActivityResult` with an instance
of `StartBazaarPay` as its `contract` parameter. The callback provides you with a boolean that
indicates whether the payment was successful or not:

```kotlin
val bazaarPayLauncher = registerForActivityResult(StartBazaarPay()) { isSuccessful ->
    if (isSuccessful) {
        // A successful payment.
    } else {
        // An unsuccessful payment (Canceled by the user).
    }
}
```

The happy path of this `if` statement is the place you need to [commit](#3-commit-checkout-token)
the *Checkout Token*.

### 2. Launch Payment

Registering the payment callback will return a payment launcher instance. You can start a payment
flow by calling the `launch()` function on the payment launcher. It takes an instance of
the `BazaarPayOptions` as its parameter:

```kotlin
val options = BazaarPayOptions(checkoutToken = "CHECKOUT_TOKEN")
bazaarPayLauncher.launch(options)
```

`BazaarPayOptions` has a mandatory `checkoutToken` constructor parameter which is the token
you [generated before](#requirements). But there are also other optional parameters that you can
configure to your needs:

* `phoneNumber` - pre-fills the input field of the login screen with this number.
* `isInDarkMode` - enables Dark Mode for the UI elements of the payment flow.

### 3. Commit Checkout Token

You have to commit the *Checkout Token* after successful payment. There is a suspend `commit()`
function for this purpose that you can call from a coroutine scope:

```kotlin
// Inside isSuccessful branch of the registered payment callback
myScope.launch {
    commit(
        checkoutToken = "CHECKOUT_TOKEN",
        context = requireContext(),
        onSuccess = { },
        onFailure = { }
    )
}
```

Otherwise, if you are using other technologies, you need to implement this yourself. It is better to
call it from a [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
worker or a Service for safety reasons.

> Although sending tokens through the SDK is possible, we recommend this happens on the server
> side.

License
--------

    Copyright 2022 Cafebazaar, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
