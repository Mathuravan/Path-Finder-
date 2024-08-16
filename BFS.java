import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BFS {
    private static class Puzzle {
        int x;
        int y;

        Puzzle(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Puzzle point = (Puzzle) obj;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private static final int[] type_X = {-1, 1, 0, 0};
    private static final int[] type_Y = {0, 0, -1, 1};
    private static final char Start = 'S';
    private static final char Finish = 'F';
    private static final char Rock = '0';

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String directoryPath = "src/input_files";

        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Invalid directory path. Please ensure the path is correct.");
            return;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.err.println("No text files found in the directory.");
            return;
        }

        while (true) {
            System.out.println("Available files:");
            for (int i = 0; i < files.length; i++) {
                System.out.println((i + 1) + ". " + files[i].getName());
            }

            System.out.println("Select a file by entering the corresponding number (or enter 0 to exit):");
            int fileIndex = scanner.nextInt() - 1;

            if (fileIndex == -1) {
                System.out.println("Exiting...");
                break;
            }

            if (fileIndex < 0 || fileIndex >= files.length) {
                System.err.println("Invalid file selection. Please try again.");
                continue;
            }

            String filePath = files[fileIndex].getPath();
            try {
                char[][] map = parseMap(filePath);
                List<String> path = BFS_Path(map);
                if (path != null) {
                    System.out.println("Path from S to F:");
                    for (String p : path) {
                        System.out.println(p);
                    }
                } else {
                    System.out.println("No path found.");
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error processing file: " + e.getMessage());
            }

            System.out.println();
        }
    }

    private static char[][] parseMap(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        char[][] map = new char[lines.size()][];
        for (int i = 0; i < lines.size(); i++) {
            map[i] = lines.get(i).toCharArray();
        }

        return map;
    }

    private static List<String> BFS_Path(char[][] map) {
        Puzzle start = null, finish = null;
        int rows = map.length;
        int cols = map[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (map[i][j] == Start) start = new Puzzle(i, j);
                if (map[i][j] == Finish) finish = new Puzzle(i, j);
            }
        }

        if (start == null || finish == null) {
            System.err.println("Start or Finish point not found in the path.");
            return null;
        }

        Queue<Puzzle> queue = new LinkedList<>();
        Map<Puzzle, Puzzle> parentMap = new HashMap<>();
        queue.add(start);
        parentMap.put(start, null);

        while (!queue.isEmpty()) {
            Puzzle current = queue.poll();

            if (current.equals(finish)) {
                return generatePath(parentMap, start, finish);
            }

            for (int i = 0; i < 4; i++) {
                Puzzle nextStop = moveToNextValidation(map, current, type_X[i], type_Y[i], rows, cols);
                if (!parentMap.containsKey(nextStop)) {
                    parentMap.put(nextStop, current);
                    queue.add(nextStop);
                }
            }
        }

        return null;
    }

    private static Puzzle moveToNextValidation(char[][] map, Puzzle start, int dx, int dy, int rows, int cols) {
        int x = start.x;
        int y = start.y;

        while (x + dx >= 0 && x + dx < rows && y + dy >= 0 && y + dy < cols && map[x + dx][y + dy] != Rock) {
            x += dx;
            y += dy;
        }

        return new Puzzle(x, y);
    }

    private static List<String> generatePath(Map<Puzzle, Puzzle> parentMap, Puzzle start, Puzzle finish) {
        LinkedList<String> way = new LinkedList<>();
        Puzzle step = finish;
        LinkedList<Puzzle> steps = new LinkedList<>();

        while (step != null) {
            Puzzle parent = parentMap.get(step);
            if (parent != null) {
                steps.addFirst(step);
            }
            step = parent;
        }

        steps.addFirst(start);

        // Add "Start at" to the path
        way.add("1. Start at (" + (start.y + 1) + ", " + (start.x + 1) + ")");

        for (int i = 1; i < steps.size(); i++) {
            Puzzle current = steps.get(i);
            Puzzle pre = steps.get(i - 1);
            String direction = getDirection(pre, current);
            way.add((i + 1) + ". Move " + direction + " to (" + (current.y + 1) + ", " + (current.x + 1) + ")");
        }

        way.add((steps.size() + 1) + ". Done!");
        return way;
    }

    private static String getDirection(Puzzle from, Puzzle to) {
        if (from.x < to.x) return "down";
        if (from.x > to.x) return "up";
        if (from.y < to.y) return "right";
        if (from.y > to.y) return "left";
        return "";
    }
}
