package club.throwable.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

/**
 * @author throwable
 * @version v1.0
 * @description 调用C的动态链接类库中的printf方法
 * @since 2020/1/10 14:59
 */
public class JnaHelloWorld {

    public interface CLibrary extends Library {

        CLibrary X = (CLibrary) Native.load(Platform.isWindows() ? "msvcrt" : "c", CLibrary.class);

        void printf(String format, Object... args);
    }

    public static void main(String[] args) throws Exception {
        CLibrary.X.printf("Hello World!\n");
        CLibrary.X.printf("Argument[%d]:%s\n", 0, "throwable");
    }
}
