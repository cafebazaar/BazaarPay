## BazaarPay Android SDK

This document aims to help you to integrate the *BazaarPay* android SDK within your application. For
the topics out of this scope, read the corresponding documents.

### Requirements

- The SDK requires Android 4.2 (API level 17) or higher.
- You need a *Checkout Token* before starting a payment. Check out [this]() documentation on
  how to get one.

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

### 1. Register For Activity Result

*BazaarPay* uses
the [Activity Result API](https://developer.android.com/training/basics/intents/result). Register
for an activity result and pass `StartActivityForResult` as its `contract` parameter. Inside its
callback, notify the `BazaarPayLauncher` object about the results as follows:

```kotlin
val registeredLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    BazaarPayLauncher.onResultLauncher(
        result,
        onSuccess = { },
        onCancel = { }
    )
}
```

You also need to specify two callback parameters:

* `onSuccess`: Will be called after a successful payment. This is the place you need
  to [commit](#3-commit-checkout-token) the *Checkout Token*.
* `onCancel`: Will be called if the payment flow has not been finished successfully (Canceled by the
  user).

### 2. Launch Payment

After registering for the activity result, you can start a payment flow by calling
the `launchBazaarPay()` function. It takes the following parameters:

* `Context`: The context in which payment happens.
* `checkoutToken`: The token you [generated before](#requirements).
* `activityResultLauncher`: The launcher you [just registered](#1-register-for-activity-result) for
  its result.

```kotlin
BazaarPayLauncher.launchBazaarPay(
    context = requireContext(),
    checkoutToken = "CHECKOUT_TOKEN",
    activityResultLauncher = registeredLauncher
)
```

There are also other optional parameters that you can configure to your needs:

* `phoneNumber`: Pre-fills the input field of the login screen with the specified number. By
  default, it uses a null value, resulting in no pre-filled number.
* `isEnglish`: Whether force English language for the payment flow. The default value is `false` and
  the Persian language will be used.
* `isDarkMode`: Payment flow supports Dark Mode for its UI elements. The parameter enables this
  option, which is `false` by default and uses Light Mode.

### 3. Commit Checkout Token

You have to commit the *Checkout Token* after a successful payment. There is a suspend `commit()`
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

Otherwise, if you are using other technologies you need to implement this yourself. It is better to
call it from a [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
worker or a Service for safety reasons.

> Although sending tokens through the SDK is possible, we recommend this happens on the server
> side. 
