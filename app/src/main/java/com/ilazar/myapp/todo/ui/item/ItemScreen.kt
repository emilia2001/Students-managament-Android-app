package com.ilazar.myapp.todo.ui

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilazar.myapp.R
import com.ilazar.myapp.todo.ui.item.ItemViewModel

@Composable
fun ItemScreen(itemId: String, onClose: () -> Unit) {
    val itemViewModel = viewModel<ItemViewModel>(factory = ItemViewModel.Factory(itemId))
    val itemUiState = itemViewModel.uiState
    var text by rememberSaveable { mutableStateOf("") }
    Log.d("ItemScreen", "recompose, text = $text")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.item)) },
                actions = {
                    Button(onClick = {
                        Log.d("ItemScreen", "save item text = $text");
                        itemViewModel.createItem(text)
                        onClose()
                    }) { Text("Save") }
                }
            )
        }
    ) {
        Row {
            TextField(
                value = text,
                onValueChange = { text = it }, label = { Text("Text") },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


@Preview
@Composable
fun PreviewItemScreen() {
    ItemScreen(itemId = "0", onClose = {})
}
