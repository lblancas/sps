package com.smartpayment.nxt.hierarchy.parameters;
import com.smartpayment.nxt.client.NxtMsaLoggerConfig;
import com.smartpayment.nxt.config.GenericJwtFilterConfig;
import com.smartpayment.nxt.messaging.ResponseMessageComponent;
import com.smartpayment.nxt.messaging.service.impl.NxtMsaMessagingService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import com.smartpayment.nxt.config.GenericDataSourceConfig;
import com.smartpayment.nxt.logging.service.impl.LoggingServiceImpl;
@Import({GenericDataSourceConfig.class,
    NxtMsaLoggerConfig.class,
    ResponseMessageComponent.class,
    GenericJwtFilterConfig.class,
    NxtMsaMessagingService.class
    , LoggingServiceImpl.class})
@SpringBootApplication(
    exclude = { JdbcRepositoriesAutoConfiguration.class }
)
@EnableAspectJAutoProxy
@EnableAsync
public class HierarchyApplication {

    public static void main(String[] args) {
        SpringApplication.run(HierarchyApplication.class, args);
    }

}
