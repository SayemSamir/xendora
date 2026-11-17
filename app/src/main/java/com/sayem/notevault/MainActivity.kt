package com.sayem.notevault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sayem.notevault.data.*
import com.sayem.notevault.ui.*
import java.util.UUID

private val Accent = Color(0xFF9B8CFF)
private val PageDark = Color(0xFF10131D)
private val CardDark = Color(0xFF1B2030)
private val noteColors = listOf(
    0xFF242938, 0xFF44304A, 0xFF25443F, 0xFF493B27,
    0xFF293F5A, 0xFF4A2D35, 0xFF30334D, 0xFF3B3B3B
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseProvider.get(applicationContext)
        val repo = NoteRepository(db.noteDao(), db.folderDao())
        val factory = NotesViewModelFactory(repo)
        setContent {
            val vm: NotesViewModel = viewModel(factory = factory)
            NoteVaultTheme { NoteVaultScreen(vm) }
        }
    }
}

@Composable
private fun NoteVaultTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent,
            onPrimary = Color.White,
            background = PageDark,
            surface = CardDark,
            onSurface = Color(0xFFF2F1FA),
            secondary = Color(0xFF74DCC8)
        ),
        content = content
    )
}

@Composable
private fun NoteVaultScreen(vm: NotesViewModel) {
    val state by vm.uiState.collectAsState()
    var editorNote by remember { mutableStateOf<NoteEntity?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var showFolderDialog by remember { mutableStateOf(false) }
    var folderName by remember { mutableStateOf("") }
    var showFolders by remember { mutableStateOf(false) }
    var lightMode by remember { mutableStateOf(false) }

    BackHandler(showEditor || showFolders) {
        if (showEditor) {
            showEditor = false
        } else {
            showFolders = false
        }
    }

    Scaffold(
        containerColor = if (lightMode) Color(0xFFF5F5FA) else PageDark,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { editorNote = null; showEditor = true },
                containerColor = Accent,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("New note", fontWeight = FontWeight.SemiBold) }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("NOTEvault", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("Your thoughts, protected.", color = if (lightMode) Color(0xFF191A22) else Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                    Text("BY SAYEM UDDIN SAMIR", color = Color.Gray, fontSize = 9.sp, letterSpacing = 1.4.sp)
                }
                IconButton(onClick = { lightMode = !lightMode }) {
                    Icon(if (lightMode) Icons.Default.DarkMode else Icons.Default.LightMode, "Toggle theme",
                        tint = if (lightMode) Color.DarkGray else Accent)
                }
                IconButton(onClick = { showFolders = true }) {
                    Icon(Icons.Default.Folder, "Folders", tint = Accent)
                }
            }
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = vm::setSearchQuery,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search title or note content…", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Accent) },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { vm.setSearchQuery("") }) {
                            Icon(Icons.Default.Close, "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = if (lightMode) Color.LightGray else Color(0xFF34394A),
                    focusedTextColor = if (lightMode) Color.Black else Color.White,
                    unfocusedTextColor = if (lightMode) Color.Black else Color.White
                )
            )
            Spacer(Modifier.height(15.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("All notes", state.notes.size.toString(), Modifier.weight(1f))
                StatCard("Pinned", state.notes.count { it.isPinned }.toString(), Modifier.weight(1f))
                StatCard("Favorites", state.notes.count { it.isFavorite }.toString(), Modifier.weight(1f))
            }
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Your notes", color = if (lightMode) Color.Black else Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("${state.visibleNotes.size} items", color = Color.Gray, fontSize = 11.sp)
            }
            Spacer(Modifier.height(10.dp))
            if (state.folders.isNotEmpty()) {
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { FilterChip(selected = state.selectedFolderId == null, onClick = { vm.selectFolder(null) }, label = { Text("All") }) }
                    items(state.folders) { folder ->
                        FilterChip(
                            selected = state.selectedFolderId == folder.id,
                            onClick = { vm.selectFolder(if (state.selectedFolderId == folder.id) null else folder.id) },
                            label = { Text(folder.name) }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            if (state.visibleNotes.isEmpty()) {
                EmptyNotes(lightMode, state.searchQuery.isNotBlank())
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    items(state.visibleNotes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            lightMode = lightMode,
                            folderName = state.folders.firstOrNull { it.id == note.folderId }?.name ?: "Personal",
                            onOpen = { editorNote = note; showEditor = true },
                            onPin = { vm.togglePin(note) },
                            onFavorite = { vm.toggleFavorite(note) },
                            onDelete = { vm.deleteNote(note.id) }
                        )
                    }
                }
            }
        }
    }

    if (showEditor) {
        NoteEditorDialog(
            initial = editorNote,
            folders = state.folders,
            onDismiss = { showEditor = false; editorNote = null },
            onAutoSave = { draftId, title, content, folderId, color ->
                vm.saveDraft(draftId, title, content, folderId, color)
            },
            onSave = {
                showEditor = false
                editorNote = null
            }
        )
    }

    if (showFolderDialog) {
        AlertDialog(
            onDismissRequest = { showFolderDialog = false },
            title = { Text("Create folder") },
            text = {
                OutlinedTextField(folderName, { folderName = it }, label = { Text("Folder name") }, singleLine = true)
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.createFolder(folderName)
                    folderName = ""
                    showFolderDialog = false
                }) { Text("Create") }
            },
            dismissButton = { TextButton(onClick = { showFolderDialog = false }) { Text("Cancel") } }
        )
    }

    if (showFolders) {
        AlertDialog(
            onDismissRequest = { showFolders = false },
            title = { Text("Your folders") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (state.folders.isEmpty()) Text("No folders yet.")
                    state.folders.forEach { folder ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(folder.name, Modifier.weight(1f))
                            IconButton(onClick = { vm.deleteFolder(folder.id) }) {
                                Icon(Icons.Default.DeleteOutline, "Delete folder", tint = Color(0xFFFF7777))
                            }
                        }
                    }
                    OutlinedButton(onClick = { showFolders = false; showFolderDialog = true }) {
                        Icon(Icons.Default.CreateNewFolder, null)
                        Spacer(Modifier.width(6.dp))
                        Text("New folder")
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showFolders = false }) { Text("Done") } }
        )
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier, shape = RoundedCornerShape(15.dp), color = CardDark) {
        Column(Modifier.padding(12.dp)) {
            Text(value, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Color.LightGray, fontSize = 10.sp)
        }
    }
}

