## BazaarPay Android SDK

This document aims to help you to integrate the `BazaarPay` android SDK within your application. For the topics out of this scope, you have to read the corresponding documents.

### Rules:
You have to get a `checkout` token before starting the payment flow using the SDK.
You have to commit the `checkout` token after a successful payment flow.

### Limitations:
`Minimum Android SDK` >= 17

## Dependency

To start working with `BazaarPay` Android SDK, you need to add its dependency to your `build.gradle` file at module level:

```
dependencies {
    implementation "com.github.cafebazaar:bazaarpay:[latest_version]"
}
```

To use this dependency, you need to add `jitpack` as a `maven` repository in `build.gradle` file in project level:

```
repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
```

## How to use

Using the `BazaarPay` Android SDK is very simple. You only need to create an instance of `BazaarPayLauncher` class. Then use this instance to start the payment flow by calling the `launchBazaarPay` function.

There are three points which you have to consider:
1. You have to create an instance of `BazaarPayLauncher` as a class property.*1
2. You have to pass three parameters while creating a new instance of the `BazaarPayLauncher` class:
* `Context`: Has to be an implementation of `ActivityResultCaller` interface.
* `onSuccess`: This function will call after a successful payment flow.
* `onCancel`: This function will call if the payment flow does not finish successfully (Cancel by the user).
3. You need to pass the `checkout` token as a parameter during calling `launchBazaarPay` function.*2

### Notes:

*1: This is mandatory because it uses the `Activity Result API`. Look at [this](https://developer.android.com/training/basics/intents/result) to learn more.

*2: There are two other optional parameters with default values (`isEnglish = false / isDarkMode = false`). You can use these parameters to set the language (Default is Persian) and dark mode (Default is light) manually.