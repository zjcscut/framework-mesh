package cn.vlts.tomcat;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author throwable
 * @version v1
 * @description
 * @since 2022/10/7 21:43
 */
public class VirtualThreadHandleServlet extends HttpServlet {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Thread thread = Thread.currentThread();
        System.out.printf("service by thread ==> %s, is virtual ==> %s, carrier thread ==> %s\n",
                thread.getName(), thread.isVirtual(), getCurrentCarrierThreadName(thread));
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setHeader("Content-Type", "application/json");
        String content = "{\"time\":" + "\"" + LocalDateTime.now().format(FORMATTER) + "\"}";
        resp.getWriter().write(content);
    }

    private static String getCurrentCarrierThreadName(Thread currentThread) {
        if (currentThread.isVirtual()) {
            try {
                MethodHandle methodHandle = MethodHandles.privateLookupIn(Thread.class, MethodHandles.lookup())
                        .findStatic(Thread.class, "currentCarrierThread", MethodType.methodType(Thread.class));
                Thread carrierThread = (Thread) methodHandle.invoke();
                return carrierThread.getName();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return "UNKNOWN";
    }
}
