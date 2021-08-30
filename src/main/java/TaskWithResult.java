import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TaskWithResult implements Callable<List<String>> {

    private String extension;

    private volatile List<String> res;

    public volatile List<String> fin;

    TaskWithResult(String extension, List<String> res) {
        this.extension = extension;
        this.res = res;
        fin = getExtFiles(this.extension, this.res);
    }

    List<String> getExtFiles(String extension, List<String> files) {
            List<String> resFiles = new ArrayList<>();
            for (String fileString : files) {
                String ext = FilenameUtils.getExtension(fileString);
                if (extension.equals(ext)) {
                    resFiles.add(fileString);
                    Thread.yield();
                }
            }
            return resFiles;
    }

    @Override
    public List<String> call() throws Exception {
        return fin;
    }

}
