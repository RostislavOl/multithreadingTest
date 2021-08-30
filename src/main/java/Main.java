import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) {
        System.out.println("Insert the file path for search:");
        Scanner sc = new Scanner(System.in);
        String path = sc.nextLine();
        System.out.println("Insert file extensions:");
        String extension = sc.nextLine();
        File dir = new File(path);

        List<String> result = fulfillFiles(dir);

        ArrayList<Future<List<String>>> results =
                new ArrayList<Future<List<String>>>();

        ExecutorService exec = Executors.newCachedThreadPool();

        for (int i = 0; i < 4; i++)
            results.add(exec.submit(new TaskWithResult(extension, result)));

        for (Future<List<String>> fs : results)
            try {
                // Вызов get() блокируется до завершения;:
                System.out.println(fs.get());
            } catch (InterruptedException e) {
                System.out.println(e);
                return;
            } catch (ExecutionException e) {
                System.out.println(e);
            } finally {
                exec.shutdown();
            }

    }

    private static List<String> fulfillFiles(File dir) {
        List<String> result = new ArrayList<>();
        Queue<File> fileTree = new PriorityQueue<>();
        Collections.addAll(fileTree, Objects.requireNonNull(dir.listFiles()));
        while (!fileTree.isEmpty()) {
            File currentFile = fileTree.remove();
            if (currentFile.isDirectory()) {
                Collections.addAll(fileTree, Objects.requireNonNull(currentFile.listFiles()));
            } else {
                result.add(currentFile.getAbsolutePath());
            }
        }
        return result;
    }


}
