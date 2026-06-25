import java.io.*;

public class Worker {

    public static void main(String[] args) throws Exception {

        BufferedReader in =
                new BufferedReader(new InputStreamReader(System.in));

        BufferedWriter out =
                new BufferedWriter(new OutputStreamWriter(System.out));

        String line;

        while ((line = in.readLine()) != null) {

            String response =
                    "[worker pid=" + ProcessHandle.current().pid() +
                    "] " + line.toUpperCase();

            out.write(response);
            out.newLine();
            out.flush();
        }
    }
}
