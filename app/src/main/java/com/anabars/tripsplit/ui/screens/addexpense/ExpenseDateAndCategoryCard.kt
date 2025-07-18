package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.DateInputSection
import com.anabars.tripsplit.ui.model.AddExpenseDateCategoryState
import com.anabars.tripsplit.ui.utils.inputWidthModifier
import java.time.LocalDate

@Composable
fun ExpenseDateAndCategoryCard(
    dateCategoryState: AddExpenseDateCategoryState,
    onDateSelected: (LocalDate) -> Unit,
    onCategoryChanged: (ExpenseCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    TsContentCard(modifier = modifier) {
        DateInputSection(
            modifier = Modifier.padding(horizontal = 16.dp),
            selectedDate = dateCategoryState.selectedDate,
            onDateSelected = onDateSelected,
        )

        ExpenseCategoriesRadioGroup(
            modifier = Modifier.padding(horizontal = 16.dp),
            selectedCategory = dateCategoryState.selectedCategory,
            onCategoryChanged = onCategoryChanged
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpenseDateAndCategoryCardPreview() {
    ExpenseDateAndCategoryCard(
        dateCategoryState = AddExpenseDateCategoryState(),
        onDateSelected = {},
        onCategoryChanged = {},
        modifier = Modifier.inputWidthModifier()
    )
}