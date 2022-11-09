package com.ilazar.myapp.todo.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ItemList(itemsViewModel: ItemsViewModel) {
    Log.d("ItemList", "recompose")
    val itemsUiState by itemsViewModel.uiState.collectAsState()
    Column {
        for (item in itemsUiState.items) {
            ItemDetail(
                item = item,
                onToggleDone = { id ->
                    Log.d("ItemList", "onToggleDone ${id}")
                    itemsViewModel.toggleItemDone(id)
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewItemList() {
    ItemList(ItemsViewModel())
}
