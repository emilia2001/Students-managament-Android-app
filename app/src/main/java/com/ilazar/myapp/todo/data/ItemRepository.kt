package com.ilazar.myapp2.todo.data

import android.util.Log
import com.ilazar.myapp.todo.data.Item

object ItemRepository {
    private val _items = List(100, { index ->
        Item(id = "$index", text = "Item $index")
    }).toMutableList()
    val items: List<Item> = _items

    fun update(id: String, text: String): Item? {
        Log.d("ItemRepository", "update $id")
        val index = _items.indexOfFirst { it -> it.id == id }
        if (index != -1) {
            val item = _items[index].copy(text = text)
            _items.set(index, item)
            return item
        }
        return null
    }
}