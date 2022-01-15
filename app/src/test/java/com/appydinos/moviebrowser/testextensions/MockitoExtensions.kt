package com.appydinos.moviebrowser.testextensions

import org.mockito.ArgumentCaptor

// use this in place of captor.capture() if you are trying to capture an argument that is not nullable
fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()