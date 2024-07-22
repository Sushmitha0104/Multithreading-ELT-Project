import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

public class BReader implements Runnable {

    private final ArrayBlockingQueue<String> queue;
    private final BufferedReader bufferedReader;

    private int readerCount = 0;
    private boolean readingDone = false;

    public BReader(String filename, ArrayBlockingQueue<String> queue) throws IOException {
        this.bufferedReader = new BufferedReader(new FileReader(filename));
        this.queue = queue;
    }

    public String readHeader() throws IOException {
        return bufferedReader.readLine(); // Read the header line
    }

    public void readAndEnqueueRecords() {
        String line;
        try {
            while (!isReadingDone()) {
                if ((line = bufferedReader.readLine()) != null) {
                    queue.put(line);
                    readerCount++;

                    // if (readerCount >= 1000 && readerCount % 1000 == 0) {
                    //     System.out.println("Record read :: " + readerCount);
                    // }
                } else {
                    break;
                }
            }
            if (bufferedReader != null) bufferedReader.close();
            System.out.println("Reading done");
        } catch (Exception e) {
            throw new RuntimeException("Error happened while reading records", e);
        }

        readingDone = true;
    }

    public boolean isReadingDone() {
        return readingDone;
    }

    public void printReaderCount(){
        System.out.println("Total records read ny Reader : " + readerCount);
    }

    // public int getReaderCount() {
    //     return readerCount;
    // }

    @Override
    public void run() {
        readAndEnqueueRecords();
    }
}
