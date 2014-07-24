import java.util.Comparator;


public class EntryComparator implements Comparator<Entry>
{
	@Override
	public int compare(Entry e1, Entry e2)
	{
		return e1.score.compareTo(e2.score);
	}

}
