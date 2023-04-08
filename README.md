## BazaarPay Android SDK

This document aims to help you to integrate the *BazaarPay* android SDK within your application. For
the topics out of this scope, read the corresponding documents.

### Requirements

- The SDK requires Android 4.2 (API level 17) or higher.
- You need a *Checkout Token* before starting a payment. It is a unique identifier that provides
  essential payment information. Check out [this]() documentation on how to generate one.

## Setup

`BazaarPay` is available through the JitPack repository. You can declare this repository in your
project `build.gradle` file:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

Add the dependency to your module `build.gradle` file:

```groovy
dependencies {
    implementation 'com.github.cafebazaar:bazaarpay:[latest_version]'
}
```

## Usage

### 1. Register Payment Callback

*BazaarPay* uses
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

* `phoneNumber` - the default phone number to pre-fill the login screen's input field. It uses a
  null value by default, resulting in no pre-filled input.
* `isInDarkMode` - enables Dark Mode for the UI elements of the payment flow, which are in Light Mode
  by default.

### 3. Commit Checkout Token

You have to commit the *Checkout Token* after successful payment. There is a suspend `commit()`
function for this purpose that you can call from a coroutine scope:

```kotlin
// Inside onSuccess callback of launchBazaarPay
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
