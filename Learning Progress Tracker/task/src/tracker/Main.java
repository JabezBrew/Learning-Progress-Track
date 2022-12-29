package tracker;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Main {

    final static Scanner scanner = new Scanner(System.in);
    public static int notifyMethodCallCount = 0;
    public static void main(String[] args) {

        System.out.println("Learning Progress Tracker");

        String nameRegex = "^(?!.*[-']{2})[a-zA-Z ][-a-zA-Z ']*[a-zA-Z ]$";

        String emailRegex = "[a-zA-Z0-9-.]+@[a-zA-Z0-9-.]+\\.[a-zA-Z0-9-.]+";



        int studentsAdded = 0;
        boolean isCommand = true;
        boolean isCredentials = true;
        int emailID = 10000;
        Map<String, String> emails = new LinkedHashMap<>();
        Map<String, String> emailAndName = new LinkedHashMap<>();
        Map<String, int[]> courses = new LinkedHashMap<>();
        List<int[]> submissions = new ArrayList<>();


        while (isCommand) {
            String command = scanner.nextLine().toLowerCase();


            if ("add students".equals(command)) {
                System.out.println("Enter student credentials or 'back' to return");


                while (isCredentials) {
                    String studentCredentials = scanner.nextLine();

                    if (studentCredentials.equals("back")) {
                        System.out.printf("Total %d students have been added\n", studentsAdded);
                        isCredentials = false;
                    }
                    else if ( studentCredentials.split(" ").length < 3) {
                        System.out.println("Incorrect credentials");
                    } else {
                        Learner learner = new Learner(studentCredentials);

                        if (learner.getFirstName().matches(nameRegex) && learner.getLastName().matches(nameRegex) && learner.getEmail().matches(emailRegex)) {
                            String fullName = learner.getFirstName() + " " + learner.getLastName();
                            String email = learner.getEmail();
                            String stringEmailID = Integer.toString(emailID);

                            if (emails.putIfAbsent(email, stringEmailID) != null) {
                                System.out.println("This email is already taken");
                            } else {
                                emails.put(email, stringEmailID);
                                emailAndName.put(email, fullName);
                                studentsAdded++;
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
                if (emails.isEmpty()) {
                    System.out.println("No students found");
                } else {
                    System.out.println("Students:");
                    emails.forEach((email, id) -> System.out.println(id));
                }

            } else if ("statistics".equals(command)) {
                showStats(courses, submissions);
            } else if ("notify".equals(command)) {
                notifyStudent(emails, emailAndName, courses);
            } else if ("add points".equals(command)) {
                addPoints(emails, courses, submissions);
            } else if ("find".equals(command)) {
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

    public static void addPoints(Map<String ,String> emails, Map<String, int[]> courses, List<int[]> submissions) {

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
                submissions.add(intArray);

                if (emails.containsValue(stringID)) {
                    //Arrays.stream(intArray).allMatch(x -> x >= 0). makes sure all the elements are non-negative
                    if (stringArray.length == 5 && Arrays.stream(intArray).allMatch(x -> x >= 0) && noNonInteger) {
                        if (courses.containsKey(stringID)) {
                            courses.computeIfPresent(stringID, (key, value) -> {
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

    public static void notifyStudent(Map<String, String> emails, Map<String, String> emailAndName, Map<String, int[]> courses) {
        //made the email the key so had to create a custom function get key (email) with the value.
        Function<String, String> keyFinder = value ->
            emails.entrySet().stream()
                    .filter(entry -> Objects.equals(value, entry.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

        String message = """
                To: %s
                Re: Your Learning Progress
                Hello %s! You have accomplished your %s course!
                """;
        record AccomplishedStudents (String email, String name) {}
        List<AccomplishedStudents> studentsToSendMailList = new ArrayList<>();


        if (notifyMethodCallCount < 1) {

            notifyMethodCallCount++;
            for (String studentId : courses.keySet()) {
                int[] scoreArray = courses.get(studentId);
                if (scoreArray[0] == 600) {
                    String email = keyFinder.apply(studentId);
                    String name = emailAndName.get(email);
                    studentsToSendMailList.add(new AccomplishedStudents(email, name));
                    System.out.printf(message, email, name, "Java");
                }
                if (scoreArray[1] == 400) {
                    String email = keyFinder.apply(studentId);
                    String name = emailAndName.get(email);
                    studentsToSendMailList.add(new AccomplishedStudents(email, name));
                    System.out.printf(message, email, name, "DSA");
                }
                if (scoreArray[2] == 480) {
                    String email = keyFinder.apply(studentId);
                    String name = emailAndName.get(email);
                    studentsToSendMailList.add(new AccomplishedStudents(email, name));
                    System.out.printf(message, email, name, "Databases");
                }
                if (scoreArray[3] == 550) {
                    String email = keyFinder.apply(studentId);
                    String name = emailAndName.get(email);
                    studentsToSendMailList.add(new AccomplishedStudents(email, name));
                    System.out.printf(message, email, name, "Spring");
                }

            }
        }


        //Increase count by one iff the email appears one in studentsToSendMailList. code below makes sure of that
        Set<AccomplishedStudents> uniqueStudentsToSendMailList = new HashSet<>(studentsToSendMailList);
        int studentsNotified = uniqueStudentsToSendMailList.size();
        System.out.println("Total " + studentsNotified + " students have been notified.");
    }

    public static void showStats(Map<String, int[]>courses, List<int[]>submissions) {
        List<Integer> mostPopularCourse = computeMostPopularCourse(courses, (course1, course2) -> course1 > course2);
        List<Integer> leastPopularCourse = computeLeastPopularCourse(courses, (course1, course2) -> course1 < course2);
        List<Integer> mostActiveCourse = computeMostActiveCourse(submissions, (submission1, submission2) -> submission1 > submission2);
        List<Integer> leastActiveCourse = computeLeastActiveCourse(submissions, (submission1, submission2) -> submission1 < submission2);
        List<Integer> hardestCourse = computeHardestCourse(courses, (course1, course2) -> course1 < course2); // course 1 < course 2 because the lowest average is the hardest.
        List<Integer> easiestCourse = computeEasiestCourse(courses, (course1, course2) -> course1 > course2);

        String statsTemp = """
                Most popular: %s
                Least popular: %s
                Highest Activity: %s
                Lowest Activity: %s
                Easiest course: %s
                Hardest course: %s
                """;

        System.out.println("Type the name of a course to see details or 'back' to quit");
        if (mostPopularCourse.equals(leastPopularCourse) && mostActiveCourse.equals(leastPopularCourse)) {
            System.out.printf(statsTemp, convertIndexToName(mostPopularCourse), "n/a", convertIndexToName(mostActiveCourse), "n/a", convertIndexToName(easiestCourse), convertIndexToName(hardestCourse));
        } else {
            System.out.printf(statsTemp, convertIndexToName(mostPopularCourse), convertIndexToName(leastPopularCourse), convertIndexToName(mostActiveCourse), convertIndexToName(leastActiveCourse), convertIndexToName(easiestCourse), convertIndexToName(hardestCourse));
        }

        boolean moreStats = true;
        while (moreStats) {
            String inputCourse = scanner.nextLine().toLowerCase();
            moreStats = placeholder(courses, inputCourse);
        }
    }

    public static String convertIndexToName(List<Integer> indexes) {
        StringBuilder sb = new StringBuilder();
        boolean arrayEmpty = true;

        String names = null;
        if (!indexes.isEmpty()) {
            for (Integer index : indexes) {
                switch (index) {
                    case 0 -> sb.append("Java, ");
                    case 1 -> sb.append("DSA, ");
                    case 2 -> sb.append("Databases, ");
                    case 3 -> sb.append("Spring, ");
                }
            }
            names = String.valueOf(sb);
            arrayEmpty = false;
        }

        return arrayEmpty ? "n/a" : names.substring(0, names.length() - 2); //removes the last two characters: comma and space.
    }

    public static List<Integer> computeMostPopularCourse(Map<String, int[]> courses, BiFunction<Integer, Integer, Boolean> compare) {
        int maxCount = 0;
        return getPopularity(courses, compare, maxCount);
    }

    public static List<Integer> computeLeastPopularCourse(Map<String, int[]> courses, BiFunction<Integer, Integer, Boolean> compare) {

        int minCount = Integer.MAX_VALUE;
        return getPopularity(courses, compare, minCount);
    }

    public static List<Integer> getPopularity(Map<String, int[]> courses, BiFunction<Integer, Integer, Boolean> compare, int minCount) {
        List<Integer> leastPopular = new ArrayList<>();

        if (courses.values().isEmpty()) {return leastPopular;}
        else {
            for (int i = 0; i < 4; i++) {
                int countForMin = 0;
                for (int[] marks: courses.values()) {
                    if (marks[i] > 0) {countForMin++;}
                }
                if (compare.apply(countForMin, minCount)) {
                    minCount = countForMin;
                    leastPopular.clear();
                    leastPopular.add(i);
                } else if (countForMin == minCount) {
                    leastPopular.add(i);
                }
            }
        }
        return leastPopular;
    }


    public static List<Integer> computeHardestCourse(Map<String, int[]> courses, BiFunction<Double, Double, Boolean> compare) {
        double maxAvg = Double.MAX_VALUE;
        return getDifficulty(courses, compare, maxAvg);
    }

    public static List<Integer> computeEasiestCourse(Map<String, int[]> courses, BiFunction<Double, Double, Boolean> compare) {
        double minAvg = 0;
        return getDifficulty(courses, compare, minAvg);
    }

    public static List<Integer> getDifficulty(Map<String, int[]> courses, BiFunction<Double, Double, Boolean> compare, double minAvg) {

        double[] avgMarks = new double[4];
        int[] count = new int[4];
        for (int[] marks : courses.values()) {
            for (int i = 0; i < 4; i++) {
                if (marks[i] > 0) {
                    avgMarks[i] += marks[i];
                    count[i]++;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            if (count[i] > 0) {
                avgMarks[i] /= count[i];
            }
        }

        List<Integer> average = new ArrayList<>();

        if (courses.values().isEmpty()) {return average;}
        else {
            for (int i = 0; i < 4; i++) {
                if (compare.apply(avgMarks[i], minAvg)) {
                    minAvg = avgMarks[i];
                    average.clear();
                    average.add(i);
                } else if (avgMarks[i] == minAvg ) {average.add(i);}
            }
        }

        return average;
    }

    public static List<Integer> computeMostActiveCourse(List<int[]> submissions, BiFunction<Integer, Integer, Boolean> compare) {
        int courseSubmissions = 0;
        return getActivityCount(submissions, compare, courseSubmissions);
    }

    public static List<Integer> computeLeastActiveCourse(List<int[]> submissions, BiFunction<Integer, Integer, Boolean> compare) {
        int courseSubmissions = Integer.MAX_VALUE;
        return getActivityCount(submissions, compare, courseSubmissions);
    }

    public static List<Integer> getActivityCount(List<int[]> submissions, BiFunction<Integer, Integer, Boolean> compare, int courseSubmissions) {
        List<Integer> mostActive = new ArrayList<>();

        //condition to check if the submissions list is empty
        if (submissions.isEmpty()) {
            return mostActive;
        } else {
            for (int i = 0; i < 4; i++) {
                int countForMin = 0;
                for (int[] marks: submissions) {
                    if (marks[i] > 0) {countForMin++;}
                }
                if (compare.apply(countForMin, courseSubmissions)) {
                    courseSubmissions = countForMin;
                    mostActive.clear();
                    mostActive.add(i);
                } else if (countForMin == courseSubmissions ) {
                    mostActive.add(i);
                }
            }
        }

        return mostActive;
    }

    public static boolean placeholder(Map<String, int[]>courses, String inputCourse) {

        boolean changeMoreStats = false;

        String template = """
                %s\t%.0f\t%.1f%%
                """;

        //a Course (e.g. java or spring) record that stores student id and marks and percentage.
        record Course (String id, float[] scores) {
            public float[] getScores() {
                return scores;
            }
            public String getId() {
                return id;
            }
        }

        //create a List object of course objects, each Course object referring to a student enrolled in the particular course.
        List<Course> javaCourse = new ArrayList<>();
        List<Course> dsaCourse = new ArrayList<>();
        List<Course> databasesCourse = new ArrayList<>();
        List<Course> springCourse = new ArrayList<>();

        // Get the entries in the courses map
        Set<Map.Entry<String, int[]>> entries = courses.entrySet();

        // Loop through the entries and calculate the marks for each course
        for (Map.Entry<String, int[]> entry : entries) {
            String id = entry.getKey();
            int[] marks = entry.getValue();

            javaCourse.add(new Course(id, new float[]{marks[0], ((float) (marks[0]*100) / 600) } ));
            dsaCourse.add(new Course( id, new float[]{marks[1], ((float) marks[1]*100) / 400 } ));
            databasesCourse.add(new Course( id, new float[]{marks[2], ((float) marks[2]*100) / 480 } ));
            springCourse.add(new Course( id, new float[]{marks[3], ((float) marks[3]*100) / 550 } ));
        }

        //sorting.
        List<List<Course>> allCourses = Arrays.asList(javaCourse, dsaCourse, databasesCourse, springCourse);
        for (List<Course> course : allCourses) {
            course.sort((c1, c2) -> {
                int result = Float.compare(c2.getScores()[0], c1.getScores()[0]);
                return result == 0 ? c1.getId().compareTo(c2.getId()) : result;
            });
        }

        //what to show when user inputs course.
        switch (inputCourse) {
            case "java" -> {
                System.out.println("Java\nid\tpoints\tcompleted");
                for (Course course : javaCourse) {
                    if (course.scores[0] != 0) {System.out.printf(template, course.id, course.scores[0], course.scores[1]);}
                }
            }
            case "dsa" -> {
                System.out.println("DSA\nid\tpoints\tcompleted");
                for (Course course : dsaCourse) {
                    if (course.scores[0] != 0) {System.out.printf(template, course.id, course.scores[0], course.scores[1]);}
                }
            }
            case "databases" -> {
                System.out.println("Databases\nid\tpoints\tcompleted");
                for (Course course : databasesCourse) {
                    if (course.scores[0] != 0) {System.out.printf(template, course.id, course.scores[0], course.scores[1]);}
                }
            }
            case "spring" -> {
                System.out.println("Spring\nid\tpoints\tcompleted");
                for (Course course : springCourse) {
                    if (course.scores[0] != 0) {System.out.printf(template, course.id, course.scores[0], course.scores[1]);}
                }
            }
            case "back" -> changeMoreStats = true;
            default -> System.out.println("Unknown course");
        }
        return !changeMoreStats;
    }
}



