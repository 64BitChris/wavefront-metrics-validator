package com.yammer.metrics.pipeline;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.EvictingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.config.IntegrationConverter;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLfSerializer;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.codahale.metrics.MetricRegistry.name;

@SpringBootApplication
@EnableIntegration
@IntegrationComponentScan
@RestController
public class MetricsPipelineValidatorApplication {

    @Autowired
    MetricsValidator metricsValidator;

    @Autowired
    MetricRegistry metricRegistry;

    final EvictingQueue<String> malformedMetrics = EvictingQueue.create(32);

    @Bean
    @IntegrationConverter
    public Converter<byte[], String> byteArrayToStringConverter() {
        return new ByteArrayToStringConverter();
    }

    @Bean
    public MessageChannel incomingTcpChannel() {
        /** Use Pub Sub channel so that we can process messages asynchronously */
        final PublishSubscribeChannel messageChannel = new PublishSubscribeChannel(Executors.newScheduledThreadPool(32));
        messageChannel.setDatatypes(String.class);
        return messageChannel;
    }

    @Bean
    public TcpInboundGateway tcpInBoundGateway(AbstractServerConnectionFactory connectionFactory) {
        final TcpInboundGateway result = new TcpInboundGateway();
        result.setConnectionFactory(connectionFactory);
        // Calling the function here somehow gives it the proper channel name.
        result.setRequestChannel(incomingTcpChannel());
        return result;
    }

    @Bean
    public Meter invalidMetricMeter() {
        return metricRegistry.meter(name(MetricsPipelineValidatorApplication.class, "invalid-metrics"));
    }

    @Bean
    public Meter validMetricMeter() {
        return metricRegistry.meter(name(MetricsPipelineValidatorApplication.class, "valid-metrics"));
    }

    @ServiceActivator(inputChannel = "incomingTcpChannel")
    public void validatorProcessor(String message) {
        final boolean metricIsValid = metricsValidator.validate(message);

        if (metricIsValid) {
            validMetricMeter().mark();
        } else {
            invalidMetricMeter().mark();
            synchronized (this) {
                malformedMetrics.add(message);
            }
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "bad-metrics")
    public List<String> badMetrics() {
        synchronized (this) {
            return malformedMetrics.parallelStream().collect(Collectors.toList());
        }
    }

    @Bean
    public AbstractServerConnectionFactory connectionFactory() {
        final TcpNioServerConnectionFactory factory = new TcpNioServerConnectionFactory(2878);
        factory.setDeserializer(new ByteArrayLfSerializer());
        factory.setSingleUse(false);
        return factory;
    }

    public static void main(String[] args) {
        SpringApplication.run(MetricsPipelineValidatorApplication.class, args);
    }
}
