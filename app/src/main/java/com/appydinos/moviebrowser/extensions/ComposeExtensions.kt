package com.appydinos.moviebrowser.extensions

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems

//@ExperimentalFoundationApi
//fun <T: Any> LazyGridScope.items(
//    lazyPagingItems: LazyPagingItems<T>,
//    itemContent: @Composable LazyGridScope.(value: T?) -> Unit
//) {
//    items(lazyPagingItems.itemCount) { index ->
//        itemContent(lazyPagingItems[index])
//    }
//}

fun <T : Any> LazyGridScope.items(
    items: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    span: ((item: T) -> GridItemSpan)? = null,
    contentType: ((item: T) -> Any)? = null,
    itemContent: @Composable LazyGridItemScope.(value: T?) -> Unit
) {
    items(
        count = items.itemCount,
        key = if (key == null) null else { index ->
            val item = items.peek(index)
            if (item == null) {
                -1
//                Placeholder(index)
//                PagingPlaceholderKey(index)
            } else {
                key(item)
            }
        },
        span = if (span == null) null else { index ->
            val item = items.peek(index)
            if (item == null) {
                GridItemSpan(1)
            } else {
                span(item)
            }
        },
        contentType = if (contentType == null) {
            { null }
        } else { index ->
            val item = items.peek(index)
            if (item == null) {
                null
            } else {
                contentType(item)
            }
        }
    ) { index ->
        itemContent(items[index])
    }
}