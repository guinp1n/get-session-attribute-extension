package com.hivemq.extensions.helloworld;

import com.hivemq.extension.sdk.api.ExtensionMain;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.events.EventRegistry;
import com.hivemq.extension.sdk.api.parameter.*;
import com.hivemq.extension.sdk.api.services.EnterpriseServices;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.intializer.InitializerRegistry;
import com.hivemq.extension.sdk.api.services.session.SessionAttributeStore;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Random;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * This is the main class of the enterprise extension,
 * which is instantiated either during the HiveMQ start up process (if extension is enabled)
 * or when HiveMQ is already started by enabling the extension.
 *
 * @author Florian Limpöck
 * @since 4.4.0
 */
public class HelloWorldEnterpriseMain implements ExtensionMain {

    private static final @NotNull Logger log = LoggerFactory.getLogger(HelloWorldEnterpriseMain.class);

    @Override
    public void extensionStart(
            final @NotNull ExtensionStartInput extensionStartInput,
            final @NotNull ExtensionStartOutput extensionStartOutput) {

        try {
            addClientLifecycleEventListener();
            addPublishModifier();

            //just an enterprise usage example.
            final SessionAttributeStore sessionAttributeStore = EnterpriseServices.sessionAttributeStore();

            sessionAttributeStore.put("subscriber-0", "short-key", ByteBuffer.wrap("short-ascii-value".getBytes(UTF_8)));
            sessionAttributeStore.put("subscriber-0", "long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-long-key-",
                    ByteBuffer.wrap("long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-long-ascii-value-".getBytes(UTF_8)));
            sessionAttributeStore.put("subscriber-0", "another-short-key", ByteBuffer.wrap(getRandomBytes(500)));
            sessionAttributeStore.put("subscriber-0", "short-key-the-third", ByteBuffer.wrap(getRandomBytes(15)));
            for (int i = 0; i < 100; i++) {
                if (i % 2 == 0) {
                    sessionAttributeStore.put("subscriber-0", "short-key-" + i, ByteBuffer.wrap(getRandomBytes(15 * i)));
                } else {
                    sessionAttributeStore.put("subscriber-0", "short-key-" + i, ByteBuffer.wrap(RandomStringUtils.randomAlphanumeric(10 * i).getBytes(UTF_8)));
                }
            }

            final ExtensionInformation extensionInformation = extensionStartInput.getExtensionInformation();
            log.info("Started " + extensionInformation.getName() + ":" + extensionInformation.getVersion());

        } catch (final Exception e) {
            log.error("Exception thrown at extension start: ", e);
        }
    }

    private byte @NotNull [] getRandomBytes(final int size) {
        final byte[] bytes = new byte[size];
        final Random random = new Random();
        random.nextBytes(bytes);
        return bytes;
    }

    @Override
    public void extensionStop(
            final @NotNull ExtensionStopInput extensionStopInput,
            final @NotNull ExtensionStopOutput extensionStopOutput) {

        final ExtensionInformation extensionInformation = extensionStopInput.getExtensionInformation();
        log.info("Stopped " + extensionInformation.getName() + ":" + extensionInformation.getVersion());
    }

    private void addClientLifecycleEventListener() {
        final EventRegistry eventRegistry = Services.eventRegistry();

        final HelloWorldListener helloWorldListener = new HelloWorldListener();

        eventRegistry.setClientLifecycleEventListener(input -> helloWorldListener);
    }

    private void addPublishModifier() {
        final InitializerRegistry initializerRegistry = Services.initializerRegistry();

        final HelloWorldInterceptor helloWorldInterceptor = new HelloWorldInterceptor();

        initializerRegistry.setClientInitializer(
                (initializerInput, clientContext) -> clientContext.addPublishInboundInterceptor(helloWorldInterceptor));
    }
}