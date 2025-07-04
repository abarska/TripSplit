package com.anabars.tripsplit.ui.screens.addexpense

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.components.LayoutType
import com.anabars.tripsplit.ui.components.TsRadioGroup

@Composable
fun ExpenseCategoriesRadioGroup(
    selectedCategory: ExpenseCategory,
    onCategoryChanged: (ExpenseCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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

        TsRadioGroup(
            modifier = modifier,
            items = categories,
            selectedItem = selectedCategory,
            onItemSelected = onCategoryChanged,
            layout = LayoutType.Row,
            itemWidth = if (enoughSpace) equalItemWidth else 48.dp
        ) { category ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
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