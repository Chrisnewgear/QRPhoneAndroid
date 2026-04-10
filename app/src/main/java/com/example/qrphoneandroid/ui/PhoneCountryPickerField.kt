package com.example.qrphoneandroid.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import com.example.qrphoneandroid.ui.theme.NeumorphBase
import com.example.qrphoneandroid.ui.theme.NeumorphDark
import com.example.qrphoneandroid.ui.theme.QROnBackground
import com.example.qrphoneandroid.ui.theme.QROnSurfaceVariant
import com.example.qrphoneandroid.ui.theme.QRPrimary

// ── Country model ─────────────────────────────────────────────────────────────

data class Country(
    val isoCode: String,   // "US"
    val name: String,      // "United States"
    val dialCode: String,  // "+1"
)

/** Converts a 2-letter ISO 3166-1 alpha-2 code into the corresponding flag emoji. */
val String.flagEmoji: String
    get() {
        if (length != 2) return "🏳"
        val base = 0x1F1E6 - 'A'.code
        return String(Character.toChars(base + this[0].uppercaseChar().code)) +
                String(Character.toChars(base + this[1].uppercaseChar().code))
    }

// ── Country list (sorted alphabetically) ─────────────────────────────────────

val allCountries: List<Country> = listOf(
    Country("AF", "Afghanistan", "+93"),
    Country("AL", "Albania", "+355"),
    Country("DZ", "Algeria", "+213"),
    Country("AD", "Andorra", "+376"),
    Country("AO", "Angola", "+244"),
    Country("AG", "Antigua & Barbuda", "+1268"),
    Country("AR", "Argentina", "+54"),
    Country("AM", "Armenia", "+374"),
    Country("AU", "Australia", "+61"),
    Country("AT", "Austria", "+43"),
    Country("AZ", "Azerbaijan", "+994"),
    Country("BS", "Bahamas", "+1242"),
    Country("BH", "Bahrain", "+973"),
    Country("BD", "Bangladesh", "+880"),
    Country("BB", "Barbados", "+1246"),
    Country("BY", "Belarus", "+375"),
    Country("BE", "Belgium", "+32"),
    Country("BZ", "Belize", "+501"),
    Country("BJ", "Benin", "+229"),
    Country("BT", "Bhutan", "+975"),
    Country("BO", "Bolivia", "+591"),
    Country("BA", "Bosnia & Herzegovina", "+387"),
    Country("BW", "Botswana", "+267"),
    Country("BR", "Brazil", "+55"),
    Country("BN", "Brunei", "+673"),
    Country("BG", "Bulgaria", "+359"),
    Country("BF", "Burkina Faso", "+226"),
    Country("BI", "Burundi", "+257"),
    Country("CV", "Cabo Verde", "+238"),
    Country("KH", "Cambodia", "+855"),
    Country("CM", "Cameroon", "+237"),
    Country("CA", "Canada", "+1"),
    Country("CF", "Central African Republic", "+236"),
    Country("TD", "Chad", "+235"),
    Country("CL", "Chile", "+56"),
    Country("CN", "China", "+86"),
    Country("CO", "Colombia", "+57"),
    Country("KM", "Comoros", "+269"),
    Country("CG", "Congo", "+242"),
    Country("CR", "Costa Rica", "+506"),
    Country("HR", "Croatia", "+385"),
    Country("CU", "Cuba", "+53"),
    Country("CY", "Cyprus", "+357"),
    Country("CZ", "Czech Republic", "+420"),
    Country("DK", "Denmark", "+45"),
    Country("DJ", "Djibouti", "+253"),
    Country("DM", "Dominica", "+1767"),
    Country("DO", "Dominican Republic", "+1809"),
    Country("EC", "Ecuador", "+593"),
    Country("EG", "Egypt", "+20"),
    Country("SV", "El Salvador", "+503"),
    Country("GQ", "Equatorial Guinea", "+240"),
    Country("ER", "Eritrea", "+291"),
    Country("EE", "Estonia", "+372"),
    Country("SZ", "Eswatini", "+268"),
    Country("ET", "Ethiopia", "+251"),
    Country("FJ", "Fiji", "+679"),
    Country("FI", "Finland", "+358"),
    Country("FR", "France", "+33"),
    Country("GA", "Gabon", "+241"),
    Country("GM", "Gambia", "+220"),
    Country("GE", "Georgia", "+995"),
    Country("DE", "Germany", "+49"),
    Country("GH", "Ghana", "+233"),
    Country("GR", "Greece", "+30"),
    Country("GD", "Grenada", "+1473"),
    Country("GT", "Guatemala", "+502"),
    Country("GN", "Guinea", "+224"),
    Country("GW", "Guinea-Bissau", "+245"),
    Country("GY", "Guyana", "+592"),
    Country("HT", "Haiti", "+509"),
    Country("HN", "Honduras", "+504"),
    Country("HU", "Hungary", "+36"),
    Country("IS", "Iceland", "+354"),
    Country("IN", "India", "+91"),
    Country("ID", "Indonesia", "+62"),
    Country("IR", "Iran", "+98"),
    Country("IQ", "Iraq", "+964"),
    Country("IE", "Ireland", "+353"),
    Country("IL", "Israel", "+972"),
    Country("IT", "Italy", "+39"),
    Country("JM", "Jamaica", "+1876"),
    Country("JP", "Japan", "+81"),
    Country("JO", "Jordan", "+962"),
    Country("KZ", "Kazakhstan", "+7"),
    Country("KE", "Kenya", "+254"),
    Country("KI", "Kiribati", "+686"),
    Country("KW", "Kuwait", "+965"),
    Country("KG", "Kyrgyzstan", "+996"),
    Country("LA", "Laos", "+856"),
    Country("LV", "Latvia", "+371"),
    Country("LB", "Lebanon", "+961"),
    Country("LS", "Lesotho", "+266"),
    Country("LR", "Liberia", "+231"),
    Country("LY", "Libya", "+218"),
    Country("LI", "Liechtenstein", "+423"),
    Country("LT", "Lithuania", "+370"),
    Country("LU", "Luxembourg", "+352"),
    Country("MG", "Madagascar", "+261"),
    Country("MW", "Malawi", "+265"),
    Country("MY", "Malaysia", "+60"),
    Country("MV", "Maldives", "+960"),
    Country("ML", "Mali", "+223"),
    Country("MT", "Malta", "+356"),
    Country("MH", "Marshall Islands", "+692"),
    Country("MR", "Mauritania", "+222"),
    Country("MU", "Mauritius", "+230"),
    Country("MX", "Mexico", "+52"),
    Country("FM", "Micronesia", "+691"),
    Country("MD", "Moldova", "+373"),
    Country("MC", "Monaco", "+377"),
    Country("MN", "Mongolia", "+976"),
    Country("ME", "Montenegro", "+382"),
    Country("MA", "Morocco", "+212"),
    Country("MZ", "Mozambique", "+258"),
    Country("MM", "Myanmar", "+95"),
    Country("NA", "Namibia", "+264"),
    Country("NR", "Nauru", "+674"),
    Country("NP", "Nepal", "+977"),
    Country("NL", "Netherlands", "+31"),
    Country("NZ", "New Zealand", "+64"),
    Country("NI", "Nicaragua", "+505"),
    Country("NE", "Niger", "+227"),
    Country("NG", "Nigeria", "+234"),
    Country("MK", "North Macedonia", "+389"),
    Country("NO", "Norway", "+47"),
    Country("OM", "Oman", "+968"),
    Country("PK", "Pakistan", "+92"),
    Country("PW", "Palau", "+680"),
    Country("PA", "Panama", "+507"),
    Country("PG", "Papua New Guinea", "+675"),
    Country("PY", "Paraguay", "+595"),
    Country("PE", "Peru", "+51"),
    Country("PH", "Philippines", "+63"),
    Country("PL", "Poland", "+48"),
    Country("PT", "Portugal", "+351"),
    Country("QA", "Qatar", "+974"),
    Country("RO", "Romania", "+40"),
    Country("RU", "Russia", "+7"),
    Country("RW", "Rwanda", "+250"),
    Country("KN", "Saint Kitts & Nevis", "+1869"),
    Country("LC", "Saint Lucia", "+1758"),
    Country("VC", "Saint Vincent & Grenadines", "+1784"),
    Country("WS", "Samoa", "+685"),
    Country("SM", "San Marino", "+378"),
    Country("ST", "São Tomé & Príncipe", "+239"),
    Country("SA", "Saudi Arabia", "+966"),
    Country("SN", "Senegal", "+221"),
    Country("RS", "Serbia", "+381"),
    Country("SC", "Seychelles", "+248"),
    Country("SL", "Sierra Leone", "+232"),
    Country("SG", "Singapore", "+65"),
    Country("SK", "Slovakia", "+421"),
    Country("SI", "Slovenia", "+386"),
    Country("SB", "Solomon Islands", "+677"),
    Country("SO", "Somalia", "+252"),
    Country("ZA", "South Africa", "+27"),
    Country("SS", "South Sudan", "+211"),
    Country("ES", "Spain", "+34"),
    Country("LK", "Sri Lanka", "+94"),
    Country("SD", "Sudan", "+249"),
    Country("SR", "Suriname", "+597"),
    Country("SE", "Sweden", "+46"),
    Country("CH", "Switzerland", "+41"),
    Country("SY", "Syria", "+963"),
    Country("TW", "Taiwan", "+886"),
    Country("TJ", "Tajikistan", "+992"),
    Country("TZ", "Tanzania", "+255"),
    Country("TH", "Thailand", "+66"),
    Country("TL", "Timor-Leste", "+670"),
    Country("TG", "Togo", "+228"),
    Country("TO", "Tonga", "+676"),
    Country("TT", "Trinidad & Tobago", "+1868"),
    Country("TN", "Tunisia", "+216"),
    Country("TR", "Turkey", "+90"),
    Country("TM", "Turkmenistan", "+993"),
    Country("TV", "Tuvalu", "+688"),
    Country("UG", "Uganda", "+256"),
    Country("UA", "Ukraine", "+380"),
    Country("AE", "United Arab Emirates", "+971"),
    Country("GB", "United Kingdom", "+44"),
    Country("US", "United States", "+1"),
    Country("UY", "Uruguay", "+598"),
    Country("UZ", "Uzbekistan", "+998"),
    Country("VU", "Vanuatu", "+678"),
    Country("VE", "Venezuela", "+58"),
    Country("VN", "Vietnam", "+84"),
    Country("YE", "Yemen", "+967"),
    Country("ZM", "Zambia", "+260"),
    Country("ZW", "Zimbabwe", "+263"),
).sortedBy { it.name }

