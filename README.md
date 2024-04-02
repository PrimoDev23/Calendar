## Calendar
Calendar is a fully customizable calendar written in Jetpack Compose. This library offers a comprehensive and customizable solution for integrating calendars into your Jetpack Compose applications. Built upon the powerful Compose slot APIs and utilizing a pager-based architecture, this library provides developers with the flexibility to create stunning and functional calendar components tailored to their specific needs.

## Getting Started
To use the calendar, simply add the following dependcy to your project.

```kotlin
// project level build.gradle
repositories {
  maven { url 'https://jitpack.io' }
}

// module-level build.gradle
dependencies {
  implementation 'com.github.PrimoDev23:Calendar:Tag'
}
```

## Usage
Using the Jetpack Compose Calendar Library is straightforward. Here's a quick example of how you can integrate it into your project:

```kotlin
import androidx.compose.runtime.Composable
import com.github.primodev23.calendar.Calendar
    
@Composable
fun MyCalendar() {
  Calendar(
      // Customize your calendar here
  ) {

  }
}
```

## License

This library is licensed under the Apache 2.0 License. See the [LICENSE](https://github.com/PrimoDev23/Calendar/blob/master/LICENSE) file for details.

Enjoy using the Calendar Library in your projects! If you have any questions or need assistance, don't hesitate to reach out.
