package com.anabars.tripsplit.ui.screens.addexpense

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.listitems.TripSplitRadioButton
import com.anabars.tripsplit.ui.model.ExpenseCategory

@Composable
fun ExpenseCategoriesRadioGroup(
    selectedCategory: ExpenseCategory,
    onCategoryChange: (ExpenseCategory) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        val categories = ExpenseCategory.allExpenseCategories()

        // show label text only on tablets in landscape orientation
        val configuration = LocalConfiguration.current
        val enoughSpace =
            configuration.smallestScreenWidthDp >= 600 && configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val textMeasurer = rememberTextMeasurer()
        val width = categories.maxOf {
            textMeasurer.measure(
                text = AnnotatedString(stringResource(it.titleRes)),
                style = MaterialTheme.typography.labelLarge
            ).size.width
        }
        val longestTextWidth = remember { width }
        val equalItemWidth = with(LocalDensity.current) {
            longestTextWidth.toDp().times(1.4f)
        }

        categories.forEach { category ->
            TripSplitRadioButton(
                modifier = Modifier
                    .then(
                        if (enoughSpace) Modifier.width(equalItemWidth)
                        else Modifier.width(56.dp)
                    ),
                value = category,
                isSelected = category == selectedCategory,
                onItemClick = { onCategoryChange(category) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_button_size)),
                        imageVector = category.icon,
                        contentDescription = stringResource(category.titleRes),
                    )
                    if (enoughSpace) {
                        Text(
                            text = stringResource(category.titleRes),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
}