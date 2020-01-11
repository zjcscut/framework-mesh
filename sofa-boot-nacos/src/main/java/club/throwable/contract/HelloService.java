package club.throwable.contract;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2019/12/31 14:35
 */
@Path("rest")
public interface HelloService {

    @GET
    @Path(value = "/sayHello/{name}")
    String sayHello(@PathParam("name") String name);
}
