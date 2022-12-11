import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class GUFRAN_YESILYURT_S020968 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> variables = new ArrayList<>();
        ArrayList<String> terminals = new ArrayList<>();
        String start = "";
        ArrayList<String> rules = new ArrayList<>();
        ArrayList<String> epsilonValues = new ArrayList<>();
        String line = "";
        int flag = 0;
        File path = new File("G1.txt");

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
                case "NON-TERMINAL":
                    line = scanner.nextLine();
                    break;
                case "TERMINAL":
                    flag = 1;
                    line = scanner.nextLine();
                    break;
                case "RULES":
                    flag = 2;
                    line = scanner.nextLine();
                    break;
                case "START":
                    flag = 3;
                    line = scanner.nextLine();
                    break;
                default:
                    break;
            }
            switch (flag) {
                case 0:
                    variables.add(line);
                    break;
                case 1:
                    terminals.add(line);
                    break;
                case 2:
                    rules.add(line);
                    break;
                case 3:
                    start = line;
                    break;
                default:
                    break;
            }
        }

        // Initialize a 2D ArrayList which is going to contain rules for their variables.
        boolean startAdded = false;
        ArrayList<ArrayList<String>> map = new ArrayList<>(variables.size());
        for(int i=0; i < variables.size(); i++) {
            String ruleStart = "";
            String ruleValue = "";
            map.add(new ArrayList<>());
            for(int j = 0; j < rules.size(); j++){
                ruleStart = rules.get(j).split(":")[0];
                ruleValue = rules.get(j).split(":")[1];
                
                if(ruleValue.equals("e") && !epsilonValues.contains(ruleStart)){
                    epsilonValues.add(ruleStart);
                }

                if(ruleStart.equals(variables.get(i)) && !ruleValue.equals("e")){
                    map.get(i).add(ruleValue);
                }

                // Add new start variable if needed
                if(ruleStart.equals(start) && ruleValue.contains(start)){
                    char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
                    for(char k : alphabet){
                        if(!variables.contains("" + k)){
                            start = "" + k;
                            startAdded = true;
                            break;
                        }
                    }
                }
            }
        }

        // Remove epsilon values if there are any
        while(!epsilonValues.isEmpty()){
            for(int i = 0; i < map.size(); i++){
                for(int j = 0; j < map.get(i).size(); j++){
                    if(map.get(i).get(j).contains(epsilonValues.get(epsilonValues.size()-1))){
                        String temp = map.get(i).get(j);
                        String newTemp = temp.replace(epsilonValues.get(epsilonValues.size()-1), "");
                        if(!newTemp.isEmpty()){
                            map.get(i).add(newTemp);
                        }
                    }
                }
            }
            epsilonValues.remove(epsilonValues.size()-1);
        }

        // Remove Unit productions
        for(int i = 0; i < map.size(); i++){
            for(int j = 0; j < map.get(i).size(); j++){
                if(map.get(i).get(j).equals(variables.get(i))){
                    map.get(i).remove(j);
                }

                if(variables.contains(map.get(i).get(j))){
                    int indexTemp = variables.indexOf(map.get(i).get(j));
                    map.get(i).addAll(map.get(indexTemp));
                    map.get(i).remove(j);
                }
            }
            map.set(i, removeDuplicates(map.get(i)));
        }

        // If there is any aB make it XB
        boolean hasVar = false;
        boolean hasTerm = false;
        boolean hasDoubleTerm = false;
        ArrayList<String> hasTermArr = new ArrayList<>();
        ArrayList<String> tempArr = new ArrayList<>();
        String addedVar = "";
        for(int i = 0; i < map.size(); i++){
            for(int j = 0; j < map.get(i).size(); j++){
                for(int k = 0; k < variables.size(); k++){
                    if(map.get(i).get(j).contains(variables.get(k))){
                        hasVar = true;
                        break;
                    }else{
                        hasVar = false;
                    }
                }
                for(int x = 0; x < terminals.size(); x++){
                    if(map.get(i).get(j).contains(terminals.get(x))){
                        hasTerm = true;
                        hasTermArr.add(terminals.get(x));
                    }else if(!hasTerm){
                        hasTerm = false;
                    }
                }

                if(!hasVar && hasTerm && map.get(i).get(j).length() > 1){
                    hasDoubleTerm = true;
                }else{
                    hasDoubleTerm = false;
                }

                if(hasVar && hasTerm || hasDoubleTerm){
                    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                    String newAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                    if(startAdded){
                        newAlphabet = alphabet.replace(start, "");
                    }
                    for(char k : newAlphabet.toCharArray()){
                        if(!variables.contains("" + k) && !hasTermArr.isEmpty()){
                            if(!tempArr.contains(hasTermArr.get(0))){
                                variables.add("" + k);
                                addedVar = "" + k;
                                map.add(new ArrayList<>());
                                map.get(variables.indexOf("" + k)).add(hasTermArr.get(0));
                                tempArr.add(hasTermArr.get(0));
                            }
                            String tempStr = map.get(i).get(j);
                            ArrayList<String> tempArrayList = new ArrayList<>();
                            tempArrayList.add(hasTermArr.get(0));
                            String newTempStr = tempStr.replace(hasTermArr.get(0), variables.get(map.indexOf(tempArrayList)));
                            map.get(i).set(j, newTempStr);
                            hasTermArr.remove(0);
                        }
                    }
                }
            }
        }

        // Make new variables for k > 2 productions
        ArrayList<String> lastTwoArr = new ArrayList<>();
        for(int i = 0; i < map.size(); i++){
            for(int j = 0; j < map.get(i).size(); j++){
                String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                String newAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                if(startAdded){
                    newAlphabet = alphabet.replace(start, "");
                }
                if(map.get(i).get(j).length() > 2){
                    String lastTwo = map.get(i).get(j).substring(map.get(i).get(j).length()-2);
                    lastTwoArr.add(lastTwo);
                    for(char k : newAlphabet.toCharArray()){
                        if(!variables.contains("" + k) && !map.contains(lastTwoArr)){
                            variables.add("" + k);
                            addedVar = "" + k;
                            map.add(new ArrayList<>());
                            map.get(variables.indexOf("" + k)).add(lastTwo);
                        }
                    }
                    String tempStr = map.get(i).get(j);
                    String newTempStr = tempStr.replace(lastTwo, addedVar);
                    map.get(i).set(j, newTempStr);
                    lastTwoArr.remove(0);
                }
            }
        }


        // Print the CNF in an expected format
        print(variables, terminals, map, start);

    }

    public static void print(ArrayList<String> variables, ArrayList<String> terminals, ArrayList<ArrayList<String>> map, String start){
        System.out.println("NON-TERMINAL");
        for (String s : variables) {
            System.out.println(s);
        }

        System.out.println("TERMINAL");
        for (String s : terminals) {
            System.out.println(s);
        }

        System.out.println("RULES");
        if(!start.equals(variables.get(0))){
            for(int i = 0; i < map.get(0).size(); i++){
                System.out.println(start + ":" + map.get(0).get(i));
            }
        }
        for(int i=0; i < map.size(); i++) {
            for(int j = 0; j < map.get(i).size(); j++){
                System.out.println(variables.get(i) + ":" + map.get(i).get(j));
            }
        }

        System.out.println("START\n" + start);
    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list){
  
        // Create a new LinkedHashSet
        Set<T> set = new LinkedHashSet<>();
  
        // Add the elements to set
        set.addAll(list);
  
        // Clear the list
        list.clear();
  
        // add the elements of set
        // with no duplicates to the list
        list.addAll(set);
  
        // return the list
        return list;
    }
}