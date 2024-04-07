
## Calendar
[![](https://jitpack.io/v/PrimoDev23/Calendar.svg)](https://jitpack.io/#PrimoDev23/Calendar)

Calendar is a fully customizable calendar written in Jetpack Compose. This library offers a comprehensive and customizable solution for integrating calendars into your Jetpack Compose applications. Built upon the powerful Compose slot APIs and utilizing a pager-based architecture, this library provides developers with the flexibility to create stunning and functional calendar components tailored to their specific needs.

## Features
- Lazy loading
- Fully customizable month header
- Fully customizable day content
- Disabling user gestures to switch months
- Setting the first day of the week
- Min/Max months
- Fully customizable selection
- Scroll to specific months with or without animation

## Screenshots
These screenshots are created using the official sample app. Due to the fully customizable day and header contents you can make the calendar look like you want it to!

|Default calendar|Customize the minimum month|Customize the maximum month|Define the first day of the week|Select one or multiple independent days|Select a range of dates
|--|--|--|--|--|--|
| ![Default calendar](https://github.com/PrimoDev23/Calendar/blob/master/screenshots/Showcase_Default.png) | ![Customziable minimum month](https://github.com/PrimoDev23/Calendar/blob/master/screenshots/Showcase_minimum_month.png) | ![Customziable maximum month](https://github.com/PrimoDev23/Calendar/blob/master/screenshots/Showcase_maximum_month.png) | ![Define the first day of a week](https://github.com/PrimoDev23/Calendar/blob/master/screenshots/Showcase_start_day_of_week.png) | ![Select one or multiple independent days](https://github.com/PrimoDev23/Calendar/blob/master/screenshots/Showcase_single_selection.png) | ![Select a range of dates](https://github.com/PrimoDev23/Calendar/blob/master/screenshots/Showcase_range_selection.png) |

## Getting Started
To use the calendar, simply add the following dependcy to your project.

```kotlin
// project level build.gradle
repositories {
  maven { url 'https://jitpack.io' }
}

// module-level build.gradle
dependencies {
  implementation 'com.github.PrimoDev23:Calendar:<latest-version>'
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
