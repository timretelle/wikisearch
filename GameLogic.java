// 11-21

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class GameLogic
{
	static JFrame f;
	static String article = "";
	static int width = 15;
	static int height = 15;
	static int foundWords = 0;
	static int amountOfWords = 0;
	static WordSearch w;
	static JLabel[][] buttonList;
	static buttonClickChecker clickChecker = new buttonClickChecker();
	static final String URL_START = "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=";
	static ArrayList<JLabel> wordBankLabelList = new ArrayList<JLabel>();
	static String guess = "";
	static JLabel guessSoFar = new JLabel("Current Guess: ");
	static String downloadArticle(String wikiPage)
	{
		String returnValue = "";

		try {
			// Create a buffered reader of the page's source
			URL wiki = new URL(URL_START + wikiPage);
			URLConnection yc = wiki.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

			String inputLine;

			boolean inBody = false;
			while ((inputLine = in.readLine()) != null)
			{
				returnValue += inputLine;
			}
		}

		// Generic exception handling boilerplate
		catch(FileNotFoundException ex) {
			System.out.println("unknown");
		}
		catch(MalformedURLException e) {
			  	System.out.println("badly formed url exception occurred");
				return null;
		}
		catch(IOException e) {
			System.out.println("IO exception occurred");
			return null;
		}
		//If the page is a redirect, follow the redirect and try the download again
		if(returnValue.contains("#REDIRECT [[")){
			int startIndex = returnValue.indexOf("#REDIRECT [[") + 12;
			int endIndex = returnValue.indexOf(']',startIndex);
			returnValue = (downloadArticle(returnValue.substring(startIndex, endIndex).replace(' ', '_')));
		}
		return returnValue;
	}

	static String[] findLinks(String html)
	{
		ArrayList<String> returnValueBuffer = new ArrayList<String>();
		//iterates through all the html checking for "[[" which indicates the start of a link
		for(int i=0; i<html.length(); i++){
			//makes sure the link is not to a file or an image
			if(html.startsWith("[[",i) && !html.startsWith("[[Image:",i) && !html.startsWith("[[File:",i)){
				//skips the actual [[, straight to the text of the link
				i+=2;
				//adds the link text by stopping when it finds the closing bracket
				if(html.substring(i,  html.indexOf(']',i)).indexOf('|') >= 0){
					returnValueBuffer.add(html.substring(i,  html.indexOf('|',i)));
				}
				else{
				returnValueBuffer.add(html.substring(i,  html.indexOf(']',i)));
				}
			}
		}
		/* If you just do ArrayList.toArray() an object array will be returned. Since the type won't be
		 * String, we can't use this method. Instead, we have to call ArrayList.toArray(String[]). This
		 * will work only if the String[] given can fit all of the ArrayList's values.
		 */
		String[] returnValue = new String[returnValueBuffer.size()];
		returnValue = returnValueBuffer.toArray(returnValue);
		return returnValue;
	}
	public static void addToGuess(String s){
		if(!guess.equals("")){
			
		}
		guess += s.substring(12,13); //add the clicked letter to the guess by getting it out of the HTML
		int foundIndex = checkIfFound(); //check if the current guess is in the word bank, get the index of the found word if it is
		if(foundIndex != -1 && !wordBankLabelList.get(foundIndex).getText().startsWith("<html><body><span style='text-decoration: line-through;'>")){ //make sure the word hasn't already been found
			wordBankLabelList.get(foundIndex).setText("<html><body><span style='text-decoration: line-through;'>" + guess + "</span></body></html>");//strikethrough the found word
			cancelButtonPressed();
			foundWords++;
			if(foundWords == amountOfWords){
				JOptionPane.showMessageDialog(f, "Congratulations, you have completed the word search!");
				System.exit(0);
			}
		}
		guessSoFar.setText("Guess So Far: " + guess); //update the JLabel showing the current guess
	}
	public static int checkIfFound(){
		if(w.getWordBank() != null){
			for(int i = 0; i<w.getWordBank().length; i++){
				if(w.getWordBank()[i].equals(guess)){
					return i;
				}
			}
		}
		return -1;
	}
	public static void cancelButtonPressed(){
		guess = "";
		guessSoFar.setText("Guess So Far: ");
		clickChecker.clearGuess();
		for(JLabel[] labelRow : buttonList){
			for(JLabel theLabel : labelRow){
				theLabel.setForeground(Color.BLACK);	//reset the color for all the letters
			}
		}
	
	}
	public static void main(String[] args)
	{
		JPanel inputPanel = new JPanel();
        BoxLayout inputPanelLayout = new BoxLayout(inputPanel, BoxLayout.Y_AXIS);
		JTextField urlEntry = new JTextField();
		JTextField widthEntry = new JTextField();
		JTextField heightEntry = new JTextField();
		inputPanel.setLayout(inputPanelLayout);
		JLabel urlEntryLabel = new JLabel("Enter the name of the article: ");
		JLabel widthEntryLabel = new JLabel("Enter width (Default is 15): ");
		JLabel heightEntryLabel = new JLabel("Enter height (Default is 15): ");
		inputPanel.add(urlEntryLabel);
		inputPanel.add(urlEntry);
		inputPanel.add(widthEntryLabel);
		inputPanel.add(widthEntry);
		inputPanel.add(heightEntryLabel);
		inputPanel.add(heightEntry);
		int result = JOptionPane.showConfirmDialog(null, inputPanel, 	//make a popup asking for the article, width, and height
               "Wikisearch", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
        	if(urlEntry.getText().equals("")){
        		article = "Special:Random";
        	}
        	else{
        		article = urlEntry.getText().replace(' ','_');
        	}
      	   	if(widthEntry.getText().equals("")){
      	   		width = 15;
      	   	}
      	   	else{
      	   		width = Integer.parseInt(widthEntry.getText());
      	   	}
      	   	if(heightEntry.getText().equals("")){
      	   		height = 15;
      	   	}
      	   	else{
      	   		height = Integer.parseInt(heightEntry.getText());
      	   	}
      	}
      	else if(result == JOptionPane.CANCEL_OPTION){
      		System.exit(0);
      	}
		GridLayout panelLayout = new GridLayout(2,1);
		f = new JFrame("Wikisearch: " + article.replace('_',' '));
		f.setLayout(panelLayout);
        GridLayout buttonLayout = new GridLayout(width,height); //make a GridLayout for the buttons in the word search
        buttonList = new JLabel[width][height]; //make a 2d array of buttons, each one containing one letter of the word search
        JPanel searchPanel = new JPanel(buttonLayout); //make a JPanel for the word search to go on
        JPanel wordBankPanel = new JPanel(); //make a JPanel for the word bank to go on
        JPanel cancelPanel = new JPanel();
        BoxLayout cancelLayout = new BoxLayout(cancelPanel, BoxLayout.Y_AXIS);
        cancelPanel.setLayout(cancelLayout);
        JLabel wordBankTitle = new JLabel("Word Bank:");
        JButton cancelButton = new JButton("clear guess");
        cancelButton.addActionListener(new ActionListener() { 
  			public void actionPerformed(ActionEvent e) { 
    			cancelButtonPressed();
  			} 
		} );
        cancelPanel.add(cancelButton);
        cancelPanel.add(guessSoFar);
        wordBankPanel.add(wordBankTitle);
		String articleHTML = downloadArticle(article); //download the specified wikipedia article
		String[] words = findLinks(articleHTML);

		// Remove non-letters, and make string all lowercase
		for (int i = 0; i < words.length; i++)
		{
			// Make it lowercase and remove non-letters
			String tmp = words[i].toLowerCase();
			words[i] = "";
			for (int j = 0; j < tmp.length(); j++)
			{
				if ( Character.isAlphabetic(tmp.charAt(j)) )
					words[i] += tmp.charAt(j);
			}
		}
		String toWrite = "";
		
		w = new WordSearch(height, width, words);
		for (int i = 0; i < buttonList.length; i++)
		{
			char[] row = w.getWordField()[i];
			for (int j = 0; j<buttonList[0].length; j++)
			{
						char letter = row[j];
						JLabel toAdd = new JLabel(letter + "");	
				//		toAdd.setBorderPainted(false);
				//		toAdd.setFocusPainted(false);
        		//		toAdd.setMargin(new Insets(0, 0, 0, 0));
        		//		toAdd.setContentAreaFilled(false);
        		//		toAdd.setBorderPainted(false);
        		//		toAdd.setOpaque(false);
        				toAdd.addMouseListener(clickChecker);
        			//	toAdd.setMouseCommand(toAdd.getText());
        				buttonList[i][j] = toAdd;
						searchPanel.add(toAdd); //add button to jpanel
						//System.out.println(toAdd.getUIClassID());
			}
		//	toWrite += "\n";
		}
	//	System.out.println();
		if (w.getWordBank() != null)
            for (String word : w.getWordBank()){
            	amountOfWords++;
            	JLabel wordBankLabel = new JLabel(word + " ");
            	wordBankLabelList.add(wordBankLabel);
            	wordBankPanel.add(wordBankLabelList.get(wordBankLabelList.size()-1));
            	
               // System.out.print(word + " ");
            }
        else
            System.out.print("Word bank empty");
            
    //  GridLayout wordBankLayout = new GridLayout();
	//	JTextArea area1 = new JTextArea(toWrite, width, height);
		f.add(searchPanel);
		f.add(cancelPanel);
	//	f.add(area1);
		f.setSize(700,700);
		f.setVisible(true);
		f.add(wordBankPanel);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       // System.out.println();
       for(int i = 0; i<width; i++){
       		for(int j = 0; j<height; j++){
       	buttonList[i][j].setText("<html><body>" + buttonList[i][j].getText() + "<!--" + i + "," + j + "--></body></html>");
       		}
       }
	}
}