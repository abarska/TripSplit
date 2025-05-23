package com.anabars.tripsplit.ui.screens.addtrip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.InfoText

@Composable
fun ParticipantRowItem(
    name: String,
    modifier: Modifier = Modifier,
    onChangeStatusClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
    hideChangeStatusIcon: Boolean = false,
    hideDeleteIcon: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(dimensionResource(R.dimen.vertical_spacer_small)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        InfoText(text = name)
        Row {
            if (!hideChangeStatusIcon) {
                IconButton(
                    modifier = Modifier.size(32.dp),
                    onClick = { onChangeStatusClick(name) }) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = stringResource(R.string.deactivate_participant)
                    )
                }
            }
            if (!hideDeleteIcon) {
                IconButton(
                    modifier = Modifier.size(32.dp),
                    onClick = { onDeleteClick(name) }) {
                    Icon(
                        imageVector = if (hideChangeStatusIcon) Icons.Default.Delete else Icons.Default.Stop,
                        contentDescription = stringResource(R.string.delete_participant)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ParticipantRowPreviewOneButton() {
    ParticipantRowItem(
        name = "placeholder",
        hideChangeStatusIcon = true
    )
}

@Preview(showBackground = true)
@Composable
private fun ParticipantRowPreviewTwoButtons() {
    ParticipantRowItem(
        name = "placeholder"
    )
}