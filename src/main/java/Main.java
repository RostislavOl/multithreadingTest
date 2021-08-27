import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Insert the file path for search:");
        Scanner sc = new Scanner(System.in);
        String path = sc.nextLine();
        System.out.println("Insert file extensions:");
        String extension = sc.nextLine();
        File dir = new File(path);

        ArrayList<String> result = fulfillFiles(dir);
        ArrayList<String> fin = new ArrayList<>();

        ArrayList<Future<String>> results =
                new ArrayList<Future<String>>();

        ExecutorService exec = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++)
            results.add(exec.submit(new TaskWithResult(extension, fin, result)));
        for(Future<String> fs : results)
            try {
                // Вызов get() блокируется до завершения;:
                System.out.println(fs.get());
            } catch(InterruptedException e) {
                System.out.println(e);
                return;
            } catch(ExecutionException e) {
                System.out.println(e);
            } finally {
                exec.shutdown();
            }

    }

    private static ArrayList<String> fulfillFiles(File dir){
        ArrayList<String> result = new ArrayList<>();
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

class TaskWithResult implements Callable<String> {

    public String extension;

    public ArrayList<String> fin;

    public ArrayList<String> res;

    TaskWithResult(String extension, ArrayList<String> fin, ArrayList<String> res) {
        this.extension = extension;
        this.fin = fin;
        this.res = res;
    }

    ArrayList<String> getExtFiles(String extension, List<String> files) {
        synchronized (files) {
            for (String fileString : files) {
                String ext = FilenameUtils.getExtension(fileString);
                if (extension.equals(ext)) {
                    fin.add(fileString);
                }
            }
            return fin;
        }
    }

    @Override
    public String call() throws Exception {
        return getExtFiles(extension, res).toString();
    }
}
