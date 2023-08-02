import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowCalendar(
    dateRange: Boolean = false,
    state: UseCaseState,
    onDateSelect: (LocalDate) -> Unit,
    onDateRangeSelect: (List<LocalDate>) -> Unit,
) {
    val dates: MutableList<LocalDate> = ArrayList()
    val currentDate = LocalDate.now()
    for (i in 1 until 100) {
        dates.add(currentDate.plusDays(i.toLong()))
    }
    CalendarDialog(state = state,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
            disabledDates = dates
        ),
        selection = if (dateRange) CalendarSelection.Dates { date ->
            onDateRangeSelect(date)
        } else {
            CalendarSelection.Date { date ->
                onDateSelect(date)
            }
        })
}
