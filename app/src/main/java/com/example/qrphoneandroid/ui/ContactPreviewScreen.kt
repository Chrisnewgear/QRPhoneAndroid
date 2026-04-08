package com.example.qrphoneandroid.ui

import android.Manifest
import android.content.ContentProviderOperation
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.qrphoneandroid.R
import com.example.qrphoneandroid.ui.theme.QRDeepBlue
import com.example.qrphoneandroid.ui.theme.QRWhite
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactPreviewScreen(encodedData: String, navController: NavController) {
    val context = LocalContext.current
    val rawData = remember { URLDecoder.decode(encodedData, "UTF-8") }
    val parts = remember { rawData.split("\n") }

    val firstName = parts.getOrElse(0) { "" }
    val lastName = parts.getOrElse(1) { "" }
    val phone = parts.getOrElse(2) { "" }
    val email = parts.getOrNull(3)?.takeIf { it.isNotEmpty() }

    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val readGranted = permissions[Manifest.permission.READ_CONTACTS] ?: false
        val writeGranted = permissions[Manifest.permission.WRITE_CONTACTS] ?: false

        if (writeGranted) {
            val result = saveContact(context, firstName, lastName, phone, email, readGranted)
            if (result.first) {
                Toast.makeText(context, result.second, Toast.LENGTH_SHORT).show()
                navController.navigate("content") {
                    popUpTo("content") { inclusive = true }
                }
            } else {
                statusMessage = result.second
                isError = true
            }
        } else {
            statusMessage = context.getString(R.string.contacts_permission_denied)
            isError = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.contact_preview_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = QRDeepBlue,
                    titleContentColor = QRWhite,
                    navigationIconContentColor = QRWhite
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$firstName $lastName",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = phone, fontSize = 16.sp)
                    email?.let { Text(text = it, fontSize = 14.sp) }
                }
            }

            statusMessage?.let { msg ->
                Text(
                    text = msg,
                    color = if (isError) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    val hasWritePermission = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.WRITE_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED
                    val hasReadPermission = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.READ_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasWritePermission) {
                        val result = saveContact(context, firstName, lastName, phone, email, hasReadPermission)
                        if (result.first) {
                            Toast.makeText(context, result.second, Toast.LENGTH_SHORT).show()
                            navController.navigate("content") {
                                popUpTo("content") { inclusive = true }
                            }
                        } else {
                            statusMessage = result.second
                            isError = true
                        }
                    } else {
                        contactsPermissionLauncher.launch(
                            arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = QRDeepBlue)
            ) {
                Text(
                    text = stringResource(R.string.btn_save_contact),
                    color = QRWhite,
                    fontSize = 16.sp
                )
            }
        }
    }
}

private fun saveContact(
    context: Context,
    firstName: String,
    lastName: String,
    phone: String,
    email: String?,
    canRead: Boolean
): Pair<Boolean, String> {
    if (canRead) {
        val cursor: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?",
            arrayOf(phone),
            null
        )
        val exists = (cursor?.count ?: 0) > 0
        cursor?.close()

        if (exists) {
            return Pair(false, context.getString(R.string.contact_already_exists))
        }
    }

    val ops = ArrayList<ContentProviderOperation>()

    ops.add(
        ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
            .build()
    )
    ops.add(
        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
            )
            .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)
            .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName)
            .build()
    )
    ops.add(
        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            )
            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
            .withValue(
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
            )
            .build()
    )
    email?.let {
        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                )
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, it)
                .withValue(
                    ContactsContract.CommonDataKinds.Email.TYPE,
                    ContactsContract.CommonDataKinds.Email.TYPE_WORK
                )
                .build()
        )
    }

    return try {
        context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
        Pair(true, context.getString(R.string.contact_saved_success))
    } catch (e: Exception) {
        Pair(false, context.getString(R.string.contact_save_error))
    }
}
