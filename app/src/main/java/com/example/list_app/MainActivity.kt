package com.example.list_app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.list_app.ui.theme.ListappTheme

data class ShoppingItem(val name: String, val isBought: Boolean = false)

class StorageHelper(context: Context) {
    private val prefs = context.getSharedPreferences("shopping_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveList(list: List<ShoppingItem>) {
        prefs.edit().putString("shopping_list", gson.toJson(list)).apply()
    }

    fun loadList(): List<ShoppingItem> {
        val json = prefs.getString("shopping_list", null) ?: return emptyList()
        val type = object : TypeToken<List<ShoppingItem>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveDictionary(dict: List<String>) {
        prefs.edit().putString("dictionary", gson.toJson(dict)).apply()
    }

    fun loadDictionary(): List<String> {
        val json = prefs.getString("dictionary", null)
        if (json == null) {
            return listOf("Молоко", "Хлеб", "Яйца", "Яблоко", "Бананы", "Кофе", "Сыр салатный", "Сыр для нарезки", "Сырок плавленный", "Груша", "Баклажан", "Огурцы",
                "Помидоры", "Морковь", "Морковь корейская", "Лук красный", "Торт", "Колбаса копчёная", "Колбаса варёная", "Ветчина", "Салат зеленый", "Шпинат",
            "Ананас в банке", "Курица копчёная", "Горошек", "Кукуруза", "Шпроты", "Говядина", "Свинина", "Курица", "Курогрудь", "Пекинка", "Апельсин", "Картошка",
            "Лимон", "Лайм", "Виноград", "Майонез", "Соль", "Сахар", "Масло подсолнечное", "Масло оливковое", "Лимонный сок", "Кетчуп", "Кепчук", "Перец", "Фисташки",
            "Кешью", "Арахис", "Сгущенка", "Сливки", "Йогурт", "Творог", "Рыба", "Минтай", "Сойка", "Фарш", "Тесто", "Капуста")
        }
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListappTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ShoppingScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen() {
    val context = LocalContext.current
    val storage = remember { StorageHelper(context) }

    val dictionary = remember { mutableStateListOf<String>().apply { addAll(storage.loadDictionary()) } }
    val shoppingList = remember { mutableStateListOf<ShoppingItem>().apply { addAll(storage.loadList()) } }

    var searchQuery by remember { mutableStateOf("") }
    var showDictionaryDialog by remember { mutableStateOf(false) }

    fun updateList(action: () -> Unit) {
        action()
        storage.saveList(shoppingList.toList())
    }

    fun addToDictionary(word: String) {
        dictionary.add(word)
        storage.saveDictionary(dictionary.toList())
    }

    val suggestions = remember(searchQuery, dictionary.size) {
        if (searchQuery.isBlank()) emptyList()
        else dictionary.filter { it.contains(searchQuery.trim(), ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Text(
                text = "Список покупок",
                style = MaterialTheme.typography.headlineMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { showDictionaryDialog = true }) {
                    Text("Словарь (${dictionary.size})")
                }
                if (shoppingList.isNotEmpty()) {
                    TextButton(onClick = { updateList { shoppingList.clear() } }) {
                        Text("Очистить всё", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Найти или добавить продукт...") },
            singleLine = true,
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Очистить поиск")
                    }
                }
            }
        )

        if (searchQuery.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
                Column {
                    suggestions.forEach { product ->
                        Text(
                            text = product,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    updateList { shoppingList.add(0, ShoppingItem(product)) }
                                    searchQuery = ""
                                }
                                .padding(16.dp)
                        )
                    }
                    val exactMatch = dictionary.any { it.equals(searchQuery.trim(), ignoreCase = true) }
                    if (!exactMatch) {
                        // Делаем первую букву заглавной заранее
                        val formattedProduct = searchQuery.trim().replaceFirstChar { it.uppercase() }

                        Divider()
                        // Вариант 1: Добавить и в словарь, и в список
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    addToDictionary(formattedProduct)
                                    updateList { shoppingList.add(0, ShoppingItem(formattedProduct)) }
                                    searchQuery = ""
                                }
                                .padding(16.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "В список и в словарь: «$formattedProduct»",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Вариант 2: Только в список (разовая покупка)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    updateList { shoppingList.add(0, ShoppingItem(formattedProduct)) }
                                    searchQuery = ""
                                }
                                .padding(16.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Только в список: «$formattedProduct»",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(shoppingList, key = { it.name + it.hashCode() }) { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            updateList {
                                val index = shoppingList.indexOf(item)
                                if (index != -1) shoppingList[index] = item.copy(isBought = !item.isBought)
                            }
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = item.isBought,
                        onCheckedChange = { checked ->
                            updateList {
                                val index = shoppingList.indexOf(item)
                                if (index != -1) shoppingList[index] = item.copy(isBought = checked)
                            }
                        }
                    )
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textDecoration = if (item.isBought) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    )
                    IconButton(onClick = { updateList { shoppingList.remove(item) } }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Удалить",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                Divider()
            }
        }
    }
    if (showDictionaryDialog) {
        var newDictWord by remember { mutableStateOf("") }
        val sortedDict = dictionary.sorted()

        AlertDialog(
            onDismissRequest = { showDictionaryDialog = false },
            title = { Text("Мой словарь") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Поле для добавления нового слова прямо в словарь
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = newDictWord,
                            onValueChange = { newDictWord = it },
                            placeholder = { Text("Новое слово...") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                val formatted = newDictWord.trim().replaceFirstChar { it.uppercase() }
                                if (formatted.isNotEmpty() && dictionary.none { it.equals(formatted, ignoreCase = true) }) {
                                    addToDictionary(formatted)
                                    newDictWord = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Добавить в словарь")
                        }
                    }

                    Divider()

                    // Список всех слов в словаре с кнопкой удаления
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(sortedDict, key = { it }) { word ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                            ) {
                                Text(text = word, modifier = Modifier.weight(1f))
                                IconButton(onClick = {
                                    dictionary.remove(word)
                                    storage.saveDictionary(dictionary.toList())
                                }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Удалить из словаря",
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDictionaryDialog = false }) {
                    Text("Готово")
                }
            }
        )
    }
}