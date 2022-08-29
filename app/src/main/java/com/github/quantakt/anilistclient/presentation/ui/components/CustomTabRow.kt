package com.github.quantakt.anilistclient.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    indicator: @Composable (List<TabPosition>) -> Unit = { tabPositions ->
        // TODO: Can we make the indicator match size of current tab label?
        TabRowDefaults.Indicator(
            Modifier
                .tabIndicatorOffset(tabPositions[selectedTabIndex])
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)),
        )
    },
    containerColor: Color = TabRowDefaults.containerColor,
    contentColor: Color = TabRowDefaults.contentColor,
    divider: @Composable () -> Unit = @Composable {
        Divider()
    },
    tabs: @Composable () -> Unit,
) {
    androidx.compose.material3.TabRow(
        modifier = modifier,
        selectedTabIndex = selectedTabIndex,
        indicator = indicator,
        containerColor = containerColor,
        contentColor = contentColor,
        divider = divider,
        tabs = tabs,
    )
}