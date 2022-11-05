package hvpaiva.configuration

import org.axonframework.commandhandling.CommandBus
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

@Configuration
class AxonConfiguration {
    @Autowired
    fun registerInterceptor(commandBus: CommandBus) {
        commandBus.registerDispatchInterceptor(BeanValidationInterceptor())
    }
}