package cn.throwx.cb;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FuckingHexo {

    public static void main(String[] args) throws Exception {
//         find("I:\\blog-butterfly");
        System.out.println(powerOfTwoF(9));
    }

    static float powerOfTwoF(int n) {
        return Float.intBitsToFloat(((n + 127) <<
                (24 - 1)) &0x7F800000);
    }

    static void find(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (Objects.nonNull(files)) {
                for (File f : files) {
                    find(f.getAbsolutePath());
                }
            }
        } else if (file.isFile()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String line;
            while (null != (line = reader.readLine())) {
                if (line.contains("cloudTags")) {
                    System.out.println(file.getAbsolutePath());
                }
            }
        }
    }
}
