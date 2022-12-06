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
        Map<String, Integer> emails = new LinkedHashMap<>();
        Map<Integer, int[]> courses = new LinkedHashMap<>();


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

                            if (emails.putIfAbsent(email, emailID) != null) {
                                System.out.println("The email is already taken");
                            } else {
                                emails.put(email, emailID);
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
                findStudent(courses);
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

    public static void addPoints(Map<String ,Integer> emails, Map<Integer, int[]> courses) {

        boolean addingPoints = true;

        System.out.println("Enter an id and points or 'back' to return");
        while (addingPoints) {
            String idAndPoints = scanner.nextLine();
            if (!Objects.equals("back", idAndPoints)) {

                String[] stringArray = idAndPoints.split(" ");
                int[] intArray = new int[stringArray.length];
                for (int i = 0; i < stringArray.length; i++) {
                    try {
                        intArray[i] = Integer.parseInt(stringArray[i]);
                    } catch (NumberFormatException e) {
                        System.out.println("Incorrect points format here.");
                    }
                }

                int id = intArray[0];
                if (emails.containsValue(id)) {
                    //Arrays.stream(intArray).allMatch(x -> x > 0). makes sure all the elements are positive
                    if (intArray.length == 5 && Arrays.stream(intArray).allMatch(x -> x > 0)) {
                        if (courses.containsKey(id)) {
                            courses.compute(id, (key, value) -> {
                                for (int i = 1; i < value.length; i++) {
                                    value[i] += intArray[i];
                                }
                                return value;
                            });
                        } else {courses.put(id, intArray);}
                        System.out.println("Points updated.");

                    } else {System.out.println("Incorrect points format.");}
                } else {System.out.println("No student is found for id=" + id + ".");}

            } else {
                addingPoints = false;
            }


        }
    }

    public static void findStudent(Map<Integer, int[]> courses) {
        boolean stillSearching = true;
        String template = "id points: Java=%d; DSA=%d; Database=%d; Spring=%d\n";
        System.out.println("Enter an id or back to return");
        while (stillSearching) {
            String inputId = scanner.nextLine();
            if (!Objects.equals("back", inputId)) {
                int studentId = Integer.parseInt(inputId);
                if (courses.containsKey(studentId)) {
                    int[] scores = courses.get(studentId);
                    System.out.printf(template, scores[1], scores[2], scores[3], scores[4]);
                } else {System.out.println("No student is found for id=" + studentId + ".");}

            } else {stillSearching = false;}
        }
    }

}



