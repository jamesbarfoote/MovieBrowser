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

Flexbox for dynamic grid layout

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

