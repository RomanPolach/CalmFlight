package com.example.calmflight.ui.components

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
import com.example.calmflight.data.preferences.PreferencesManager
import com.example.calmflight.ui.theme.BeigeWarm
import com.example.calmflight.ui.theme.NavyDeep
import com.example.calmflight.ui.theme.NavyLight
import com.example.calmflight.ui.theme.TealSoft

@Composable
fun SettingsDialog(
    preferencesManager: PreferencesManager,
    onDismiss: () -> Unit
) {
    var selectedUnitSystem by remember {
        mutableStateOf(preferencesManager.getUnitSystem())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = BeigeWarm
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Unit System",
                    style = MaterialTheme.typography.titleMedium,
                    color = TealSoft,
                    fontWeight = FontWeight.SemiBold
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Metric option
                    UnitSystemOption(
                        title = "Metric",
                        description = "Celsius (°C), km/h",
                        selected = selectedUnitSystem == PreferencesManager.UnitSystem.METRIC,
                        onClick = { selectedUnitSystem = PreferencesManager.UnitSystem.METRIC }
                    )

                    // Imperial option
                    UnitSystemOption(
                        title = "Imperial",
                        description = "Fahrenheit (°F), mph",
                        selected = selectedUnitSystem == PreferencesManager.UnitSystem.IMPERIAL,
                        onClick = { selectedUnitSystem = PreferencesManager.UnitSystem.IMPERIAL }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    preferencesManager.setUnitSystem(selectedUnitSystem)
                    onDismiss()
                }
            ) {
                Text("Save", color = TealSoft)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = BeigeWarm.copy(alpha = 0.7f))
            }
        },
        containerColor = NavyLight,
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
        color = if (selected) TealSoft.copy(alpha = 0.2f) else NavyDeep.copy(alpha = 0.3f)
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
                    selectedColor = TealSoft,
                    unselectedColor = BeigeWarm.copy(alpha = 0.5f)
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = BeigeWarm,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = BeigeWarm.copy(alpha = 0.7f)
                )
            }
        }
    }
}
