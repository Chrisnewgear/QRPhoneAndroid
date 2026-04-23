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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.qrphoneandroid.ui.theme.NeumorphBase
import com.example.qrphoneandroid.ui.theme.QROnBackground
import com.example.qrphoneandroid.ui.theme.QROnSurfaceVariant
import com.example.qrphoneandroid.ui.theme.QRPrimary

@Composable
fun ContactPreviewScreen(encodedData: String, navController: NavController) {
    val context = LocalContext.current
    // Navigation component already URL-decodes query parameters via Uri.getQueryParameter()
    val parts = remember { encodedData.split("\n") }

    val firstName = parts.getOrElse(0) { "" }
    val lastName  = parts.getOrElse(1) { "" }
    val phone     = parts.getOrElse(2) { "" }.trim()
    val email     = parts.getOrNull(3)?.takeIf { it.isNotEmpty() }

    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isError       by remember { mutableStateOf(false) }

    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val readGranted  = permissions[Manifest.permission.READ_CONTACTS]  ?: false
        val writeGranted = permissions[Manifest.permission.WRITE_CONTACTS] ?: false

        if (writeGranted) {
            val result = saveContact(context, firstName, lastName, phone, email, readGranted)
            if (result.first) {
                Toast.makeText(context, result.second, Toast.LENGTH_SHORT).show()
                navController.navigate("content") { popUpTo("content") { inclusive = true } }
            } else {
                statusMessage = result.second
                isError = true
            }
        } else {
            statusMessage = context.getString(R.string.contacts_permission_denied)
            isError = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeumorphBase),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 48.dp),
        ) {
            // ── Back button ────────────────────────────────────────────────
            val backInteraction = remember { MutableInteractionSource() }
            val backPressed by backInteraction.collectIsPressedAsState()

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .then(
                        if (backPressed) Modifier.neumorphPressed(cornerRadius = 24.dp, elevation = 4.dp)
                        else             Modifier.neumorphRaised(cornerRadius = 24.dp, elevation = 6.dp)
                    )
                    .clickable(
                        interactionSource = backInteraction,
                        indication        = null,
                        onClick           = { navController.popBackStack() },
                    ),
            ) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint               = QRPrimary,
                    modifier           = Modifier.size(22.dp),
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Header ─────────────────────────────────────────────────────
            Text(
                text       = stringResource(R.string.contact_preview_title),
                color      = QRPrimary,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(bottom = 6.dp),
            )
            Text(
                text     = stringResource(R.string.contact_preview_subtitle),
                color    = QROnSurfaceVariant,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 32.dp),
            )

            // ── Contact info card ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .neumorphRaised(cornerRadius = 20.dp, elevation = 10.dp),
            ) {
                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    // Full name
                    Text(
                        text       = "$firstName $lastName",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color      = QRPrimary,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Phone row
                    ContactInfoRow(
                        label = stringResource(R.string.label_phone_field),
                        value = phone,
                    )

                    // Email row (optional)
                    email?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(
                            modifier  = Modifier.padding(vertical = 8.dp),
                            thickness = 0.5.dp,
                            color     = QROnSurfaceVariant.copy(alpha = 0.2f),
                        )
                        ContactInfoRow(
                            label = stringResource(R.string.label_email_field),
                            value = it,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Status message ─────────────────────────────────────────────
            statusMessage?.let { msg ->
                Text(
                    text     = msg,
                    color    = if (isError) MaterialTheme.colorScheme.error
                               else        MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Save button ────────────────────────────────────────────────
            NeumorphPrimaryButton(
                text     = stringResource(R.string.btn_save_contact),
                onClick  = {
                    val hasWrite = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.WRITE_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED
                    val hasRead = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.READ_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasWrite) {
                        val result = saveContact(context, firstName, lastName, phone, email, hasRead)
                        if (result.first) {
                            Toast.makeText(context, result.second, Toast.LENGTH_SHORT).show()
                            navController.navigate("content") { popUpTo("content") { inclusive = true } }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun ContactInfoRow(label: String, value: String) {
    Row(
        modifier            = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment   = Alignment.CenterVertically,
    ) {
        Text(
            text       = label,
            fontSize   = 13.sp,
            color      = QROnSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier   = Modifier.weight(0.35f),
        )
        Text(
            text     = value,
            fontSize = 15.sp,
            color    = QROnBackground,
            modifier = Modifier.weight(0.65f),
        )
    }
}

private val PHONE_REGEX = Regex("^\\+[1-9][0-9]{6,24}$")
private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

private fun saveContact(
    context: Context,
    firstName: String,
    lastName: String,
    phone: String,
    email: String?,
    canRead: Boolean,
): Pair<Boolean, String> {
    val safeFirst = firstName.replace(Regex("[\\p{Cntrl}]"), "").trim()
    val safeLast  = lastName.replace(Regex("[\\p{Cntrl}]"), "").trim()
    if (safeFirst.isEmpty() || safeLast.isEmpty() || !PHONE_REGEX.matches(phone)) {
        return Pair(false, context.getString(R.string.contact_save_error))
    }
    if (email != null && !EMAIL_REGEX.matches(email)) {
        return Pair(false, context.getString(R.string.contact_save_error))
    }

    if (canRead) {
        val cursor: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?",
            arrayOf(phone),
            null,
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
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
            )
            .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, safeFirst)
            .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, safeLast)
            .build()
    )
    ops.add(
        ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
            )
            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
            .withValue(
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
            )
            .build()
    )
    email?.let {
        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                )
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, it)
                .withValue(
                    ContactsContract.CommonDataKinds.Email.TYPE,
                    ContactsContract.CommonDataKinds.Email.TYPE_WORK,
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
