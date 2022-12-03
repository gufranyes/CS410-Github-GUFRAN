import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class GUFRAN_YESILYURT_S020968 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> alphabet = new ArrayList<>();
        ArrayList<String> states = new ArrayList<>();
        String start = "";
        ArrayList<String> finals = new ArrayList<>();
        ArrayList<String> transitions = new ArrayList<>();
        String line = "";
        int flag = 0;
        File path = new File("NFA2.txt");

        try {
            scanner = new Scanner(path);
        } catch (FileNotFoundException e) {
            System.out.println("Invalid path!");
            return;
        }
        
        // Filling array lists according to the input
        while(scanner.hasNext()){
            line = scanner.nextLine();
            switch (line) {
                case "ALPHABET":
                    line = scanner.nextLine();
                    break;
                case "STATES":
                    flag = 1;
                    line = scanner.nextLine();
                    break;
                case "START":
                    flag = 2;
                    line = scanner.nextLine();
                    break;
                case "FINAL":
                    flag = 3;
                    line = scanner.nextLine();
                    break;
                case "TRANSITIONS":
                    flag = 4;
                    line = scanner.nextLine();
                    break;
                case "END":
                    flag = 5;
                    break;
                default:
                    break;
            }
            switch (flag) {
                case 0:
                    alphabet.add(line);
                    break;
                case 1:
                    states.add(line);
                    break;
                case 2:
                    start = line;
                    break;
                case 3:
                    finals.add(line);
                    break;
                case 4:
                    transitions.add(line);
                    break;
                default:
                    break;
            }
        }

        // Initialize a 2D ArrayList which is going to contain destinations of transitions.
        ArrayList<ArrayList<String>> map = new ArrayList<>(alphabet.size());
        for(int i=0; i < alphabet.size(); i++) {
            map.add(new ArrayList<>());
            for(int j = 0; j < states.size(); j++){
                map.get(i).add("-1");
            }
        }

        // Initialize strings to check if transition is non-determined and belongs to the same state
        String prevState = "";
        String prevTrns = "";
        String currState = "";
        String currTrns = "";
        String newState = "";
        int extraStates = 0;

        // Parsing transitions to add destination states to the 2D ArrayList
        for (String string : transitions) {
            String[] splitStg = string.split(" ");
            currState = splitStg[0];
            currTrns = splitStg[1];

            // Check if the source state of the transition or the alphabet changes
            if((!currState.equals(prevState) && !prevState.isEmpty()) || (!currTrns.equals(prevTrns) && !prevTrns.isEmpty())){
                newState = "";
            }
            newState += splitStg[2];
            prevState = currState;
            prevTrns = currTrns;

            // Check if the states array list contains the destination state
            if(!states.contains(newState)){
                states.add(newState);
                for(int j = 0; j < alphabet.size(); j++){
                    map.get(j).add("-1"); // Initializing the state in 2D ArrayList if it is new
                }
                extraStates++;
            }
            map.get(Integer.parseInt(currTrns)).set(states.indexOf(currState), newState);
        }

        // Filling the states that are created during conversion
        String[] strArray = null;
        String targetState = "";
        for(int i = states.size()-extraStates; i < states.size(); i++){
            // Parsing the state to check states it contains (Parses and checks A and B for the state AB)
            strArray = states.get(i).split("");
            for(int j = 0; j < alphabet.size(); j++){
                for(int k = 0; k < strArray.length; k++){
                    // Checks if the state has a non-empty transition
                    if(!map.get(j).get(states.indexOf(strArray[k])).equals("-1")){
                        String response = map.get(j).get(states.indexOf(strArray[k]));
                        for(int x = 0; x < response.length(); x++){
                            char charAtPos = response.charAt(x);
                            if(targetState.indexOf(charAtPos) < 0){
                                targetState += charAtPos;
                                char tempArray[] = targetState.toCharArray();
                                Arrays.sort(tempArray); // Sorts to prevent same states occur with different names (BCA & ABC)
                                targetState = new String(tempArray); 
                            }
                        }
                        map.get(j).set(i, targetState);
                    }
                }

                // Check if the states array list contains the destination state
                if(!states.contains(targetState)){
                    states.add(targetState);
                    for(int y = 0; y < alphabet.size(); y++){
                        map.get(y).add("-1");
                    }
                    extraStates++;
                }

                // If the state has a final state in it, adds it to the array list
                for(int z = 0; z < finals.size(); z++){
                    if(targetState.indexOf(finals.get(z)) > 0 && !finals.contains(targetState)){
                        finals.add(targetState);
                    }
                }
                targetState = "";
            }
        }
        
        // Print the DFA in an expected format
        print(alphabet, states, start, finals, transitions, map);

    }

    public static void print(ArrayList<String> alphabet, ArrayList<String> states, String start, ArrayList<String> finals, ArrayList<String> transitions, ArrayList<ArrayList<String>> map){
        System.out.println("ALPHABET");
        for (String s : alphabet) {
            System.out.println(s);
        }

        System.out.println("STATES");
        for (String s : states) {
            System.out.println(s);
        }

        System.out.println("START\n" + start);

        System.out.println("FINAL");
        for (String s : finals) {
            System.out.println(s);
        }

        System.out.println("TRANSITIONS");
        for(int i=0; i < states.size(); i++) {
            for(int j = 0; j < alphabet.size(); j++){
                if(!map.get(j).get(i).equals("-1")){ // To prevent printing empty transitions
                    System.out.println(states.get(i) + " " + alphabet.get(j) + " " + map.get(j).get(i));
                }
            }
        }
        System.out.println("END");
    }
}