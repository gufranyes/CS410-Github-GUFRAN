import java.io.File;
import java.io.IOException;
import java.util.*;

public class GUFRAN_YESILYURT_S020968 {
  private static final String INPUT_FILE_NAME = "Input_GUFRAN_YESILYURT_S020968.txt";

  private static class Transition {
    String currentState;
    char read;
    char write;
    char direction;
    String nextState;

    Transition(String currentState, char read, char write, char direction, String nextState) {
      this.currentState = currentState;
      this.read = read;
      this.write = write;
      this.direction = direction;
      this.nextState = nextState;
    }

    public String toString() {
      return (currentState + " " + read + " " + write + " " + direction + " " + nextState);
    }
  }

  private List<String> states;
  private String startState;
  private String acceptState;
  private String rejectState;
  private List<Character> tape;
  private ArrayList<Transition> stateTransitions;
  private char blankSymbol;
  private int headPosition;
  private int tapeLength;

  private List<Character> inputAlphabet;

  private List<Character> tapeAlphabet;

  public GUFRAN_YESILYURT_S020968() throws IOException {
    Scanner scanner = new Scanner(new File(INPUT_FILE_NAME));

    int inputAlphabetLength = scanner.nextInt();
    inputAlphabet = new ArrayList<>();

    for (int i = 1; i <= inputAlphabetLength; i++) {
      inputAlphabet.add(scanner.next().charAt(0));
    }

    int tapeAlphabetLength = scanner.nextInt();
    tapeAlphabet = new ArrayList<>();

    for (int i = 1; i <= tapeAlphabetLength; i++) {
      tapeAlphabet.add(scanner.next().charAt(0));
    }

    blankSymbol = scanner.next().charAt(0);

    int statesLength = scanner.nextInt();
    states = new ArrayList<>();

    for (int i = 1; i <= statesLength; i++) {
      states.add(scanner.next());
    }
    scanner.nextLine();
    startState = scanner.nextLine();
    acceptState = scanner.nextLine();
    rejectState = scanner.nextLine();

    stateTransitions = new ArrayList<>();
    tape = new ArrayList<>();
    while (scanner.hasNext()) {
      String line = scanner.nextLine();

      String[] tapeSymbols = line.split("\\s+");

      if (tapeSymbols.length == 1) {
        for (int i = 0; i < line.length(); i++) {
          tape.add(line.charAt(i));
        }
      } else if (tapeSymbols.length == 5) {
        Transition transition = new Transition(tapeSymbols[0], tapeSymbols[1].charAt(0), tapeSymbols[2].charAt(0), tapeSymbols[3].charAt(0), tapeSymbols[4]);
        stateTransitions.add(transition);
      }
    }
  }

  private void run() {
    String currentState = startState;
    List<String> visitedStates = new ArrayList<>();
    headPosition = 0;
    Transition transition = null;
    tapeLength = tape.size();

    int result;
    while (true) {
      for (Transition t : stateTransitions) {
        if (t.currentState.equals(currentState)) {
          if (t.read == tape.get(headPosition)) {
            transition = new Transition(t.currentState, t.read, t.write, t.direction, t.nextState);
            break;
          }
        }
      }

      tape.set(headPosition, transition.write);
      if (transition.direction == 'R') {
        headPosition++;
        if (headPosition == tapeLength) {
          tapeLength++;
          tape.add(blankSymbol);
        }
      } else {
        headPosition--;
        if (headPosition < 0) {
          headPosition = 0;
          tape.add(0, blankSymbol);
          tapeLength++;
        }
      }

      visitedStates.add(currentState);
      currentState = transition.nextState;
      if (currentState.equals(acceptState)) {
        result = 1;
        break;
      } else if (currentState.equals(rejectState)) {
        result = 2;
        break;
      } else if (
        visitedStates.indexOf(currentState) !=
        visitedStates.lastIndexOf(currentState)
      ) {
        // The current state has been visited before, consider it as a looped state
        result = 3;
        break;
      }
    }

    System.out.print("ROUTE: ");
    visitedStates.forEach(state -> System.out.print(state + " "));
    System.out.println();

    switch (result) {
      case 1:
        System.out.println("RESULT: accepted");
        break;
      case 2:
        System.out.println("RESULT: rejected");
        break;
      case 3:
        System.out.println("RESULT: loop");
        break;
    }
  }

  public static void main(String[] args) throws IOException {
    GUFRAN_YESILYURT_S020968 turingMachine = new GUFRAN_YESILYURT_S020968();
    turingMachine.run();
  }
}
