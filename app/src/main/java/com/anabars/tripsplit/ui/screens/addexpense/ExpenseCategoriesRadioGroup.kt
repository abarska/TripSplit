package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.R
import com.anabars.tripsplit.ui.components.LayoutType
import com.anabars.tripsplit.ui.components.TsRadioGroup
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.utils.inputWidthModifier
import com.anabars.tripsplit.utils.isTablet

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

        TsRadioGroup(
            modifier = modifier,
            items = categories,
            selectedItem = selectedCategory,
            onItemSelected = onCategoryChanged,
            layout = LayoutType.Row,
            itemWidth = if (isTablet(LocalConfiguration.current)) 56.dp else 48.dp
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
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpenseCategoriesRadioGroupPreview() {
    ExpenseCategoriesRadioGroup(
        selectedCategory = ExpenseCategory.Food,
        onCategoryChanged = {},
        modifier = Modifier.inputWidthModifier()
    )
}