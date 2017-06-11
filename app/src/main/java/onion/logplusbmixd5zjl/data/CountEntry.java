package onion.logplusbmixd5zjl.data;

import android.content.Context;
import android.util.Log;

import onion.logplusbmixd5zjl.Common;
import onion.logplusbmixd5zjl.Count;
import onion.logplusbmixd5zjl.util.Scheduler;

import java.util.Vector;

/** a single countable */
public class CountEntry extends TaskEntry {
    public static final String TAG = CountEntry.class.getName();
    public static final int hours = 18;
    public static final int minutes = 0;
    public static final int remindRepetitions = 1;

    private static Vector<CountEntry> all = new Vector<CountEntry>();

    private final long target;
    private LogEntry count;

    // td: separate repetitions from count (f.ex. l.2 zu l.1.2)

    public CountEntry(Context context, String name, long target) {
	super(context, name);
	this.target = target;
	count = LogEntry.getByName(context, name, Common.getStartOfToday(context).getTime());//td: move to own date class
	all.add(this); // this is the culprit, arrrgh
    }

    public static Vector<CountEntry> getAll() { return all; }

    @Override public Class getActivity() { return Count.class; }
    public long getCount() { return ( count == null )? 0 : count.getDuration(); }
    public long getTarget() { return target; }

    public Reminder getReminder() {
        return new Reminder(hours, minutes, remindRepetitions, this);
    }

    public void incrementCount(long increment) {
	Log.d(TAG, "increment(" + increment + ") from " + getCount());
	// td: refactor: unify this nag with timerentry.nag?
	Scheduler.get(context).scheduleNag();
	if ( count == null ) {
	    count = new LogEntry(context, name, 0, System.currentTimeMillis());
	}
	count.saveDuration(count.getDuration() + increment, true);
    }
    // this writes to sp twice, td: rework (?name as put/commit?)

    // @Override public void save() {
    //     throw new UnsupportedOperationException("not implemented");
    // }

    // @Override public void remove() {
    //     throw new UnsupportedOperationException("not (yet) implemented");
    // }
    /** clears the counter for this task */
    public void resetCount() {
	if ( count != null ) {
	    count.remove();
	    count = null;
	}
    }

    @Override public String toString() {
	return name + " (" + getCount() + "/" + target + ") ";
    }

    // @Override public String verboseString() {
    // 	return getClass().getName() + "[" +
    // 	    "name=" + name + ", " +
    // 	    "durationMillis=" + durationMillis + ", " +
    // 	    "ID=" + ID  + ", " +
    // 	    "repetitions=" + repetitions
    // 	    + "]";
    // }
}
