# MovieBrowser
Simple movie browser

A demo app that uses The Movie Database (TMDB) API. 
It pulls the latest, now playing, movies and displays them in a list. Clicking a movie takes the user to the details of the movie.
There is also a watchlist, stored locally in a Room Database, which can be added to via the movie details screen.
The app follows the system preferences for light and dark theme.

## Built using
MVVM Architecture

Hilt for Dependency injection

Material Components

Retrofit for networking

AndroidX Navigation

Kotlin

Coroutines

Glide for async image loading

Jetpack Paging V3 for loading the list

Lottie for animations

Room for local storage

Flexbox for dynamic grid layout (Classic UI)

Jetpack Compose

Accompanist for window insets and paging extensions (Compose UI)

## Versions
[Classic UI](https://github.com/jamesbarfoote/MovieBrowser/tree/classic_ui)
A version of this app written using xml style layouts.
An APK can be found in the [releases section](https://github.com/jamesbarfoote/MovieBrowser/releases/tag/1.0.0)

[Jetpack Compose version](https://github.com/jamesbarfoote/MovieBrowser/tree/master)
The app's UI written with Jetpack compose. This is currently a hybrid with the main activity and movie list using xml layouts for the main navigation and sliding pane. Everything else is using Compose



## Running the app

To allow the app to compile and run you will need to add the Auth file:

Location: `com/appydinos/moviebrowser/data/auth/Auth.kt`

It should look this:
```
package com.appydinos.moviebrowser.data.auth
class Auth: IAuth {
    override val theMovieDBAPI: String = "INSERT_YOUR_API_KEY_HERE"
}

```
