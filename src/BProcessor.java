
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BProcessor implements Runnable {

    public int processorCount = 0;
    public int discardedCount = 0;

    private final ArrayBlockingQueue<String> inputQueue;
    private final ArrayBlockingQueue<String> outputQueue;

    private volatile boolean recordToProcess = true;

    public BProcessor(ArrayBlockingQueue inputQueue, ArrayBlockingQueue outputQueue) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    @Override
    public void run() {
        processQueue();
    }

    public void processQueue() {

        try {
            while (recordToProcess) {

                String data = inputQueue.poll(10, TimeUnit.MILLISECONDS);
                if (data == null) continue;
                String[] columns = data.split(",");
                String pincode = extractPincode(columns);

                if (isValid(pincode)) {
                    String processedData = data + "," + pincode;
                    outputQueue.put(processedData);
                    processorCount++;
                }
                else {
                    discardedCount++;
                }

                // if ((processorCount >= 1000 && processorCount % 1000 == 0)) {
                //     System.out.println("Record processed :: " + processorCount);
                // }

                // if ((discardedCount >= 1000 && discardedCount % 1000 == 0)) {
                //     System.out.println("Record discarded :: " + discardedCount);
                // }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Error in processing records", e);
        }
    }

    private String extractPincode(String[] columns) {
        for (String column : columns) {
            Pattern pattern = Pattern.compile("\\d{6}");
            Matcher matcher = pattern.matcher(column);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return null;
    }

    private boolean isValid(String pincode) {
        return pincode != null;
    }

    public void close() {
        recordToProcess = false;
    }

    public void printProcessorCount(){
        System.out.println("Records processed by the processor are : " + processorCount);
        System.out.println("Records discarded by the processor are : " + discardedCount);
    }

    // public int getProcessorCount() {
    //     return processorCount;
    // }

    // public int getDiscardedCount() {
    //     return discardedCount;
    // }
}
