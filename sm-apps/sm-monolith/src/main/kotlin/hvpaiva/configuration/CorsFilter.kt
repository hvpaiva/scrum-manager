package hvpaiva.configuration

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class CorsFilter : Filter {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest?, res: ServletResponse?, chain: FilterChain?) {
        val response = res as HttpServletResponse
        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
        response.setHeader("Access-Control-Allow-Credentials", "true")
        response.setHeader("Access-Control-Max-Age", "3600")
        response.setHeader(
            "Access-Control-Allow-Headers",
            "X-Requested-With, Authorization, Content-Type, Authorization, Origin, Version"
        )
        response.setHeader(
            "Access-Control-Expose-Headers",
            "X-Requested-With, Authorization, Content-Type, Authorization, Origin"
        )

        val request = req as HttpServletRequest
        if (request.method != "OPTIONS") {
            chain?.doFilter(req, res)
        } else {
            // Just OK an OPTIONS request.
        }
    }

    override fun destroy() {
        // Nothing to do.
    }

    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig?) {
        // Nothing to do.
    }
}