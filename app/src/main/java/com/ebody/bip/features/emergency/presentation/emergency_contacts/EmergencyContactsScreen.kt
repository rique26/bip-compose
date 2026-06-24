package com.ebody.bip.features.emergency.presentation.emergency_contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ebody.bip.features.emergency.domain.model.EmergencyContact
import com.ebody.bip.features.emergency.presentation.emergency_contacts.components.AddContactBottomSheetContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyContactsScreen(
    onNavigateToAddContact: () -> Unit,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showInfoDialog by remember { mutableStateOf(false) }

    var isBottomSheetVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // RF04 - Caixa de diálogo detalhada e instrutiva
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Text("Como funciona?", fontWeight = FontWeight.Bold, color = Color.DarkGray)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Adicione seus contatos de emergência usando o botão de mais (+) no canto inferior.",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                    Text(
                        "Toque nos ícones ao lado do nome para escolher por onde o alerta será enviado:",
                        fontSize = 15.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Whatsapp, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(24.dp))
                        Text("Verde = Alertas via WhatsApp", fontSize = 14.sp, color = Color.DarkGray)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Message, contentDescription = null, tint = Color(0xFF29B6F6), modifier = Modifier.size(24.dp))
                        Text("Azul = Alertas via SMS", fontSize = 14.sp, color = Color.DarkGray)
                    }
                    Text(
                        "Você pode marcar ambos ou apenas um canal de comunicação por contato.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Entendi", fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                }
            }
        )
    }

    if (isBottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { isBottomSheetVisible = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            AddContactBottomSheetContent(
                onSaveClicked = { name, phone ->
                    viewModel.onEvent(ContactsEvent.SaveContact(name, phone))
                    isBottomSheetVisible = false
                },
                onCancelClicked = {
                    isBottomSheetVisible = false
                }
            )
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Contatos de Emergência", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Informações", tint = Color.Gray)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isBottomSheetVisible = true },
                containerColor = Color(0xFFC62828),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar contato")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {

            // Subtítulo descritivo de UX nativo na tela (Sem precisar de cliques)
            item {
                Text(
                    text = "Toque nos ícones para definir como o alerta será enviado para cada contato:",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            items(
                items = uiState.contacts,
                key = { it.id }
            ) { contact ->
                ContactItem(
                    contact = contact,
                    onToggleWhatsApp = { viewModel.onEvent(ContactsEvent.ToggleWhatsApp(contact.id)) },
                    onToggleSms = { viewModel.onEvent(ContactsEvent.ToggleSms(contact.id)) }
                )
            }
        }
    }
}

@Composable
fun ContactItem(
    contact: EmergencyContact,
    onToggleWhatsApp: () -> Unit,
    onToggleSms: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFE57373),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = contact.name.firstOrNull()?.uppercase() ?: "?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = contact.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Text(
                text = contact.phoneNumber,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        IconButton(onClick = onToggleWhatsApp) {
            Icon(
                imageVector = Icons.Default.Whatsapp,
                contentDescription = "Toggle WhatsApp",
                tint = if (contact.isWhatsAppEnabled) Color(0xFF25D366) else Color.LightGray,
                modifier = Modifier.size(28.dp)
            )
        }

        IconButton(onClick = onToggleSms) {
            Icon(
                imageVector = Icons.Default.Message,
                contentDescription = "Toggle SMS",
                tint = if (contact.isSmsEnabled) Color(0xFF29B6F6) else Color.LightGray,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}