@Composable
private fun NoteCard(
    note: NoteEntity,
    lightMode: Boolean,
    folderName: String,
    onOpen: () -> Unit,
    onPin: () -> Unit,
    onFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    val background = if (lightMode) Color(note.colorArgb).copy(alpha = 0.12f) else Color(note.colorArgb)
    Card(
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Column(Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(note.title.ifBlank { "Untitled note" }, Modifier.weight(1f), color = if (lightMode) Color(0xFF20202A) else Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (note.isPinned) Icon(Icons.Default.PushPin, null, tint = Accent, modifier = Modifier.size(17.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(note.content.ifBlank { "No additional text" }, color = if (lightMode) Color.DarkGray else Color(0xFFCFD1DB), maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 13.sp)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(folderName, color = Accent, fontSize = 11.sp, modifier = Modifier.weight(1f))
                IconButton(onClick = onPin, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.PushPin, "Pin note", tint = if (note.isPinned) Accent else Color.Gray, modifier = Modifier.size(17.dp))
                }
                IconButton(onClick = onFavorite, modifier = Modifier.size(34.dp)) {
                    Icon(if (note.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, "Favorite note", tint = if (note.isFavorite) Color(0xFFFF7B9C) else Color.Gray, modifier = Modifier.size(17.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.DeleteOutline, "Move to trash", tint = Color.Gray, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyNotes(lightMode: Boolean, searching: Boolean) {
    Column(
        Modifier.fillMaxWidth().padding(top = 55.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(if (searching) Icons.Default.SearchOff else Icons.Default.NoteAdd, null, tint = Accent, modifier = Modifier.size(45.dp))
        Spacer(Modifier.height(12.dp))
        Text(if (searching) "No matching notes" else "Your space is waiting", color = if (lightMode) Color.DarkGray else Color.White, fontWeight = FontWeight.SemiBold)
        Text(if (searching) "Try another keyword." else "Tap New note to capture your first idea.", color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
private fun NoteEditorDialog(
    initial: NoteEntity?,
    folders: List<FolderEntity>,
    onDismiss: () -> Unit,
    onAutoSave: (String, String, String, String?, Long) -> Unit,
    onSave: () -> Unit
) {
    val draftId = remember(initial?.id) { initial?.id ?: UUID.randomUUID().toString() }
    var title by remember(initial?.id) { mutableStateOf(initial?.title.orEmpty()) }
    var content by remember(initial?.id) { mutableStateOf(initial?.content.orEmpty()) }
    var folderId by remember(initial?.id) { mutableStateOf(initial?.folderId) }
    var color by remember(initial?.id) { mutableLongStateOf(initial?.colorArgb ?: noteColors.first()) }
    // Set only by real user edits. Opening and closing a note without changes must not
    // save, otherwise updatedAt changes and the note jumps to the top of the list.
    var edited by remember(initial?.id) { mutableStateOf(false) }
    var folderMenu by remember { mutableStateOf(false) }

    LaunchedEffect(draftId, title, content, folderId, color) {
        if (edited) {
            delay(700)
            onAutoSave(draftId, title, content, folderId, color)
        }
    }

    // Runs however the editor closes (Done, Cancel, back, outside tap). It saves an edit that is
    // still inside the 700 ms debounce window, so the last keystrokes are never dropped.
    DisposableEffect(draftId) {
        onDispose {
            if (edited) onAutoSave(draftId, title, content, folderId, color)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Create a note" else "Edit note", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                Modifier.fillMaxWidth().heightIn(max = 520.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(title, { title = it; edited = true }, modifier = Modifier.fillMaxWidth(), label = { Text("Title") }, singleLine = true)
                OutlinedTextField(content, { content = it; edited = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 130.dp), label = { Text("Start writing…") }, minLines = 5)
                Text("NOTE COLOR", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    noteColors.forEach { colorValue ->
                        Box(
                            Modifier.size(27.dp).clip(CircleShape)
                                .background(Color(colorValue))
                                .clickable { color = colorValue; edited = true },
                            contentAlignment = Alignment.Center
                        ) {
                            if (color == colorValue) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Box {
                    OutlinedButton(onClick = { folderMenu = true }) {
                        Icon(Icons.Default.Folder, null)
                        Spacer(Modifier.width(6.dp))
                        Text(folders.firstOrNull { it.id == folderId }?.name ?: "Personal folder")
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                    DropdownMenu(expanded = folderMenu, onDismissRequest = { folderMenu = false }) {
                        DropdownMenuItem(text = { Text("Personal (no folder)") }, onClick = { folderId = null; edited = true; folderMenu = false })
                        folders.forEach { folder ->
                            DropdownMenuItem(text = { Text(folder.name) }, onClick = { folderId = folder.id; edited = true; folderMenu = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onSave, enabled = title.isNotBlank() || content.isNotBlank()) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(6.dp))
                Text("Done")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
