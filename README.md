## BazaarPay Android SDK

This document aims to help you to integrate the `BazaarPay` android SDK within your application. For
the topics out of this scope, read the corresponding documents.

### Minimum requirements

The SDK requires Android 4.2 (API level 17) or higher.

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
    implementation "com.github.cafebazaar:bazaarpay:[latest_version]"
}
```

## Checkout Token

You need a `checkout` token before starting the payment flow.

## Usage

You need to create an instance of `BazaarPayLauncher` class. Then use this instance to start the
payment flow by calling the `launchBazaarPay` function.

There are three points which you have to consider:

1. You have to create an instance of `BazaarPayLauncher` as a class property[^1].
2. You have to pass three parameters while creating a new instance of the `BazaarPayLauncher` class:

* `Context`: Has to be an implementation of `ActivityResultCaller` interface.
* `onSuccess`: This function will call after a successful payment flow.
* `onCancel`: This function will call if the payment flow does not finish successfully (Cancel by
  the user).

3. You need to pass the `checkout` token as a parameter during calling `launchBazaarPay`
   function[^2].
   * You can optionally pass the user phone number in `phoneNumber` in order to pre fill phone
     number in login screen.
4. You have to commit the `checkout` token after a successful payment flow. Recommended way for
   using this API is calling it from the server side, But this option is also provided in the SDK.
   For doing this you should call the suspend `commit` function from a coroutine scope. Otherwise if
   you are using other technologies you can implement it yourself. Note that
   it is better to call it from a WorkManager worker or a Service for safety reasons.

[^1]: This is mandatory because it uses the `Activity Result API`. Look
at [this](https://developer.android.com/training/basics/intents/result) to learn more.

[^2]: There are two other optional parameters with default
values (`isEnglish = false / isDarkMode = false`). You can use these parameters to set the
language (Default is Persian) and dark mode (Default is light) manually.
