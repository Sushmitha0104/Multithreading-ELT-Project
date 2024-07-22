import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

public class BCoordinator {

    private static final String INPUT_FILE = ""; 
    private static final String OUTPUT_FILE = "";

    public static void main(String[] args) {
        new BCoordinator().processDataFile(INPUT_FILE, OUTPUT_FILE);
    }

    private void processDataFile(String inputFile, String outputFile) {

        ArrayBlockingQueue<String> rawDataQueue = new ArrayBlockingQueue(10);
        ArrayBlockingQueue<String> processedDataQueue = new ArrayBlockingQueue(10);
        
        long start = System.currentTimeMillis();

        try {
            BReader reader = new BReader(inputFile, rawDataQueue);
            String header = reader.readHeader();

            BProcessor processor = new BProcessor(rawDataQueue, processedDataQueue);
            BWriter writer = new BWriter(outputFile, processedDataQueue, header);

            Thread readerThread = new Thread(reader);
            Thread processorThread = new Thread(processor);
            Thread writerThread = new Thread(writer);

            readerThread.start();
            processorThread.start();
            writerThread.start();

            while (true) {
                if (!reader.isReadingDone()) {
                    sleep(10);
                    continue;
                }

                if (rawDataQueue.peek() != null) {
                    sleep(10);
                    continue;
                }

                if (processedDataQueue.peek() != null) {
                    sleep(10);
                    continue;
                }

                break;
            }

            processor.close();
            writer.close();

            // Print the stats
            reader.printReaderCount();
            processor.printProcessorCount();
            writer.printWriterCount();

        } catch (IOException e) {
            e.printStackTrace();
        }

        long millis = System.currentTimeMillis() - start;
        System.out.println("Time taken in millis :: " + millis);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Nothing to do
        }
    }
}
