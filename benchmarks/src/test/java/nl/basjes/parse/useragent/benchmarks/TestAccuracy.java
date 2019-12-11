package nl.basjes.parse.useragent.benchmarks;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

import java.io.*;

/**
 * Created by junjie on 10/12/19.
 */
public class TestAccuracy {

    public void test(String fileName) {
        UserAgentAnalyzer analyzer = UserAgentAnalyzer.newBuilder().build();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName)));
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] array = line.split(",");
                UserAgent ua = analyzer.parse(array[0]);
                String brand = ua.getValue(UserAgent.DEVICE_BRAND);

                if (!brand.equals(array[1])) {
                    System.out.println("ua: " + array[0] + ", expected: " + array[1] + ", get: " + brand);
                }
                else {
                    ++count;
                }
            }

            System.out.println("accuracy: " + (double)count / 2 + "%");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new TestAccuracy().test(args[0]);
    }
}
