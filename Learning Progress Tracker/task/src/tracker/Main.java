package tracker;
import java.util.*;


public class Main {

    final static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("Learning Progress Tracker");

        String nameRegex = "^(?!.*[-']{2})[a-zA-Z ][-a-zA-Z ']*[a-zA-Z ]$";

        String emailRegex = "[a-zA-Z0-9\\-\\.]+@[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z0-9\\-\\.]+";



        int count = 0;
        boolean isCommand = true;
        boolean isCredentials = true;
        int emailID = 10000;
        Map<String, String> emails = new LinkedHashMap<>();
        Map<String, int[]> courses = new LinkedHashMap<>();


        while (isCommand) {
            String command = scanner.nextLine().toLowerCase();


            if ("add students".equals(command)) {
                System.out.println("Enter student credentials or 'back' to return");


                while (isCredentials) {
                    String studentCredentials = scanner.nextLine();

                    if (studentCredentials.equals("back")) {
                        System.out.printf("Total %d students have been added\n", count);
                        isCredentials = false;
                    }
                    else if ( studentCredentials.split(" ").length < 3) {
                        System.out.println("Incorrect credentials");
                    } else {
                        Learner learner = new Learner(studentCredentials);

                        if (learner.getFirstName().matches(nameRegex) && learner.getLastName().matches(nameRegex) && learner.getEmail().matches(emailRegex)) {
                            String email = learner.getEmail();
                            String stringEmailID = Integer.toString(emailID);

                            if (emails.putIfAbsent(email, stringEmailID) != null) {
                                System.out.println("This email is already taken");
                            } else {
                                emails.put(email, stringEmailID);
                                count++;
                                emailID++;
                                System.out.println("The student has been added");
                            }

                        } else if (!learner.getFirstName().matches(nameRegex)) {
                            System.out.println("Incorrect first name.");
                        } else if (!learner.getLastName().matches(nameRegex)) {
                            System.out.println("Incorrect last name.");
                        } else if (!learner.getEmail().matches(emailRegex)) {
                            System.out.println("Incorrect email");
                        }
                        else {
                            System.out.println("Invalid input");
                            isCommand = false;
                        }
                    }
                }


            } else if ("list".equals(command)) {
                //implement list functionality
                if (emails.isEmpty()) {
                    System.out.println("No students found");
                } else {
                    System.out.println("Students:");
                    emails.forEach((email, id) -> System.out.println(id));
                }

            } else if ("add points".equals(command)) {
                //implement add points functionality (while loop)
                addPoints(emails, courses);
            } else if ("find".equals(command)) {
                //implement find functionality (while loop)
                findStudent(emails, courses);
            } else if (command.trim().isEmpty()) {
                System.out.println("no input");
            } else if ("back".equals(command)) {
                System.out.println("Enter 'exit' to exit the program");
            } else if ("exit".equals(command)) {
                System.out.println("Bye!");
                isCommand = false;
            } else {
                System.out.println("Unknown command!");
            }

        }
    }

    public static void addPoints(Map<String ,String> emails, Map<String, int[]> courses) {

        boolean addingPoints = true;
        boolean noNonInteger = true;

        System.out.println("Enter an id and points or 'back' to return");
        while (addingPoints) {
            String idAndPoints = scanner.nextLine();
            if (!Objects.equals("back", idAndPoints)) {

                String[] stringArray = idAndPoints.split(" ");
                int[] intArray = new int[stringArray.length-1];
                for (int i = 0; i < intArray.length; i++) {
                    try {intArray[i] = Integer.parseInt(stringArray[i+1]);} catch (NumberFormatException e) {noNonInteger = false;}
                }

                String stringID = stringArray[0];

                if (emails.containsValue(stringID)) {
                    //Arrays.stream(intArray).allMatch(x -> x > 0). makes sure all the elements are positive
                    if (stringArray.length == 5 && Arrays.stream(intArray).allMatch(x -> x > 0) && noNonInteger) {
                        if (courses.containsKey(stringID)) {
                            courses.compute(stringID, (key, value) -> {
                                for (int i = 0; i < value.length; i++) {
                                    value[i] += intArray[i];
                                }
                                return value;
                            });
                        } else {courses.put(stringID, intArray);}
                        System.out.println("Points updated.");

                    } else {
                        System.out.println("Incorrect points format.");
                        noNonInteger = true;
                    }
                } else {System.out.println("No student is found for id=" + stringID + ".");}

            } else {
                addingPoints = false;
            }


        }
    }

    public static void findStudent(Map<String, String> emails, Map<String, int[]> courses) {
        boolean stillSearching = true;
        String template = "%s points: Java=%d; DSA=%d; Databases=%d; Spring=%d";
        System.out.println("Enter an id or back to return");
        while (stillSearching) {
            String inputId = scanner.nextLine();
            if (!Objects.equals("back", inputId)) {
                int[] scores = courses.get(inputId);

                if (emails.containsValue(inputId)) {
                    if (courses.containsKey(inputId) && scores != null) {
                        System.out.printf(template, inputId, scores[0], scores[1], scores[2], scores[3]);
                    } else {System.out.printf(template, inputId, 0, 0, 0, 0);}
                } else {System.out.println("No student is found for id=" + inputId + ".");}


            } else {stillSearching = false;}
        }
    }

}