// ── Composable ────────────────────────────────────────────────────────────────

/**
 * Splits a full international phone number (e.g. "+15551234567") back into a
 * (dialCode, localNumber) pair using [allCountries].
 * The longest matching dial code wins so "+1242" isn't greedily matched by "+1".
 * Falls back to ("+1", "") for blank / unparseable input.
 */
fun parsePhoneNumber(fullNumber: String): Pair<String, String> {
    if (fullNumber.isBlank() || !fullNumber.startsWith("+")) return "+1" to ""
    val country = allCountries
        .filter { fullNumber.startsWith(it.dialCode) }
        .maxByOrNull { it.dialCode.length }
    return if (country != null) {
        country.dialCode to fullNumber.removePrefix(country.dialCode)
    } else {
        "+1" to fullNumber.removePrefix("+")
    }
}

/**
 * A row containing:
 *  - a compact country dropdown (flag + dial code) that is searchable
 *  - a phone number text field
 *
 * The caller receives every change through [onDialCodeChange] and [onPhoneNumberChange].
 * The full E.164-style number can be assembled as `dialCode + phoneNumber`.
 */
@Composable
fun PhoneCountryPickerField(
    dialCode: String,
    phoneNumber: String,
    onDialCodeChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    val selectedCountry = remember(dialCode) {
        allCountries.find { it.dialCode == dialCode } ?: allCountries.first { it.isoCode == "US" }
    }

    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // ── Country selector (tap to open dialog) ────────────────────────────
        Box(modifier = Modifier.width(136.dp)) {
            OutlinedTextField(
                value = "${selectedCountry.isoCode.flagEmoji}  ${selectedCountry.dialCode}",
                onValueChange = {},
                enabled = false,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledContainerColor = NeumorphBase,
                    disabledBorderColor    = NeumorphDark,
                    disabledTextColor      = QROnBackground,
                ),
            )
            // Invisible overlay to capture taps (disabled TextField swallows them)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDialog = true },
            )
        }

        // ── Local phone number ────────────────────────────────────────────────
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { onPhoneNumberChange(it.filter { c -> c.isDigit() }.take(15)) },
            label = { Text(label) },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = NeumorphBase,
                unfocusedContainerColor = NeumorphBase,
                focusedBorderColor      = QRPrimary,
                unfocusedBorderColor    = NeumorphDark,
                focusedLabelColor       = QRPrimary,
                unfocusedLabelColor     = QROnSurfaceVariant,
                cursorColor             = QRPrimary,
                focusedTextColor        = QROnBackground,
                unfocusedTextColor      = QROnBackground,
            ),
        )
    }

    // ── Country picker dialog (full window → keyboard works reliably) ────────
    if (showDialog) {
        CountryPickerDialog(
            onCountrySelected = { country ->
                onDialCodeChange(country.dialCode)
                showDialog = false
            },
            onDismiss = { showDialog = false },
        )
    }
}

@Composable
private fun CountryPickerDialog(
    onCountrySelected: (Country) -> Unit,
    onDismiss: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(200)
        runCatching { searchFocusRequester.requestFocus() }
    }

    val filteredCountries = remember(searchQuery) {
        if (searchQuery.isBlank()) allCountries
        else allCountries.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.dialCode.contains(searchQuery)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = NeumorphBase,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 480.dp),
        ) {
            Column {
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search country…") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .focusRequester(searchFocusRequester),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor   = NeumorphBase,
                        unfocusedContainerColor = NeumorphBase,
                        focusedBorderColor      = QRPrimary,
                        unfocusedBorderColor    = NeumorphDark,
                        focusedTextColor        = QROnBackground,
                        unfocusedTextColor      = QROnBackground,
                        cursorColor             = QRPrimary,
                    ),
                )

                // Country list
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filteredCountries, key = { it.isoCode }) { country ->
                        Text(
                            text = "${country.isoCode.flagEmoji}  ${country.name}  (${country.dialCode})",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyLarge,
                            color = QROnBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCountrySelected(country) }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                        )
                        HorizontalDivider(color = NeumorphDark.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

