package com.anabars.tripsplit.ui.screens.addexpense

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anabars.tripsplit.ui.model.ExpenseCategory
import com.anabars.tripsplit.ui.components.TsContentCard
import com.anabars.tripsplit.ui.components.DateInputSection
import java.time.LocalDate

@Composable
fun ExpenseDateAndCategoryCard(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    selectedCategory: ExpenseCategory,
    onCategoryChange: (ExpenseCategory) -> Unit
) {
    TsContentCard(modifier = modifier) {
        DateInputSection(
            modifier = Modifier.padding(horizontal = 16.dp),
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
        )

        ExpenseCategoriesRadioGroup(
            modifier = Modifier.padding(horizontal = 16.dp),
            selectedCategory = selectedCategory,
            onCategoryChange = onCategoryChange
        )
    }
}