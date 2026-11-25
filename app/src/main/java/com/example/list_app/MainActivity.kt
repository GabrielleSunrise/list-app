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
import androidx.compose.material.icons.filled.Check
import java.util.UUID
import android.graphics.Color
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars

data class ShoppingItem(val name: String, val isBought: Boolean = false, val id: String = UUID.randomUUID().toString())

class StorageHelper(context: Context) {
    private val prefs = context.getSharedPreferences("shopping_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveList(list: List<ShoppingItem>) {
        prefs.edit().putString("shopping_list", gson.toJson(list)).apply()
    }

    fun loadList(): List<ShoppingItem> {
        val json = prefs.getString("shopping_list", null) ?: return emptyList()
        val type = object : TypeToken<List<ShoppingItem>>() {}.type
        val loaded: List<ShoppingItem> = gson.fromJson(json, type) ?: emptyList()
        // Страховка: если в памяти лежат старые товары без id, Gson запишет туда null.
        // Эта строка автоматически сгенерирует им новые id.
        return loaded.map { if (it.id == null) it.copy(id = UUID.randomUUID().toString()) else it }
    }

    fun saveDictionary(dict: List<String>) {
        prefs.edit().putString("dictionary", gson.toJson(dict)).apply()
    }

    fun loadDictionary(): List<String> {
        val json = prefs.getString("dictionary", null)
        if (json == null) {
            return listOf("Молоко", "Хлеб", "Яйца", "Яблоко", "Бананы", "Кофе", "Сыр салатный", "Сыр для нарезки", "Сырок плавленный", "Груша", "Баклажан", "Огурцы",
                "Помидоры", "Морковь", "Морковь корейская", "Лук красный", "Лук белый", "Торт", "Колбаса копчёная", "Колбаса варёная", "Ветчина", "Салат зеленый",
                "Шпинат", "Ананас в банке", "Курица копчёная", "Горошек", "Кукуруза", "Шпроты", "Говядина", "Свинина", "Курица", "Курогрудь", "Пекинка",
                "Апельсин", "Картошка", "Лимон", "Лайм", "Виноград", "Майонез", "Соль", "Сахар", "Масло подсолнечное", "Масло оливковое", "Лимонный сок",
                "Кетчуп", "Кепчук", "Перец", "Фисташки", "Кешью", "Арахис", "Сгущенка", "Сливки", "Йогурт", "Творог", "Рыба", "Минтай", "Сойка", "Фарш",
                "Тесто", "Капуста", "Киви", "Кошкорм сухой", "Кошкорм влажный", "Феликс", "Фэликс", "Наполнитель", "Грибы", "Шампиньоны", "Вешенки", "Спаржа",
                "Спаржа соевая", "Шоколад", "Сметана", "Масло сливочное", "Карпаччо", "Кабачок", "Тыква", "Сливы", "Персики", "Нектарины", "Кокосовое молоко",
                "Кинза", "Петрушка", "Укроп", "Рис", "Гречка", "Пшено", "Чечевица", "Макароны", "Лапша", "Бичпакет", "Мука", "Томатная паста", "Томаты", "Черри",
                "Брокколи", "Капуста цветная", "Капуста брюссельская", "Овощная смесь зам.", "Куриные сердца", "Куриная печень", "Куриные желудки",
                "Индейка", "Утка", "Креветки", "Кальмары", "Мидии", "Фасоль в банке", "Желатин", "Агар", "Минералка", "Газировка", "Сок", "Вода без газа",
                "Печенье", "Конфеты", "Мандарины", "Черемша", "Чеснок", "Огурцы маринованные", "Кукуруза маринованная", "Маш", "Горох (крупа)", "Ребра копчёные",
                "Оливки зеленые", "Оливки черные", "Маслины", "Редис", "Дайкон", "Редька", "Сельдерей", "Каперсы", "Анчоусы", "Сайра", "Томаты в соб. соку",
                "Тунец замороженный", "Тунец консерва", "Лук зелёный", "Форель", "Кета", "Горбуша", "Скумбрия", "Мексиканская смесь", "Черешня", "Абрикосы",
                "Авокадо", "Квас", "Рикотта", "Маскарпоне", "Салфетки", "Салфетки влажные", "Свёкла", "Имбирь", "Овсянка", "Булгур", "Сосиски"
            )
        }
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
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

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp)) {
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 16.dp
            )
        ) {
            items(shoppingList, key = { it.id }) { item ->
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
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            title = { Text("Мой словарь") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
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

                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(sortedDict, key = { it }) { word ->
                            val isInShoppingList = shoppingList.any { it.name.equals(word, ignoreCase = true) }

                            // Вынесли логику переключения в отдельную функцию для чистоты
                            val toggleItemInList = {
                                updateList {
                                    if (isInShoppingList) {
                                        shoppingList.removeAll { it.name.equals(word, ignoreCase = true) }
                                    } else {
                                        shoppingList.add(0, ShoppingItem(name = word))
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { toggleItemInList() }
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = word,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 4.dp),
                                    color = if (isInShoppingList) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                                )

                                // Кнопка-переключатель (+ или галочка)
                                IconButton(onClick = { toggleItemInList() }) {
                                    if (isInShoppingList) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Убрать из списка покупок",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Добавить в список покупок",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                // Кнопка полного удаления из словаря
                                IconButton(onClick = {
                                    dictionary.remove(word)
                                    storage.saveDictionary(dictionary.toList())
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
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