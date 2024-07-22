import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BWriter implements Runnable {

    private final String outputFile;
    private final ArrayBlockingQueue<String> queue;
    private final String header;

    private final BufferedWriter bw;

    public int writerCount = 0;
    private volatile boolean recordsToWrite = true;

    public BWriter(String outputFile, ArrayBlockingQueue<String> queue, String header) throws IOException {
        this.outputFile = outputFile;
        this.queue = queue;
        this.header = header;
        this.bw = new BufferedWriter(new FileWriter(outputFile, false));
        this.bw.write(header + ",Pincode");
    }

    @Override
    public void run() {
        writeRecords();
    }

    public void writeRecords() {

        try {
            while(recordsToWrite) {
                String data = queue.poll(10, TimeUnit.MILLISECONDS);
                if (data == null) continue;
                bw.newLine();
                bw.write(data);
                writerCount++;
                // if (writerCount >= 1000 && writerCount % 1000 == 0) {
                //     System.out.println("Records written :: " + writerCount);
                // }
            }
            if (bw != null) bw.close();
        } catch (Exception e) {
            throw new RuntimeException("Error in writing records", e);
        }
    }

    public void printWriterCount(){
        System.out.println("Records written by the writer are : " + writerCount);
    }

    public void close() {
        recordsToWrite = false;
    }
}
