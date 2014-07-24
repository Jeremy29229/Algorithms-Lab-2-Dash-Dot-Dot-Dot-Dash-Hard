import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import edu.neumont.nlp.DecodingDictionary;

public class ExhaustiveDecoder
{
	DecodingDictionary dd;
	float total;
	float minimumScore;
	ArrayList<String> options;

	ExhaustiveDecoder(DecodingDictionary dd)
	{
		this.dd = dd;
		total = 10000.0f;
		minimumScore = 300.0f;
	}

	public List<String> decode(String message)
	{
		ArrayList<Entry> soFar = new ArrayList<Entry>();

		String toGo = message;

		ArrayList<Entry> results = new ArrayList<Entry>();
		ArrayList<Entry> empty = new ArrayList<Entry>();
		results = decodeHelper(soFar, toGo, empty);

		Collections.sort(results,
				Collections.reverseOrder(new EntryComparator()));

		ArrayList<String> orderedResults = new ArrayList<String>();

		for (int i = 0; i < results.size(); i++)
		{			
			orderedResults.add(results.get(i).phrase);
		}

		return orderedResults;
	}

	public ArrayList<Entry> decodeHelper(ArrayList<Entry> soFar, String toGo,
			ArrayList<Entry> results)
	{
		if (toGo.isEmpty())
		{
			Entry concatenated = new Entry();
			concatenated.phrase = removeEncoding(soFar.get(0).phrase);
			concatenated.score = soFar.get(0).score / total;

			for (int i = 1; i < soFar.size(); i++)
			{
				concatenated.phrase += " " + removeEncoding(soFar.get(i).phrase);
				concatenated.score *= soFar.get(i).score / total;
			}

			results.add(concatenated);
		} else
		{
			Set<String> possibleWords;

			for (int i = 0; i < toGo.length(); i++)
			{
				possibleWords = dd.getWordsForCode(toGo.substring(0, i + 1));
				if (possibleWords != null)
				{
					for (String s : possibleWords)
					{
						if (soFar.isEmpty()
								|| dd.frequencyOfFollowingWord(
										soFar.get(soFar.size() - 1).phrase, s) > minimumScore)
						{
							Entry word = new Entry();
							word.phrase = s;

							if (soFar.isEmpty())
							{
								word.score = total;
							} else
							{
								word.score = (float) dd
										.frequencyOfFollowingWord(
												soFar.get(soFar.size() - 1).phrase,
												s);
							}

							ArrayList<Entry> newSoFar = new ArrayList<Entry>(
									soFar);
							newSoFar.add(word);

							String newToGo = toGo.substring(toGo.substring(0,
									i + 1).length());

							decodeHelper(newSoFar, newToGo, results);
						}
					}
				}
			}
		}
		return results;
	}
	
	public String removeEncoding(String s)
	{
		int extraSpaceIndex = s.indexOf('[');
		return s.substring(0, extraSpaceIndex);
	}
}
