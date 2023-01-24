/*
 * Part of WikiSearch. This class is used for representing and generating word searches.
 */

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.*;
public class WordSearch
{
	final static int DEFAULT_WORD_FIELD_SIZE = 15;
	// A grid of letters. This is where the user will be looking for words.
	private char[][] wordField;

	// Sets every letter in the word array to blank (represented using an underscore)
	public void clearWordField()
	{
		for (int i = 0; i < wordField.length; i++)
			for (int j = 0; j < wordField[0].length; j++)
				wordField[i][j] = '_';
	}
	// Return a copy of the word field (so that the client can't mess with ours)
	public char[][] getWordField()
	{
		if (wordField == null)
			return null;

		char[][] returnValue = new char[wordField.length][wordField[0].length];

		for (int i = 0; i < wordField.length; i++)
			for (int j = 0; j < wordField[0].length; j++)
				returnValue[i][j] = wordField[i][j];

		return returnValue;
	}

	private String[] wordBank;
	// Return a copy of the word bank (so that the client can't mess with ours)
	public String[] getWordBank()
	{
		if (wordBank == null)
			return wordBank;

		String[] returnValue = new String[wordBank.length];

		for (int i = 0; i < wordBank.length; i++)
			returnValue[i] = wordBank[i];

		return returnValue;
	}

	// Checks weather or not the given letter can be put in the given location
	private boolean isAvailable(Coordinate space, char letter)
	{
		if (space.row >= 0 && space.row < wordField.length)
			if (space.column >= 0 && space.column < wordField[0].length)
				if (wordField[space.row][space.column] == '_' || wordField[space.row][space.column] == letter)
					return true;

		return false;
	}

	// Used to pick random starting positions for words in addWord
	private Random generator;

	// Returns weather or not word could be added
	private boolean addWord(String word)
	{
        // Blacklist certain words and make sure that they are long enough to be words in a search of words
        if ( word.length()<4 || word.equals("") || word.equals("categories") || word.contains("stub") || word.equals("cite")  || word.equals("sources")  || word.equals("removed")  || word.equals("expandingit")  || word.equals("addone")  || word.equals("talkpage")  || word.equals("improveit")  || word.equals("originalresearch")  || word.equals("worldwideview") || word.matches(".*\\d+.*") )
            return false;

		// First generate a list of possible starting points
		ArrayList<Coordinate> possibleLocations= new ArrayList<Coordinate>();
		for (int i = 0; i < wordField.length; i++)
		{
			for (int j = 0; j < wordField[0].length; j++)
			{
				Coordinate toTest = new Coordinate(i, j);
				if (isAvailable(toTest, word.charAt(0)))
				{
					possibleLocations.add(toTest);
				}
			}
		}

		// If I change  the while loop's condition to possibleLocations.isEmpty() == false, I get an exception. Why?
		while (possibleLocations.isEmpty() == false)
		{
			// Choose a random location to try and put the word there
			Coordinate currentLocation =  possibleLocations.get( generator.nextInt(possibleLocations.size()) );

			// Now try all the possible orientations that the word could be put there, in a random order
			ArrayList<direction> possibleDirections = new ArrayList<direction>(8);
			possibleDirections.add(direction.NORTH);
			possibleDirections.add(direction.NORTH_EAST);
			possibleDirections.add(direction.EAST);
			possibleDirections.add(direction.SOUTH_EAST);
			possibleDirections.add(direction.SOUTH);
			possibleDirections.add(direction.SOUTH_WEST);
			possibleDirections.add(direction.WEST);
			possibleDirections.add(direction.NORTH_WEST);

			while (possibleDirections.isEmpty() == false)
			{
				direction currentDirection = possibleDirections.get( generator.nextInt(possibleDirections.size()) );
				Coordinate toAdd; // A coordinate + toAdd = the next coordinate in line pointing in currentDirection

				// Initialize toAdd so that the above comment holds true
				switch (currentDirection)
				{
					case NORTH:
						toAdd = new Coordinate( 1,  0);
						break;
					case NORTH_EAST:
						toAdd = new Coordinate( 1,  0);
						break;
					case EAST:
						toAdd = new Coordinate( 0,  1);
						break;
					case SOUTH_EAST:
						toAdd = new Coordinate(-1,  1);
						break;
					case SOUTH:
						toAdd = new Coordinate(-1,  0);
						break;
					case SOUTH_WEST:
						toAdd = new Coordinate(-1, -1);
						break;
					case WEST:
						toAdd = new Coordinate( 0,  -1);
						break;
					case NORTH_WEST:
						toAdd = new Coordinate( 1, -1);
						break;
					default: // I don't think that it's even possible that this will happen, but the compiler thinks that toAdd might not have been initialized later on in the program.
						toAdd = new Coordinate(1000, 1000);
				}

				// Stores each coordinate that works with the current word
				ArrayList<Coordinate> validLocations = new ArrayList<Coordinate>(1);
				validLocations.add(currentLocation);

				boolean locationAndDirectionWorked = true;
				Coordinate toTest = currentLocation.add(toAdd);
				for (int i = 1; i < word.length(); i++)
				{
					if (isAvailable(toTest, word.charAt(i)))
					{
						validLocations.add(toTest);
					}
					else
					{
						locationAndDirectionWorked = false;
						break;
					}
					toTest = toTest.add(toAdd);
				}

				if (locationAndDirectionWorked)
				{
					// Add the word to the word search!
					for (int i = 0; i < word.length(); i++)
					{
						Coordinate finalLocation = validLocations.get(i);
						wordField[finalLocation.row][finalLocation.column] = word.charAt(i);
					}

					return true;
				}

				possibleDirections.remove(currentDirection);
			}
			possibleLocations.remove(currentLocation);
		}

		return false;
	}

