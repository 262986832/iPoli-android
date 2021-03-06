package io.ipoli.android.quest.viewmodels;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import org.threeten.bp.LocalDate;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import io.ipoli.android.R;
import io.ipoli.android.app.ui.formatters.DurationFormatter;
import io.ipoli.android.app.utils.DateUtils;
import io.ipoli.android.app.utils.Time;
import io.ipoli.android.quest.data.Category;
import io.ipoli.android.quest.data.PeriodHistory;
import io.ipoli.android.quest.data.Recurrence;
import io.ipoli.android.quest.data.RepeatingQuest;

/**
 * Created by Venelin Valkov <venelin@curiousily.com>
 * on 4/9/16.
 */
public class RepeatingQuestViewModel {

    private final Context context;
    private final RepeatingQuest repeatingQuest;
    private final LocalDate nextDate;
    private final int scheduledCount;
    private final int completedCount;
    private final int remainingScheduledCount;

    public RepeatingQuestViewModel(Context context, RepeatingQuest repeatingQuest) {
        this.context = context;
        this.repeatingQuest = repeatingQuest;
        nextDate = repeatingQuest.getNextScheduledDate(LocalDate.now());
        List<PeriodHistory> periodHistories = repeatingQuest.getPeriodHistories(LocalDate.now());
        PeriodHistory currentPeriodHistory = periodHistories.get(periodHistories.size() - 1);
        scheduledCount = currentPeriodHistory.getScheduledCount();
        completedCount = currentPeriodHistory.getCompletedCount();
        remainingScheduledCount = currentPeriodHistory.getRemainingScheduledCount();
    }

    public String getName() {
        return repeatingQuest.getName();
    }

    @ColorRes
    public int getCategoryColor() {
        return getQuestCategory().color500;
    }

    @DrawableRes
    public int getCategoryImage() {
        return getQuestCategory().colorfulImage;
    }

    private Category getQuestCategory() {
        return repeatingQuest.getCategoryType();
    }

    public RepeatingQuest getRepeatingQuest() {
        return repeatingQuest;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public String getNextText() {
        String nextText = "";
        if (nextDate == null) {
            nextText += context.getString(R.string.unscheduled);
        } else {
            if (DateUtils.isTodayUTC(nextDate)) {
                nextText = context.getString(R.string.today);;
            } else if (DateUtils.isTomorrowUTC(nextDate)) {
                nextText = context.getString(R.string.tomorrow);;
            } else {
                nextText = new SimpleDateFormat("dd MMM", Locale.getDefault()).format(DateUtils.toStartOfDay(nextDate));
            }
        }

        nextText += " ";

        int duration = repeatingQuest.getDuration();
        Time startTime = repeatingQuest.getStartTime();
        if (duration > 0 && startTime != null) {
            Time endTime = Time.plusMinutes(startTime, duration);
            nextText += startTime + " - " + endTime;
        } else if (duration > 0) {
            nextText += String.format(context.getString(R.string.repeating_quest_for_time), DurationFormatter.formatReadable(context, duration));
        } else if (startTime != null) {
            nextText += startTime;
        }
        return String.format(context.getString(R.string.repeating_quest_next), nextText);
    }

    public long getScheduledCount() {
        return scheduledCount;
    }

    public String getRepeatText() {

        int remainingCount = getRemainingScheduledCount();

        if (remainingCount <= 0) {
            return context.getString(R.string.repeating_quest_done);
        }

        Recurrence recurrence = repeatingQuest.getRecurrence();
        if (recurrence.getRecurrenceType() == Recurrence.RepeatType.MONTHLY) {
            return String.format(context.getString(R.string.repeating_quest_more_this_month), remainingCount);
        }

        return String.format(context.getString(R.string.repeating_quest_more_this_week), remainingCount);

    }

    public int getRemainingScheduledCount() {
        return remainingScheduledCount;
    }


}