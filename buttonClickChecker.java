import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class buttonClickChecker implements MouseListener{
	private String guessSoFar = "";
	private String labelContents = "";
	private JLabel theLabel;
	private int xPos = 0;
	private int yPos = 0;
	private int lastXPos = 0;
	private int lastYPos = 0;
	private int indexOfXCoordinate = 19;
	private boolean horizontalRight = false;	//booleans for which direction the guess is going
	private boolean horizontalLeft = false;
	private boolean verticalUp = false;
	private boolean verticalDown = false;
	private boolean diagonalUpRight = false;
	private boolean diagonalDownRight = false;
	private boolean diagonalUpLeft = false;
	private boolean diagonalDownLeft = false;
	public void mouseClicked(MouseEvent event) {
	}
	public void mouseEntered(MouseEvent event) {
	}
	public void mouseExited(MouseEvent event) {
	}
	public void mousePressed(MouseEvent event) {
		theLabel = (JLabel)event.getSource();
		labelContents = theLabel.getText();
		if(!(labelContents.charAt(18) == ',')){
		yPos = Integer.parseInt(labelContents.substring(17,19));
		indexOfXCoordinate++;
		}
		else{
		yPos = Integer.parseInt(labelContents.substring(17,18));			
		}
		if(labelContents.charAt(indexOfXCoordinate+1) == '-'){
			xPos = Integer.parseInt(labelContents.substring(indexOfXCoordinate,indexOfXCoordinate+1));
		}
		else{
			xPos = Integer.parseInt(labelContents.substring(indexOfXCoordinate,indexOfXCoordinate+2));
		}
		indexOfXCoordinate = 19;
		if(guessSoFar.equals("")){
			validChoice();
			}
		else if(guessSoFar.length() == 1){
			if(getDirection()){
				validChoice();
			}
		}
			if(horizontalRight && (xPos-lastXPos == 1 && yPos == lastYPos)){
				validChoice();
			}
			else if(horizontalLeft && (xPos-lastXPos == -1 && yPos == lastYPos)){
				validChoice();
			}
			else if(verticalDown && (yPos-lastYPos == 1 && xPos == lastXPos)){
				validChoice();
			}
			else if(verticalUp && (yPos-lastYPos == -1 && xPos == lastXPos)){
				validChoice();
			}
			else if(diagonalDownRight && (xPos-lastXPos == 1 && yPos-lastYPos == 1)){
				validChoice();
			}
			else if(diagonalDownLeft && (xPos-lastXPos == -1 && yPos-lastYPos == 1)){
				validChoice();
			}
			else if(diagonalUpLeft && (xPos-lastXPos == -1 && yPos-lastYPos == -1)){
				validChoice();
			}
			else if(diagonalUpRight && (xPos-lastXPos == 1 && yPos-lastYPos == -1)){
				validChoice();
			}
	}
	public void mouseReleased(MouseEvent event) {
	}
	public boolean getDirection(){	//Called on the second choice of a letter. determines the direction the guessed word is going, either horizontally, vertically, or diagonally. Returns false if the word isn't going in a valid direction
		horizontalRight = (xPos-lastXPos == 1 && yPos == lastYPos);
		horizontalLeft = (xPos-lastXPos == -1 && yPos == lastYPos);
		verticalDown = (yPos-lastYPos == 1 && xPos == lastXPos);
		verticalUp = (yPos-lastYPos == -1 && xPos == lastXPos);
		diagonalDownRight = (xPos-lastXPos == 1 && yPos-lastYPos == 1);
		diagonalDownLeft = (xPos-lastXPos == -1 && yPos-lastYPos == 1);
		diagonalUpRight = (xPos-lastXPos == 1 && yPos-lastYPos == -1);
		diagonalUpLeft = (xPos-lastXPos == -1 && yPos-lastYPos == -1);
		return (horizontalRight || horizontalLeft || verticalUp || verticalDown || diagonalUpRight || diagonalUpLeft || diagonalDownRight || diagonalDownLeft);
	}
	public void clearGuess(){	//called when the clear button is pressed, resets the current guess and position
		guessSoFar = "";
		xPos = 0;
		yPos = 0;
		lastXPos = 0;
		lastYPos = 0;
	}
	public void validChoice(){	//called when the choice of letter is valid with the direction the word is going
		lastXPos = xPos;
		lastYPos = yPos;
		theLabel.setForeground(Color.RED);	//make chosen letters red
		guessSoFar += labelContents.substring(12,13);
		GameLogic.addToGuess(labelContents);
	}
}