	// Small struct-like class for representing the location of a letter in the word field. Used above.
	private class Coordinate
	{
		public int row, column;

		public String toString()
		{
			return "[" + row + "][" + column + "]";
		}

		public Coordinate add(Coordinate addend)
		{
			return new Coordinate(row + addend.row, column + addend.column);
		}

		public Coordinate(int r, int c)
		{
			row = r;
			column = c;
		}
	}

	// Used to represent in what direction a word could be added to the wordField
	private enum direction
	{
		NORTH,
		NORTH_EAST,
		EAST,
		SOUTH_EAST,
		SOUTH,
		SOUTH_WEST,
		WEST,
		NORTH_WEST
	}

	// Replace the '_' with letters
	public void randomizeBlanks()
	{
		for (int i = 0; i < wordField.length; i++)
			for (int j = 0; j < wordField[0].length; j++)
				if (wordField[i][j] == '_')
					wordField[i][j] = (char)(generator.nextInt(26) + 'a');
	}

	// Adds an entire list of words. Returns true if any of them could be added
	public boolean addWords(String[] toAdd)
	{
		ArrayList<String> wordBankBuffer = new ArrayList<String>();
		// Make sure we don't lose what's already in the word bank
		if (wordBank != null)
			for (String word : wordBank){
				wordBankBuffer.add(word);
			}

		boolean wordWasAdded = false;
		for (String newWord : toAdd)
		{
			// Make sure that the word we are adding is not already in the word bank
			boolean repeat = false;
			for(String existingWord : wordBankBuffer){
				if(newWord.equals(existingWord)){
					repeat = true;
					break;
				}
			}
			if (!repeat && addWord(newWord))
			{
				wordBankBuffer.add(newWord);
				wordWasAdded = true;
			}
		}

		wordBank = new String[wordBankBuffer.size()];
		wordBankBuffer.toArray(wordBank);

		return wordWasAdded;
	}

	public WordSearch()
	{
		this(DEFAULT_WORD_FIELD_SIZE, DEFAULT_WORD_FIELD_SIZE);
	}
	public WordSearch(String[] words)
	{
		this(DEFAULT_WORD_FIELD_SIZE, DEFAULT_WORD_FIELD_SIZE, words);
	}
	public WordSearch(int n, int m, String[] words)
	{
		this(n, m);

		addWords(words);
		randomizeBlanks();
	}
	public WordSearch(int n, int m)
	{
		// n and m must be positive
		if (n < 1)
			n = 1;
		if (m < 1)
			m = 1;

		wordField = new char[n][m];
		clearWordField();
		wordBank = null;

		generator = new Random();
	}
}
