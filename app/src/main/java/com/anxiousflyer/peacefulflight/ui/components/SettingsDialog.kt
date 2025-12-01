package com.anxiousflyer.peacefulflight.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anxiousflyer.peacefulflight.data.preferences.PreferencesManager

@Composable
fun SettingsDialog(
    preferencesManager: PreferencesManager,
    onDismiss: () -> Unit
) {
    var selectedUnitSystem by remember {
        mutableStateOf(preferencesManager.getUnitSystem())
    }

    var selectedTheme by remember {
        mutableStateOf(preferencesManager.getThemeMode())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Unit System Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Unit System",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectableGroup(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UnitSystemOption(
                            title = "Metric",
                            description = "Celsius (°C), km/h",
                            selected = selectedUnitSystem == PreferencesManager.UnitSystem.METRIC,
                            onClick = { selectedUnitSystem = PreferencesManager.UnitSystem.METRIC }
                        )

                        UnitSystemOption(
                            title = "Imperial",
                            description = "Fahrenheit (°F), mph",
                            selected = selectedUnitSystem == PreferencesManager.UnitSystem.IMPERIAL,
                            onClick = {
                                selectedUnitSystem = PreferencesManager.UnitSystem.IMPERIAL
                            }
                        )
                    }
                }

                // Theme Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Theme",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectableGroup(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UnitSystemOption(
                            title = "System Default",
                            description = "Follow system settings",
                            selected = selectedTheme == PreferencesManager.ThemeMode.SYSTEM,
                            onClick = { selectedTheme = PreferencesManager.ThemeMode.SYSTEM }
                        )

                        UnitSystemOption(
                            title = "Light Mode",
                            description = "Always light theme",
                            selected = selectedTheme == PreferencesManager.ThemeMode.LIGHT,
                            onClick = { selectedTheme = PreferencesManager.ThemeMode.LIGHT }
                        )

                        UnitSystemOption(
                            title = "Dark Mode",
                            description = "Always dark theme",
                            selected = selectedTheme == PreferencesManager.ThemeMode.DARK,
                            onClick = { selectedTheme = PreferencesManager.ThemeMode.DARK }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    preferencesManager.setUnitSystem(selectedUnitSystem)
                    preferencesManager.setThemeMode(selectedTheme)
                    onDismiss()
                }
            ) {
                Text("Save", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun UnitSystemOption(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface.copy(
            alpha = 0.3f
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
